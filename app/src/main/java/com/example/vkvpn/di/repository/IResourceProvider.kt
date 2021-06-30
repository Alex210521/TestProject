package com.example.vkvpn.di.repository

import android.content.Context
import android.content.res.Resources

interface IResourceProvider {
    fun getContext():Context
    fun getResources(): Resources
    fun getString(stringId: Int): String
}