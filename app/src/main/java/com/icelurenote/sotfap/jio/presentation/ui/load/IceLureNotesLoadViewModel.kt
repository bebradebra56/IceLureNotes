package com.icelurenote.sotfap.jio.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icelurenote.sotfap.jio.data.shar.IceLureNotesSharedPreference
import com.icelurenote.sotfap.jio.data.utils.IceLureNotesSystemService
import com.icelurenote.sotfap.jio.domain.usecases.IceLureNotesGetAllUseCase
import com.icelurenote.sotfap.jio.presentation.app.IceLureNotesAppsFlyerState
import com.icelurenote.sotfap.jio.presentation.app.IceLureNotesApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IceLureNotesLoadViewModel(
    private val iceLureNotesGetAllUseCase: IceLureNotesGetAllUseCase,
    private val iceLureNotesSharedPreference: IceLureNotesSharedPreference,
    private val iceLureNotesSystemService: IceLureNotesSystemService
) : ViewModel() {

    private val _iceLureNotesHomeScreenState: MutableStateFlow<IceLureNotesHomeScreenState> =
        MutableStateFlow(IceLureNotesHomeScreenState.IceLureNotesLoading)
    val iceLureNotesHomeScreenState = _iceLureNotesHomeScreenState.asStateFlow()

    private var iceLureNotesGetApps = false


    init {
        viewModelScope.launch {
            when (iceLureNotesSharedPreference.iceLureNotesAppState) {
                0 -> {
                    if (iceLureNotesSystemService.iceLureNotesIsOnline()) {
                        IceLureNotesApplication.iceLureNotesConversionFlow.collect {
                            when(it) {
                                IceLureNotesAppsFlyerState.IceLureNotesDefault -> {}
                                IceLureNotesAppsFlyerState.IceLureNotesError -> {
                                    iceLureNotesSharedPreference.iceLureNotesAppState = 2
                                    _iceLureNotesHomeScreenState.value =
                                        IceLureNotesHomeScreenState.IceLureNotesError
                                    iceLureNotesGetApps = true
                                }
                                is IceLureNotesAppsFlyerState.IceLureNotesSuccess -> {
                                    if (!iceLureNotesGetApps) {
                                        iceLureNotesGetData(it.iceLureNotesData)
                                        iceLureNotesGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _iceLureNotesHomeScreenState.value =
                            IceLureNotesHomeScreenState.IceLureNotesNotInternet
                    }
                }
                1 -> {
                    if (iceLureNotesSystemService.iceLureNotesIsOnline()) {
                        if (IceLureNotesApplication.ICE_LURE_NOTES_FB_LI != null) {
                            _iceLureNotesHomeScreenState.value =
                                IceLureNotesHomeScreenState.IceLureNotesSuccess(
                                    IceLureNotesApplication.ICE_LURE_NOTES_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > iceLureNotesSharedPreference.iceLureNotesExpired) {
                            Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "Current time more then expired, repeat request")
                            IceLureNotesApplication.iceLureNotesConversionFlow.collect {
                                when(it) {
                                    IceLureNotesAppsFlyerState.IceLureNotesDefault -> {}
                                    IceLureNotesAppsFlyerState.IceLureNotesError -> {
                                        _iceLureNotesHomeScreenState.value =
                                            IceLureNotesHomeScreenState.IceLureNotesSuccess(
                                                iceLureNotesSharedPreference.iceLureNotesSavedUrl
                                            )
                                        iceLureNotesGetApps = true
                                    }
                                    is IceLureNotesAppsFlyerState.IceLureNotesSuccess -> {
                                        if (!iceLureNotesGetApps) {
                                            iceLureNotesGetData(it.iceLureNotesData)
                                            iceLureNotesGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "Current time less then expired, use saved url")
                            _iceLureNotesHomeScreenState.value =
                                IceLureNotesHomeScreenState.IceLureNotesSuccess(
                                    iceLureNotesSharedPreference.iceLureNotesSavedUrl
                                )
                        }
                    } else {
                        _iceLureNotesHomeScreenState.value =
                            IceLureNotesHomeScreenState.IceLureNotesNotInternet
                    }
                }
                2 -> {
                    _iceLureNotesHomeScreenState.value =
                        IceLureNotesHomeScreenState.IceLureNotesError
                }
            }
        }
    }


    private suspend fun iceLureNotesGetData(conversation: MutableMap<String, Any>?) {
        val iceLureNotesData = iceLureNotesGetAllUseCase.invoke(conversation)
        if (iceLureNotesSharedPreference.iceLureNotesAppState == 0) {
            if (iceLureNotesData == null) {
                iceLureNotesSharedPreference.iceLureNotesAppState = 2
                _iceLureNotesHomeScreenState.value =
                    IceLureNotesHomeScreenState.IceLureNotesError
            } else {
                iceLureNotesSharedPreference.iceLureNotesAppState = 1
                iceLureNotesSharedPreference.apply {
                    iceLureNotesExpired = iceLureNotesData.iceLureNotesExpires
                    iceLureNotesSavedUrl = iceLureNotesData.iceLureNotesUrl
                }
                _iceLureNotesHomeScreenState.value =
                    IceLureNotesHomeScreenState.IceLureNotesSuccess(iceLureNotesData.iceLureNotesUrl)
            }
        } else  {
            if (iceLureNotesData == null) {
                _iceLureNotesHomeScreenState.value =
                    IceLureNotesHomeScreenState.IceLureNotesSuccess(iceLureNotesSharedPreference.iceLureNotesSavedUrl)
            } else {
                iceLureNotesSharedPreference.apply {
                    iceLureNotesExpired = iceLureNotesData.iceLureNotesExpires
                    iceLureNotesSavedUrl = iceLureNotesData.iceLureNotesUrl
                }
                _iceLureNotesHomeScreenState.value =
                    IceLureNotesHomeScreenState.IceLureNotesSuccess(iceLureNotesData.iceLureNotesUrl)
            }
        }
    }


    sealed class IceLureNotesHomeScreenState {
        data object IceLureNotesLoading : IceLureNotesHomeScreenState()
        data object IceLureNotesError : IceLureNotesHomeScreenState()
        data class IceLureNotesSuccess(val data: String) : IceLureNotesHomeScreenState()
        data object IceLureNotesNotInternet: IceLureNotesHomeScreenState()
    }
}