package com.icelurenote.sotfap.jio.data.utils

import android.util.Log
import com.icelurenote.sotfap.jio.presentation.app.IceLureNotesApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class IceLureNotesPushToken {

    suspend fun iceLureNotesGetToken(
        iceLureNotesMaxAttempts: Int = 3,
        iceLureNotesDelayMs: Long = 1500
    ): String {

        repeat(iceLureNotesMaxAttempts - 1) {
            try {
                val iceLureNotesToken = FirebaseMessaging.getInstance().token.await()
                return iceLureNotesToken
            } catch (e: Exception) {
                Log.e(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(iceLureNotesDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}