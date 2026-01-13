package com.icelurenote.sotfap.jio.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class IceLureNotesDataStore : ViewModel(){
    val iceLureNotesViList: MutableList<IceLureNotesVi> = mutableListOf()
    var iceLureNotesIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var iceLureNotesContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var iceLureNotesView: IceLureNotesVi

}