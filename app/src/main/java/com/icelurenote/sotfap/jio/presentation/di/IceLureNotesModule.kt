package com.icelurenote.sotfap.jio.presentation.di

import com.icelurenote.sotfap.jio.data.repo.IceLureNotesRepository
import com.icelurenote.sotfap.jio.data.shar.IceLureNotesSharedPreference
import com.icelurenote.sotfap.jio.data.utils.IceLureNotesPushToken
import com.icelurenote.sotfap.jio.data.utils.IceLureNotesSystemService
import com.icelurenote.sotfap.jio.domain.usecases.IceLureNotesGetAllUseCase
import com.icelurenote.sotfap.jio.presentation.pushhandler.IceLureNotesPushHandler
import com.icelurenote.sotfap.jio.presentation.ui.load.IceLureNotesLoadViewModel
import com.icelurenote.sotfap.jio.presentation.ui.view.IceLureNotesViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val iceLureNotesModule = module {
    factory {
        IceLureNotesPushHandler()
    }
    single {
        IceLureNotesRepository()
    }
    single {
        IceLureNotesSharedPreference(get())
    }
    factory {
        IceLureNotesPushToken()
    }
    factory {
        IceLureNotesSystemService(get())
    }
    factory {
        IceLureNotesGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        IceLureNotesViFun(get())
    }
    viewModel {
        IceLureNotesLoadViewModel(get(), get(), get())
    }
}