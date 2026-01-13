package com.icelurenote.sotfap.jio.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.icelurenote.sotfap.MainActivity
import com.icelurenote.sotfap.R
import com.icelurenote.sotfap.databinding.FragmentLoadIceLureNotesBinding
import com.icelurenote.sotfap.jio.data.shar.IceLureNotesSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class IceLureNotesLoadFragment : Fragment(R.layout.fragment_load_ice_lure_notes) {
    private lateinit var iceLureNotesLoadBinding: FragmentLoadIceLureNotesBinding

    private val iceLureNotesLoadViewModel by viewModel<IceLureNotesLoadViewModel>()

    private val iceLureNotesSharedPreference by inject<IceLureNotesSharedPreference>()

    private var iceLureNotesUrl = ""

    private val iceLureNotesRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            iceLureNotesNavigateToSuccess(iceLureNotesUrl)
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                iceLureNotesSharedPreference.iceLureNotesNotificationRequest =
                    (System.currentTimeMillis() / 1000) + 259200
                iceLureNotesNavigateToSuccess(iceLureNotesUrl)
            } else {
                iceLureNotesNavigateToSuccess(iceLureNotesUrl)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iceLureNotesLoadBinding = FragmentLoadIceLureNotesBinding.bind(view)

        iceLureNotesLoadBinding.iceLureNotesGrandButton.setOnClickListener {
            val iceLureNotesPermission = Manifest.permission.POST_NOTIFICATIONS
            iceLureNotesRequestNotificationPermission.launch(iceLureNotesPermission)
            iceLureNotesSharedPreference.iceLureNotesNotificationRequestedBefore = true
        }

        iceLureNotesLoadBinding.iceLureNotesSkipButton.setOnClickListener {
            iceLureNotesSharedPreference.iceLureNotesNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            iceLureNotesNavigateToSuccess(iceLureNotesUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                iceLureNotesLoadViewModel.iceLureNotesHomeScreenState.collect {
                    when (it) {
                        is IceLureNotesLoadViewModel.IceLureNotesHomeScreenState.IceLureNotesLoading -> {

                        }

                        is IceLureNotesLoadViewModel.IceLureNotesHomeScreenState.IceLureNotesError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is IceLureNotesLoadViewModel.IceLureNotesHomeScreenState.IceLureNotesSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val iceLureNotesPermission = Manifest.permission.POST_NOTIFICATIONS
                                val iceLureNotesPermissionRequestedBefore = iceLureNotesSharedPreference.iceLureNotesNotificationRequestedBefore

                                if (ContextCompat.checkSelfPermission(requireContext(), iceLureNotesPermission) == PackageManager.PERMISSION_GRANTED) {
                                    iceLureNotesNavigateToSuccess(it.data)
                                } else if (!iceLureNotesPermissionRequestedBefore && (System.currentTimeMillis() / 1000 > iceLureNotesSharedPreference.iceLureNotesNotificationRequest)) {
                                    // первый раз — показываем UI для запроса
                                    iceLureNotesLoadBinding.iceLureNotesNotiGroup.visibility = View.VISIBLE
                                    iceLureNotesLoadBinding.iceLureNotesLoadingGroup.visibility = View.GONE
                                    iceLureNotesUrl = it.data
                                } else if (shouldShowRequestPermissionRationale(iceLureNotesPermission)) {
                                    // временный отказ — через 3 дня можно показать
                                    if (System.currentTimeMillis() / 1000 > iceLureNotesSharedPreference.iceLureNotesNotificationRequest) {
                                        iceLureNotesLoadBinding.iceLureNotesNotiGroup.visibility = View.VISIBLE
                                        iceLureNotesLoadBinding.iceLureNotesLoadingGroup.visibility = View.GONE
                                        iceLureNotesUrl = it.data
                                    } else {
                                        iceLureNotesNavigateToSuccess(it.data)
                                    }
                                } else {
                                    // навсегда отклонено — просто пропускаем
                                    iceLureNotesNavigateToSuccess(it.data)
                                }
                            } else {
                                iceLureNotesNavigateToSuccess(it.data)
                            }
                        }

                        IceLureNotesLoadViewModel.IceLureNotesHomeScreenState.IceLureNotesNotInternet -> {
                            iceLureNotesLoadBinding.iceLureNotesStateGroup.visibility = View.VISIBLE
                            iceLureNotesLoadBinding.iceLureNotesLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun iceLureNotesNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_iceLureNotesLoadFragment_to_iceLureNotesV,
            bundleOf(ICE_LURE_NOTES_D to data)
        )
    }

    companion object {
        const val ICE_LURE_NOTES_D = "iceLureNotesData"
    }
}