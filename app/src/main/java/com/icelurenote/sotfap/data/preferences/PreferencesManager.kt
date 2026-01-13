package com.icelurenote.sotfap.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.icelurenote.sotfap.data.model.UnitPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    
    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val UNIT_PREFERENCE = stringPreferencesKey("unit_preference")
    }
    
    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }
    
    val unitPreference: Flow<UnitPreference> = context.dataStore.data
        .map { preferences ->
            val value = preferences[UNIT_PREFERENCE] ?: UnitPreference.METERS.name
            UnitPreference.valueOf(value)
        }
    
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }
    
    suspend fun setUnitPreference(unit: UnitPreference) {
        context.dataStore.edit { preferences ->
            preferences[UNIT_PREFERENCE] = unit.name
        }
    }
}

