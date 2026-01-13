package com.icelurenote.sotfap.jio.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.icelurenote.sotfap.jio.presentation.app.IceLureNotesApplication

class IceLureNotesPushHandler {
    fun iceLureNotesHandlePush(extras: Bundle?) {
        Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = iceLureNotesBundleToMap(extras)
            Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    IceLureNotesApplication.ICE_LURE_NOTES_FB_LI = map["url"]
                    Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "Push data no!")
        }
    }

    private fun iceLureNotesBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}