package com.example.vkvpn.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import androidx.core.content.ContextCompat
import com.anchorfree.sdk.VpnPermissions
import com.example.vkvpn.R
import com.example.vkvpn.utils.Constant.SPLASH_TIME_OUT
import com.example.vkvpn.utils.StatusBar.setStatusBarGradient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        this.setStatusBarGradient(ContextCompat.getDrawable(this, R.color.splash_color))
        activityScope.launch {
            delay(SPLASH_TIME_OUT)
            if (VpnPermissions.granted()) {
                nextScreen(MainActivity::class.java)
            } else {
                nextScreen(VpnAccessActivity::class.java)
            }
        }
    }

    private fun nextScreen(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
        finish()
    }


    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }
}