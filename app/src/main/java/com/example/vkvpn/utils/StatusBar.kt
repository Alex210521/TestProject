package com.example.vkvpn.utils

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat

object StatusBar {
    fun Activity.setStatusBarGradient(background: Drawable?) {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor =
            ContextCompat.getColor(this.applicationContext, android.R.color.transparent)
        window.navigationBarColor =
            ContextCompat.getColor(this.applicationContext, android.R.color.transparent)
        window.setBackgroundDrawable(background)
    }
}