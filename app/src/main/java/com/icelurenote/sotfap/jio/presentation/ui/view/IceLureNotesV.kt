package com.icelurenote.sotfap.jio.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.icelurenote.sotfap.jio.presentation.app.IceLureNotesApplication
import com.icelurenote.sotfap.jio.presentation.ui.load.IceLureNotesLoadFragment
import org.koin.android.ext.android.inject

class IceLureNotesV : Fragment(){

    private lateinit var iceLureNotesPhoto: Uri
    private var iceLureNotesFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val iceLureNotesTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        iceLureNotesFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        iceLureNotesFilePathFromChrome = null
    }

    private val iceLureNotesTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            iceLureNotesFilePathFromChrome?.onReceiveValue(arrayOf(iceLureNotesPhoto))
            iceLureNotesFilePathFromChrome = null
        } else {
            iceLureNotesFilePathFromChrome?.onReceiveValue(null)
            iceLureNotesFilePathFromChrome = null
        }
    }

    private val iceLureNotesDataStore by activityViewModels<IceLureNotesDataStore>()


    private val iceLureNotesViFun by inject<IceLureNotesViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (iceLureNotesDataStore.iceLureNotesView.canGoBack()) {
                        iceLureNotesDataStore.iceLureNotesView.goBack()
                        Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "WebView can go back")
                    } else if (iceLureNotesDataStore.iceLureNotesViList.size > 1) {
                        Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "WebView can`t go back")
                        iceLureNotesDataStore.iceLureNotesViList.removeAt(iceLureNotesDataStore.iceLureNotesViList.lastIndex)
                        Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "WebView list size ${iceLureNotesDataStore.iceLureNotesViList.size}")
                        iceLureNotesDataStore.iceLureNotesView.destroy()
                        val previousWebView = iceLureNotesDataStore.iceLureNotesViList.last()
                        iceLureNotesAttachWebViewToContainer(previousWebView)
                        iceLureNotesDataStore.iceLureNotesView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (iceLureNotesDataStore.iceLureNotesIsFirstCreate) {
            iceLureNotesDataStore.iceLureNotesIsFirstCreate = false
            iceLureNotesDataStore.iceLureNotesContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return iceLureNotesDataStore.iceLureNotesContainerView
        } else {
            return iceLureNotesDataStore.iceLureNotesContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "onViewCreated")
        if (iceLureNotesDataStore.iceLureNotesViList.isEmpty()) {
            iceLureNotesDataStore.iceLureNotesView = IceLureNotesVi(requireContext(), object :
                IceLureNotesCallBack {
                override fun iceLureNotesHandleCreateWebWindowRequest(iceLureNotesVi: IceLureNotesVi) {
                    iceLureNotesDataStore.iceLureNotesViList.add(iceLureNotesVi)
                    Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "WebView list size = ${iceLureNotesDataStore.iceLureNotesViList.size}")
                    Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "CreateWebWindowRequest")
                    iceLureNotesDataStore.iceLureNotesView = iceLureNotesVi
                    iceLureNotesVi.iceLureNotesSetFileChooserHandler { callback ->
                        iceLureNotesHandleFileChooser(callback)
                    }
                    iceLureNotesAttachWebViewToContainer(iceLureNotesVi)
                }

            }, iceLureNotesWindow = requireActivity().window).apply {
                iceLureNotesSetFileChooserHandler { callback ->
                    iceLureNotesHandleFileChooser(callback)
                }
            }
            iceLureNotesDataStore.iceLureNotesView.iceLureNotesFLoad(arguments?.getString(
                IceLureNotesLoadFragment.ICE_LURE_NOTES_D) ?: "")
//            ejvview.fLoad("www.google.com")
            iceLureNotesDataStore.iceLureNotesViList.add(iceLureNotesDataStore.iceLureNotesView)
            iceLureNotesAttachWebViewToContainer(iceLureNotesDataStore.iceLureNotesView)
        } else {
            iceLureNotesDataStore.iceLureNotesViList.forEach { webView ->
                webView.iceLureNotesSetFileChooserHandler { callback ->
                    iceLureNotesHandleFileChooser(callback)
                }
            }
            iceLureNotesDataStore.iceLureNotesView = iceLureNotesDataStore.iceLureNotesViList.last()

            iceLureNotesAttachWebViewToContainer(iceLureNotesDataStore.iceLureNotesView)
        }
        Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "WebView list size = ${iceLureNotesDataStore.iceLureNotesViList.size}")
    }

    private fun iceLureNotesHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        iceLureNotesFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "Launching file picker")
                    iceLureNotesTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "Launching camera")
                    iceLureNotesPhoto = iceLureNotesViFun.iceLureNotesSavePhoto()
                    iceLureNotesTakePhoto.launch(iceLureNotesPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                iceLureNotesFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun iceLureNotesAttachWebViewToContainer(w: IceLureNotesVi) {
        iceLureNotesDataStore.iceLureNotesContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            iceLureNotesDataStore.iceLureNotesContainerView.removeAllViews()
            iceLureNotesDataStore.iceLureNotesContainerView.addView(w)
        }
    }


}