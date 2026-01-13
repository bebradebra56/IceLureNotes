package com.icelurenote.sotfap.utils

import com.icelurenote.sotfap.data.model.UnitPreference

object DepthConverter {
    private const val METERS_TO_FEET = 3.28084f
    
    /**
     * Converts depth to display value based on unit preference
     * Database always stores in meters
     */
    fun toDisplayValue(depthInMeters: Float, unit: UnitPreference): Float {
        return when (unit) {
            UnitPreference.METERS -> depthInMeters
            UnitPreference.FEET -> depthInMeters * METERS_TO_FEET
        }
    }
    
    /**
     * Converts user input to meters for database storage
     */
    fun toMeters(value: Float, unit: UnitPreference): Float {
        return when (unit) {
            UnitPreference.METERS -> value
            UnitPreference.FEET -> value / METERS_TO_FEET
        }
    }
    
    /**
     * Formats depth for display with unit
     */
    fun formatDepth(depthInMeters: Float, unit: UnitPreference): String {
        val value = toDisplayValue(depthInMeters, unit)
        val unitText = when (unit) {
            UnitPreference.METERS -> "m"
            UnitPreference.FEET -> "ft"
        }
        return "%.1f %s".format(value, unitText)
    }
}

