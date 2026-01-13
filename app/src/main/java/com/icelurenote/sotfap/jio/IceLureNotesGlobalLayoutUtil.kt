package com.icelurenote.sotfap.jio

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.icelurenote.sotfap.jio.presentation.app.IceLureNotesApplication

class IceLureNotesGlobalLayoutUtil {

    private var iceLureNotesMChildOfContent: View? = null
    private var iceLureNotesUsableHeightPrevious = 0

    fun iceLureNotesAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        iceLureNotesMChildOfContent = content.getChildAt(0)

        iceLureNotesMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val iceLureNotesUsableHeightNow = iceLureNotesComputeUsableHeight()
        if (iceLureNotesUsableHeightNow != iceLureNotesUsableHeightPrevious) {
            val iceLureNotesUsableHeightSansKeyboard = iceLureNotesMChildOfContent?.rootView?.height ?: 0
            val iceLureNotesHeightDifference = iceLureNotesUsableHeightSansKeyboard - iceLureNotesUsableHeightNow

            if (iceLureNotesHeightDifference > (iceLureNotesUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(IceLureNotesApplication.iceLureNotesInputMode)
            } else {
                activity.window.setSoftInputMode(IceLureNotesApplication.iceLureNotesInputMode)
            }
//            mChildOfContent?.requestLayout()
            iceLureNotesUsableHeightPrevious = iceLureNotesUsableHeightNow
        }
    }

    private fun iceLureNotesComputeUsableHeight(): Int {
        val r = Rect()
        iceLureNotesMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}