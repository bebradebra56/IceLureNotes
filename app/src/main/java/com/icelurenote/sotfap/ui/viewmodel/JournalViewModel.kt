package com.icelurenote.sotfap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icelurenote.sotfap.data.model.FishingEntry
import com.icelurenote.sotfap.data.model.FishType
import com.icelurenote.sotfap.data.model.Result
import com.icelurenote.sotfap.data.repository.FishingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class JournalViewModel(private val repository: FishingRepository) : ViewModel() {
    
    private val _allEntries = repository.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    private val _selectedFishFilter = MutableStateFlow<FishType?>(null)
    private val _selectedResultFilter = MutableStateFlow<Result?>(null)
    private val _searchQuery = MutableStateFlow("")
    
    val filteredEntries: StateFlow<List<FishingEntry>> = combine(
        _allEntries,
        _selectedFishFilter,
        _selectedResultFilter,
        _searchQuery
    ) { entries, fishFilter, resultFilter, searchQuery ->
        entries.filter { entry ->
            val matchesFish = fishFilter == null || entry.targetFish == fishFilter
            val matchesResult = resultFilter == null || entry.result == resultFilter
            val matchesSearch = searchQuery.isBlank() || 
                entry.baitType.displayName.contains(searchQuery, ignoreCase = true) ||
                entry.notes.contains(searchQuery, ignoreCase = true)
            matchesFish && matchesResult && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val selectedFishFilter: StateFlow<FishType?> = _selectedFishFilter.asStateFlow()
    val selectedResultFilter: StateFlow<Result?> = _selectedResultFilter.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    fun setFishFilter(fish: FishType?) {
        _selectedFishFilter.value = fish
    }
    
    fun setResultFilter(result: Result?) {
        _selectedResultFilter.value = result
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun deleteEntry(entry: FishingEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }
}

