package com.icelurenote.sotfap.jio.domain.model

import com.google.gson.annotations.SerializedName


private const val ICE_LURE_NOTES_A = "com.icelurenote.sotfap"
private const val ICE_LURE_NOTES_B = "icelurenotes"
data class IceLureNotesParam (
    @SerializedName("af_id")
    val iceLureNotesAfId: String,
    @SerializedName("bundle_id")
    val iceLureNotesBundleId: String = ICE_LURE_NOTES_A,
    @SerializedName("os")
    val iceLureNotesOs: String = "Android",
    @SerializedName("store_id")
    val iceLureNotesStoreId: String = ICE_LURE_NOTES_A,
    @SerializedName("locale")
    val iceLureNotesLocale: String,
    @SerializedName("push_token")
    val iceLureNotesPushToken: String,
    @SerializedName("firebase_project_id")
    val iceLureNotesFirebaseProjectId: String = ICE_LURE_NOTES_B,

    )