package com.icelurenote.sotfap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.icelurenote.sotfap.data.preferences.PreferencesManager
import com.icelurenote.sotfap.data.repository.FishingRepository

class ViewModelFactory(
    private val repository: FishingRepository,
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(JournalViewModel::class.java) -> {
                JournalViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddEntryViewModel::class.java) -> {
                AddEntryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(BaitsViewModel::class.java) -> {
                BaitsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ResultsViewModel::class.java) -> {
                ResultsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(preferencesManager, repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

