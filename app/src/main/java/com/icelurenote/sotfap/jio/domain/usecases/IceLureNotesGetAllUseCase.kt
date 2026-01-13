package com.icelurenote.sotfap.jio.domain.usecases

import android.util.Log
import com.icelurenote.sotfap.jio.data.repo.IceLureNotesRepository
import com.icelurenote.sotfap.jio.data.utils.IceLureNotesPushToken
import com.icelurenote.sotfap.jio.data.utils.IceLureNotesSystemService
import com.icelurenote.sotfap.jio.domain.model.IceLureNotesEntity
import com.icelurenote.sotfap.jio.domain.model.IceLureNotesParam
import com.icelurenote.sotfap.jio.presentation.app.IceLureNotesApplication

class IceLureNotesGetAllUseCase(
    private val iceLureNotesRepository: IceLureNotesRepository,
    private val iceLureNotesSystemService: IceLureNotesSystemService,
    private val iceLureNotesPushToken: IceLureNotesPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : IceLureNotesEntity?{
        val params = IceLureNotesParam(
            iceLureNotesLocale = iceLureNotesSystemService.iceLureNotesGetLocale(),
            iceLureNotesPushToken = iceLureNotesPushToken.iceLureNotesGetToken(),
            iceLureNotesAfId = iceLureNotesSystemService.iceLureNotesGetAppsflyerId()
        )
        Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "Params for request: $params")
        return iceLureNotesRepository.iceLureNotesGetClient(params, conversion)
    }



}