package com.example.vkvpn.utils

import android.content.Context
import android.graphics.Typeface
import android.widget.Button
import android.widget.TextView

object TextFont {

    fun TextView.textFontTextView( textType: String) {
        val type: Typeface = Typeface.createFromAsset(this.context.assets, textType)
        this.typeface = type
    }

    fun Button.textFontButton(textType: String) {
        val type: Typeface = Typeface.createFromAsset(this.context.assets, textType)
        this.typeface = type
    }

}