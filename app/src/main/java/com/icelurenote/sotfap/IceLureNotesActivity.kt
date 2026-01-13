package com.icelurenote.sotfap

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.icelurenote.sotfap.jio.IceLureNotesGlobalLayoutUtil
import com.icelurenote.sotfap.jio.iceLureNotesSetupSystemBars
import com.icelurenote.sotfap.jio.presentation.app.IceLureNotesApplication
import com.icelurenote.sotfap.jio.presentation.pushhandler.IceLureNotesPushHandler
import org.koin.android.ext.android.inject

class IceLureNotesActivity : AppCompatActivity() {

    private val iceLureNotesPushHandler by inject<IceLureNotesPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        iceLureNotesSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_ice_lure_notes)

        val iceLureNotesRootView = findViewById<View>(android.R.id.content)
        IceLureNotesGlobalLayoutUtil().iceLureNotesAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(iceLureNotesRootView) { iceLureNotesView, iceLureNotesInsets ->
            val iceLureNotesSystemBars = iceLureNotesInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val iceLureNotesDisplayCutout = iceLureNotesInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val iceLureNotesIme = iceLureNotesInsets.getInsets(WindowInsetsCompat.Type.ime())


            val iceLureNotesTopPadding = maxOf(iceLureNotesSystemBars.top, iceLureNotesDisplayCutout.top)
            val iceLureNotesLeftPadding = maxOf(iceLureNotesSystemBars.left, iceLureNotesDisplayCutout.left)
            val iceLureNotesRightPadding = maxOf(iceLureNotesSystemBars.right, iceLureNotesDisplayCutout.right)
            window.setSoftInputMode(IceLureNotesApplication.iceLureNotesInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "ADJUST PUN")
                val iceLureNotesBottomInset = maxOf(iceLureNotesSystemBars.bottom, iceLureNotesDisplayCutout.bottom)

                iceLureNotesView.setPadding(iceLureNotesLeftPadding, iceLureNotesTopPadding, iceLureNotesRightPadding, 0)

                iceLureNotesView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = iceLureNotesBottomInset
                }
            } else {
                Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "ADJUST RESIZE")

                val iceLureNotesBottomInset = maxOf(iceLureNotesSystemBars.bottom, iceLureNotesDisplayCutout.bottom, iceLureNotesIme.bottom)

                iceLureNotesView.setPadding(iceLureNotesLeftPadding, iceLureNotesTopPadding, iceLureNotesRightPadding, 0)

                iceLureNotesView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = iceLureNotesBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(IceLureNotesApplication.ICE_LURE_NOTES_MAIN_TAG, "Activity onCreate()")
        iceLureNotesPushHandler.iceLureNotesHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            iceLureNotesSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        iceLureNotesSetupSystemBars()
    }
}