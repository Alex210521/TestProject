package com.example.vkvpn.di.modules

import android.content.Context
import android.content.res.Resources
import com.example.vkvpn.di.repository.IResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceModule @Inject constructor(@ApplicationContext private val context: Context) :
    IResourceProvider {
    override fun getContext(): Context =context

    override fun getResources(): Resources {
        return context.resources
    }

    override fun getString(stringId: Int): String {
        return context.resources.getString(stringId)
    }


}