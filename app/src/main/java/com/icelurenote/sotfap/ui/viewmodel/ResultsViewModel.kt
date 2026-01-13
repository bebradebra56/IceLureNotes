package com.icelurenote.sotfap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icelurenote.sotfap.data.model.BaitType
import com.icelurenote.sotfap.data.model.FishType
import com.icelurenote.sotfap.data.model.Result
import com.icelurenote.sotfap.data.repository.FishingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ResultsSummary(
    val mostEffectiveBait: BaitType?,
    val bestDepth: Float?,
    val mostCommonFish: FishType?,
    val totalEntries: Int,
    val baitsTested: Int,
    val successfulSessions: Int
)

class ResultsViewModel(repository: FishingRepository) : ViewModel() {
    
    val resultsSummary: StateFlow<ResultsSummary> = repository.getAllEntries()
        .map { entries ->
            if (entries.isEmpty()) {
                ResultsSummary(null, null, null, 0, 0, 0)
            } else {
                val mostEffectiveBait = entries
                    .groupBy { it.baitType }
                    .maxByOrNull { (_, baitEntries) ->
                        baitEntries.count { it.result == Result.GOOD }
                    }?.key
                
                val bestDepth = entries
                    .groupBy { it.depth }
                    .maxByOrNull { (_, depthEntries) ->
                        depthEntries.count { it.result == Result.GOOD }
                    }?.key
                
                val mostCommonFish = entries
                    .groupBy { it.targetFish }
                    .maxByOrNull { it.value.size }?.key
                
                val successfulSessions = entries.count { it.result == Result.GOOD }
                
                ResultsSummary(
                    mostEffectiveBait = mostEffectiveBait,
                    bestDepth = bestDepth,
                    mostCommonFish = mostCommonFish,
                    totalEntries = entries.size,
                    baitsTested = entries.map { it.baitType }.distinct().size,
                    successfulSessions = successfulSessions
                )
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ResultsSummary(null, null, null, 0, 0, 0)
        )
}

