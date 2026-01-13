package com.icelurenote.sotfap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icelurenote.sotfap.data.model.*
import com.icelurenote.sotfap.data.repository.FishingRepository
import com.icelurenote.sotfap.utils.DepthConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddEntryState(
    val baitType: BaitType = BaitType.MORMYSHKA,
    val baitColor: BaitColor = BaitColor.SILVER,
    val targetFish: FishType = FishType.PERCH,
    val depth: String = "",
    val result: Result = Result.MEDIUM,
    val notes: String = ""
)

class AddEntryViewModel(private val repository: FishingRepository) : ViewModel() {
    
    private val _state = MutableStateFlow(AddEntryState())
    val state: StateFlow<AddEntryState> = _state.asStateFlow()
    
    private val _editingEntryId = MutableStateFlow<Long?>(null)
    
    fun setBaitType(type: BaitType) {
        _state.value = _state.value.copy(baitType = type)
    }
    
    fun setBaitColor(color: BaitColor) {
        _state.value = _state.value.copy(baitColor = color)
    }
    
    fun setTargetFish(fish: FishType) {
        _state.value = _state.value.copy(targetFish = fish)
    }
    
    fun setDepth(depth: String) {
        _state.value = _state.value.copy(depth = depth)
    }
    
    fun setResult(result: Result) {
        _state.value = _state.value.copy(result = result)
    }
    
    fun setNotes(notes: String) {
        _state.value = _state.value.copy(notes = notes)
    }
    
    fun loadEntry(entryId: Long, unitPreference: UnitPreference) {
        viewModelScope.launch {
            val entry = repository.getEntryById(entryId)
            if (entry != null) {
                _editingEntryId.value = entryId
                val displayDepth = DepthConverter.toDisplayValue(entry.depth, unitPreference)
                _state.value = AddEntryState(
                    baitType = entry.baitType,
                    baitColor = entry.baitColor,
                    targetFish = entry.targetFish,
                    depth = displayDepth.toString(),
                    result = entry.result,
                    notes = entry.notes
                )
            }
        }
    }
    
    fun saveEntry(unitPreference: UnitPreference, onSuccess: () -> Unit, onError: () -> Unit) {
        val depthValue = _state.value.depth.toFloatOrNull()
        if (depthValue == null || depthValue <= 0) {
            onError()
            return
        }
        
        // Convert to meters for database storage
        val depthInMeters = DepthConverter.toMeters(depthValue, unitPreference)
        
        viewModelScope.launch {
            try {
                val entry = FishingEntry(
                    id = _editingEntryId.value ?: 0,
                    baitType = _state.value.baitType,
                    baitColor = _state.value.baitColor,
                    targetFish = _state.value.targetFish,
                    depth = depthInMeters,
                    result = _state.value.result,
                    notes = _state.value.notes
                )
                
                if (_editingEntryId.value != null) {
                    repository.updateEntry(entry)
                } else {
                    repository.insertEntry(entry)
                }
                
                resetState()
                onSuccess()
            } catch (e: Exception) {
                onError()
            }
        }
    }
    
    fun resetState() {
        _state.value = AddEntryState()
        _editingEntryId.value = null
    }
}

