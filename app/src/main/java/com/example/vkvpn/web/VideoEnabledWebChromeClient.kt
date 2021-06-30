package com.example.vkvpn.web

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.example.vkvpn.dilog.FileDialog
import com.example.vkvpn.utils.Constant.CAMERA
import com.example.vkvpn.utils.Constant.CAMERA_FILES_PERMISSIONS
import com.example.vkvpn.utils.Constant.DIALOG
import com.example.vkvpn.utils.Constant.FILES


class VideoEnabledWebChromeClient(private var activity: Activity, private var supportFragmentManager: FragmentManager) :
    WebChromeClient(), FileDialog.CallBackClick {
    private var customView: View? = null
    private var customViewCallBack: WebChromeClient.CustomViewCallback? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var filePath: ValueCallback<Array<Uri>>? = null
    var uploadMessage: ValueCallback<Array<Uri>>? = null
    private var fileChooserParams: FileChooserParams? = null
    private var fileChooser: FileChooserParams? = null
    var file: Uri? = null
    private var originalOrientation = 0
    private var originalSystemUiVisibility = 0
    var isVideoFullscreen: Boolean = false
    private lateinit var intent: Intent

    override fun getDefaultVideoPoster(): Bitmap? {
        if (activity == null) {
            return null

        }
        return BitmapFactory.decodeResource(activity.applicationContext.resources, 2130837573)
    }

    override fun onHideCustomView() {
        ((activity.window.decorView) as FrameLayout).removeView(this.customView)
        customView = null
        isVideoFullscreen = false
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        activity.window.decorView.systemUiVisibility = this.originalSystemUiVisibility
        activity.requestedOrientation = this.originalOrientation
        customViewCallBack?.onCustomViewHidden()
        customViewCallBack = null
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        super.onShowCustomView(view, callback)
        if (customView != null) {
            onHideCustomView()
            return
        }
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        isVideoFullscreen = true
        customView = view
        originalSystemUiVisibility = activity.window.decorView.systemUiVisibility
        originalOrientation = activity.requestedOrientation
        customViewCallBack = callback
        ((activity.window.decorView) as FrameLayout).addView(
            customView,
            FrameLayout.LayoutParams(-1, -1)
        )
        activity.window.decorView.systemUiVisibility = 3846
    }


    override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
        callback!!.invoke(origin, true, false)
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        filePath = filePathCallback
        fileChooser = fileChooserParams
        return if (!hasPermissions(activity, *CAMERA_FILES_PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, CAMERA_FILES_PERMISSIONS, DIALOG)
            uploadMessage = filePathCallback
            true
        } else {
            openDialog()
            true
        }
    }

    fun openDialog() {
        if (uploadMessage != null) {
            uploadMessage = null
        }

        uploadMessage = filePath
        val fileDialog = FileDialog()
        fileDialog.show(supportFragmentManager, "vpnDialog")
        fileDialog.newInstance(this, filePath, fileChooser)


    }

    fun openCamera() {
        val values: ContentValues = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Pictures")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        file = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file)
        activity.startActivityForResult(intent, CAMERA)
    }


    fun openGallery() {
        val intent = fileChooserParams!!.createIntent()
        try {
            activity.startActivityForResult(Intent.createChooser(intent, ""), FILES)
        } catch (e: ActivityNotFoundException) {
            uploadMessage = null
            Toast.makeText(activity, "Cannot Open File Chooser", Toast.LENGTH_LONG).show()
        }
    }


    override fun callBackCamera(filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?) {
        this.filePathCallback = filePathCallback
        this.fileChooserParams = fileChooserParams
        if (!hasPermissions(activity, *CAMERA_FILES_PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, CAMERA_FILES_PERMISSIONS, CAMERA)
        } else {
            openCamera()
        }
    }

    override fun callBackGallery(filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?) {
        this.filePathCallback = filePathCallback
        this.fileChooserParams = fileChooserParams
        if (!hasPermissions(activity, *CAMERA_FILES_PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, CAMERA_FILES_PERMISSIONS, FILES)
        } else {
            openGallery()
        }
    }

    override fun callBackDismiss() {
        uploadMessage!!.onReceiveValue(null)
        uploadMessage = null
    }


    private fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onJsPrompt(
        view: WebView, url: String, message: String, defaultValue: String,
        result: JsPromptResult
    ): Boolean {
        return true
    }

    fun onBackPressed(): Boolean {
        return if (isVideoFullscreen) {
            onHideCustomView()
            true
        } else {
            false
        }
    }
}