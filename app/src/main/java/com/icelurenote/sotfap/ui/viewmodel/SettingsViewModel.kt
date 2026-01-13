package com.icelurenote.sotfap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icelurenote.sotfap.data.model.UnitPreference
import com.icelurenote.sotfap.data.preferences.PreferencesManager
import com.icelurenote.sotfap.data.repository.FishingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val repository: FishingRepository
) : ViewModel() {
    
    val unitPreference: StateFlow<UnitPreference> = preferencesManager.unitPreference
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UnitPreference.METERS)
    
    fun setUnitPreference(unit: UnitPreference) {
        viewModelScope.launch {
            preferencesManager.setUnitPreference(unit)
        }
    }
    
    fun resetAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteAllEntries()
            onComplete()
        }
    }
}

