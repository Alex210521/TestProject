package com.example.vkvpn.activity


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.anchorfree.vpnsdk.vpnservice.VPNState
import com.example.vkvpn.R
import com.example.vkvpn.presenter.HydraVpnPresenter
import com.example.vkvpn.utils.Constant.CAMERA
import com.example.vkvpn.utils.Constant.DIALOG
import com.example.vkvpn.utils.Constant.FILES
import com.example.vkvpn.utils.MaterialProgressDialog
import com.example.vkvpn.utils.StatusBar.setStatusBarGradient
import com.example.vkvpn.utils.TextFont.textFontButton
import com.example.vkvpn.utils.TextFont.textFontTextView
import com.example.vkvpn.view.HydraVpnView
import com.example.vkvpn.web.VideoEnabledWebChromeClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.errorLayout
import kotlinx.android.synthetic.main.activity_vpn_access.*
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MainActivity : MvpAppCompatActivity(), HydraVpnView {
    private var materialProgressDialog: MaterialProgressDialog? = null
    private var restartFlag: Boolean = false
    private var browserBackFlag: Boolean = false
    private var wrongStateFlag: Boolean = true
    private var clickableFlag: Boolean = true
    private var reconnectVpnFlag: Boolean = false
    lateinit var videoEnabledWebChromeClient: VideoEnabledWebChromeClient

    @Inject
    lateinit var presenter: Provider<HydraVpnPresenter>
    private val hydraVpnPresenter by moxyPresenter { presenter.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        videoEnabledWebChromeClient = VideoEnabledWebChromeClient(this@MainActivity, supportFragmentManager)
        this.setStatusBarGradient(ContextCompat.getDrawable(this, R.color.blue))
        textFont()
        loadingUrl()
        restart.setOnClickListener {
            clickToRestartButton()
        }
    }


    private fun clickToRestartButton() {
        if (showCheckInternet() && clickableFlag) {
            if (reconnectVpnFlag) {
                hydraVpnPresenter.stateDialog(true)
                hydraVpnPresenter.repeatButtonClick()
                reconnectVpnFlag = false
            } else {
                if (webView.url == null) {
                    hydraVpnPresenter.stateDialog(true)
                    loadingUrl()
                }
            }
            hydraVpnPresenter.checkViewState(View.GONE, View.VISIBLE)
        } else {
            hydraVpnPresenter.errorMessage(getString(R.string.no_connection))
        }
    }


    private fun textFont() {
        errorText.textFontTextView("Montserrat-Regular.ttf")
        restart.textFontButton("Montserrat-Bold.ttf")
    }

    override fun onStart() {
        super.onStart()
        if (webView.canGoBack() && browserBackFlag) {
            hydraVpnPresenter.stateDialog(true)
            webView.goBack()
            browserBackFlag = false
            hydraVpnPresenter.stateDialog(false)
        }
        if (errorLayout.visibility == View.VISIBLE) {
            hydraVpnPresenter.checkOrientation()
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (!videoEnabledWebChromeClient.onBackPressed()) {
                        if (webView.canGoBack() && webView.visibility == View.VISIBLE) {
                            webView.goBack()
                        } else {
                            moveTaskToBack(true)
                        }
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun loadingUrl() {
        webView.loadUrl("https://vk.com/")
        webView.webChromeClient = WebChromeClient()
        webView.settings.javaScriptEnabled = true
        //   webView.settings.builtInZoomControls = false
        webView.settings.setSupportZoom(false)
        // webView.settings.displayZoomControls = false
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.domStorageEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.loadsImagesAutomatically = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.setSupportMultipleWindows(false)
        webView.settings.pluginState = WebSettings.PluginState.ON
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.settings.saveFormData = true
        webView.webChromeClient = videoEnabledWebChromeClient
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                if (!url.contains("vk.com")) {
                    browserBackFlag = true
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                        return true
                    } else {
                        hydraVpnPresenter.errorMessage(getString(R.string.no_content))
                    }

                }
                return false
            }


            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                hydraVpnPresenter.stateDialog(false)


            }


            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                hydraVpnPresenter.stateDialog(true)
            }
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] >= 0 && grantResults[1] >= 0) {
            when (requestCode) {
                CAMERA ->
                    videoEnabledWebChromeClient.openCamera()
                FILES -> videoEnabledWebChromeClient.openGallery()

                DIALOG -> {
                    videoEnabledWebChromeClient.openDialog()
                }
            }
        } else {
            hydraVpnPresenter.errorMessage(resources.getString(R.string.correct_permission))
            videoEnabledWebChromeClient.uploadMessage!!.onReceiveValue(null)
            videoEnabledWebChromeClient.uploadMessage = null
        }

    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }

    private fun convertPixelToDp(dpValue: Int): Int {
        val d: Float = resources.displayMetrics.density;
        return (dpValue * d).toInt()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && errorLayout.visibility == View.VISIBLE) {
            errorLayoutMargin(20, 10, 20)
        } else if (newConfig?.orientation == Configuration.ORIENTATION_PORTRAIT && errorLayout.visibility == View.VISIBLE) {
            errorLayoutMargin(150, 35, 40)
        }
    }


    private fun errorLayoutMargin(imageMargin: Int, textMargin: Int, buttonMargin: Int) {
        (errorImage.layoutParams as RelativeLayout.LayoutParams).setMargins(
            0,
            convertPixelToDp(imageMargin),
            0,
            0
        )
        (errorText.layoutParams as RelativeLayout.LayoutParams).setMargins(
            0,
            convertPixelToDp(textMargin),
            0,
            0
        )
        (restart.layoutParams as RelativeLayout.LayoutParams).setMargins(
            0,
            convertPixelToDp(buttonMargin),
            0,
            0
        )
    }

    override fun showDialog(flag: Boolean) {
        val progressDialog =
            materialProgressDialog ?: MaterialProgressDialog.createProgressDialog(this@MainActivity)
                .also {
                    materialProgressDialog = it
                }
        if (flag) {
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FILES -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    if (null == videoEnabledWebChromeClient.uploadMessage)
                        return
                    val uri: Array<Uri> = data.data?.let { arrayOf(it) }!!
                    downloadFile(uri)

                } else {
                    downloadFile(null)
                }
            }
            CAMERA -> {
                try {
                    val uri: Array<Uri> = arrayOf(videoEnabledWebChromeClient.file!!)
                    downloadFile(uri)
                } catch (exception: Exception) {
                    downloadFile(null)
                    return
                }
            }
            else -> {
                downloadFile(null)
            }
        }
    }

    private fun downloadFile(uri: Array<Uri>?) {
        videoEnabledWebChromeClient.uploadMessage!!.onReceiveValue(uri)
        videoEnabledWebChromeClient.uploadMessage = null
    }


    override fun showErrorMessage(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        val text: TextView? = toast.view?.findViewById(android.R.id.message) as TextView
        if (text != null) {
            text.gravity = Gravity.CENTER
            text.textFontTextView("Montserrat-Regular.ttf")
        }
        toast.show()
    }

    override fun showErrorViewState(firstViewState: Int, secondViewState: Int) {
        errorLayout.visibility = firstViewState
        webView.visibility = secondViewState
        if (secondViewState == View.VISIBLE) {
            webView.onResume()
        } else {
            webView.onPause()
            pauseAudioTrack()
            restart.visibility = View.GONE
        }
        if (videoEnabledWebChromeClient.isVideoFullscreen) {
            videoEnabledWebChromeClient.onBackPressed()
        }
    }

    private fun pauseAudioTrack() {
        (getSystemService(Context.AUDIO_SERVICE) as AudioManager).requestAudioFocus(
            { }, AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        )
    }

    override fun showErrorSetView(image: Int, errorMessage: Int) {
        errorImage.setImageResource(image)
        errorText.text = resources.getString(errorMessage)
        restart.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun showCheckOrientation() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && errorLayout.visibility == View.VISIBLE) {
            errorLayoutMargin(20, 10, 20)
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && errorLayout.visibility == View.VISIBLE) {
            errorLayoutMargin(150, 35, 40)
        }
    }

    override fun showConnectToVpn() {
        wrongStateFlag = true
        loadingUrl()
    }


    override fun showVpnState(state: VPNState) {
        when (state) {
            VPNState.CONNECTED -> {
                if (webView.visibility == View.GONE && showCheckInternet()) {
                    restartFlag = true
                    clickableFlag = true
                }
            }
            VPNState.PAUSED -> {
                if (!showCheckInternet() && errorLayout.visibility == View.GONE) {
                    hydraVpnPresenter.stateDialog(false)
                    clickableFlag = false
                    hydraVpnPresenter.checkViewState(View.VISIBLE, View.GONE)
                    hydraVpnPresenter.checkOrientation()
                    hydraVpnPresenter.errorLayoutComponent(R.drawable.wifi, R.string.no_connection)
                }
            }
        }
    }

    override fun showStateReconnectFlag(reconnectFlag: Boolean) {
        reconnectVpnFlag = reconnectFlag
    }

    fun showCheckInternet(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    override fun updateWebViewPage() {
        webView.loadUrl("https://vk.com/")
    }
}