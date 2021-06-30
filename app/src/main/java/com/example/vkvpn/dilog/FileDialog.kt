package com.example.vkvpn.dilog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.fragment.app.DialogFragment
import com.example.vkvpn.R
import kotlinx.android.synthetic.main.file_dialog.view.*


class FileDialog : DialogFragment() {
    private lateinit var callBackClick: CallBackClick
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var fileChooserParams: WebChromeClient.FileChooserParams? = null
    private var dismissFlag: Boolean = false

    fun newInstance(
        callBackClick: CallBackClick,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ) {
        this.callBackClick = callBackClick
        this.filePathCallback = filePathCallback
        this.fileChooserParams = fileChooserParams
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.file_dialog, null)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.download_file.setOnClickListener {
            callBackClick.callBackGallery(filePathCallback, fileChooserParams)
            dismissFlag = true
            dismiss()
        }
        view.take_photo.setOnClickListener {
            callBackClick.callBackCamera(filePathCallback, fileChooserParams)
            dismissFlag = true
            dismiss()
        }
        return view
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!dismissFlag) {
            callBackClick.callBackDismiss()
        }
    }

    interface CallBackClick {
        fun callBackCamera(
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: WebChromeClient.FileChooserParams?
        )

        fun callBackGallery(
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: WebChromeClient.FileChooserParams?
        )

        fun callBackDismiss()
    }
}