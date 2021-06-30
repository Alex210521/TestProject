package com.example.vkvpn.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.example.vkvpn.utils.Constant.OPEN_APPLICATION_FIRST_TIME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavePreferences @Inject constructor(@ApplicationContext val context:Context) {
    private lateinit var sharedPreferences: SharedPreferences

    fun saveOpenFirst(flag: Boolean, constant: String) {
        sharedPreferences = context.getSharedPreferences(constant, Context.MODE_PRIVATE)
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putBoolean(constant, flag)
        sharedPreferencesEditor.apply()
    }

    fun loadOenFirst(constant: String): Boolean {
        var flag: Boolean = false
        sharedPreferences = context.getSharedPreferences(OPEN_APPLICATION_FIRST_TIME, Context.MODE_PRIVATE)
        if (sharedPreferences.contains(constant)) {
            flag = sharedPreferences.getBoolean(constant, false)
        }
        return flag
    }
}