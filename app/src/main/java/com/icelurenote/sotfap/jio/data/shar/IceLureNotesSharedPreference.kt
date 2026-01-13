package com.icelurenote.sotfap.jio.data.shar

import android.content.Context
import androidx.core.content.edit

class IceLureNotesSharedPreference(context: Context) {
    private val iceLureNotesPrefs = context.getSharedPreferences("iceLureNotesSharedPrefsAb", Context.MODE_PRIVATE)

    var iceLureNotesSavedUrl: String
        get() = iceLureNotesPrefs.getString(ICE_LURE_NOTES_SAVED_URL, "") ?: ""
        set(value) = iceLureNotesPrefs.edit { putString(ICE_LURE_NOTES_SAVED_URL, value) }

    var iceLureNotesExpired : Long
        get() = iceLureNotesPrefs.getLong(ICE_LURE_NOTES_EXPIRED, 0L)
        set(value) = iceLureNotesPrefs.edit { putLong(ICE_LURE_NOTES_EXPIRED, value) }

    var iceLureNotesAppState: Int
        get() = iceLureNotesPrefs.getInt(ICE_LURE_NOTES_APPLICATION_STATE, 0)
        set(value) = iceLureNotesPrefs.edit { putInt(ICE_LURE_NOTES_APPLICATION_STATE, value) }

    var iceLureNotesNotificationRequest: Long
        get() = iceLureNotesPrefs.getLong(ICE_LURE_NOTES_NOTIFICAITON_REQUEST, 0L)
        set(value) = iceLureNotesPrefs.edit { putLong(ICE_LURE_NOTES_NOTIFICAITON_REQUEST, value) }

    var iceLureNotesNotificationRequestedBefore: Boolean
        get() = iceLureNotesPrefs.getBoolean(ICE_LURE_NOTES_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = iceLureNotesPrefs.edit { putBoolean(
            ICE_LURE_NOTES_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val ICE_LURE_NOTES_SAVED_URL = "iceLureNotesSavedUrl"
        private const val ICE_LURE_NOTES_EXPIRED = "iceLureNotesExpired"
        private const val ICE_LURE_NOTES_APPLICATION_STATE = "iceLureNotesApplicationState"
        private const val ICE_LURE_NOTES_NOTIFICAITON_REQUEST = "iceLureNotesNotificationRequest"
        private const val ICE_LURE_NOTES_NOTIFICATION_REQUEST_BEFORE = "iceLureNotesNotificationRequestedBefore"
    }
}