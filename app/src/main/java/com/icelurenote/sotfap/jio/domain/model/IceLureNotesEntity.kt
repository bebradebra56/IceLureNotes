package com.icelurenote.sotfap.jio.domain.model

import com.google.gson.annotations.SerializedName


data class IceLureNotesEntity (
    @SerializedName("ok")
    val iceLureNotesOk: String,
    @SerializedName("url")
    val iceLureNotesUrl: String,
    @SerializedName("expires")
    val iceLureNotesExpires: Long,
)