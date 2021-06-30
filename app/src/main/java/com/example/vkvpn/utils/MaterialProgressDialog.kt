package com.example.vkvpn.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.example.vkvpn.R

class MaterialProgressDialog(context: Context?) : Dialog(context!!, R.style.ProgressDialogTheme) {
    companion object {
        fun createProgressDialog(activity: Activity): MaterialProgressDialog {
            val linearLayout = LinearLayout(activity)
            linearLayout.orientation = LinearLayout.VERTICAL
            val progressBar = ProgressBar(activity)
            progressBar.indeterminateDrawable = activity.resources.getDrawable(R.drawable.progress_bar)
            linearLayout.addView(progressBar)
            var dialog = MaterialProgressDialog(activity)
            dialog.setCancelable(false)
            dialog.ownerActivity
            dialog.addContentView(linearLayout, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            return dialog
        }
    }
}