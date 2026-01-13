package com.icelurenote.sotfap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icelurenote.sotfap.data.model.BaitType
import com.icelurenote.sotfap.data.model.FishingEntry
import com.icelurenote.sotfap.data.model.Result
import com.icelurenote.sotfap.data.repository.FishingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class BaitStats(
    val baitType: BaitType,
    val usageCount: Int,
    val averageResult: Double,
    val entries: List<FishingEntry>
)

class BaitsViewModel(repository: FishingRepository) : ViewModel() {
    
    val baitStatistics: StateFlow<List<BaitStats>> = repository.getAllEntries()
        .map { entries ->
            entries.groupBy { it.baitType }
                .map { (baitType, baitEntries) ->
                    val avgResult = baitEntries.map { 
                        when (it.result) {
                            Result.LOW -> 1.0
                            Result.MEDIUM -> 2.0
                            Result.GOOD -> 3.0
                        }
                    }.average()
                    
                    BaitStats(
                        baitType = baitType,
                        usageCount = baitEntries.size,
                        averageResult = avgResult,
                        entries = baitEntries
                    )
                }
                .sortedByDescending { it.averageResult }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

