package com.example.vkvpn.presenter

import android.util.Log
import com.anchorfree.sdk.UnifiedSDK
import com.anchorfree.sdk.VpnPermissions
import com.anchorfree.vpnsdk.callbacks.CompletableCallback
import com.anchorfree.vpnsdk.exceptions.VpnException
import com.anchorfree.vpnsdk.exceptions.VpnPermissionDeniedException
import com.example.vkvpn.di.modules.ResourceModule
import com.example.vkvpn.view.HydraPermissionVpnView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moxy.InjectViewState
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

@InjectViewState
class HydraPermissionVpnPresenter @Inject constructor(
    private val resourceModule: ResourceModule
) : MvpPresenter<HydraPermissionVpnView>() {

    fun checkVpnPermission() {
        if (VpnPermissions.granted()) {
           permissionDialog()
        } else {
            viewState.showNextActivity()
        }
    }

    private fun permissionDialog(){
        VpnPermissions.request(object : CompletableCallback {
            override fun complete() {
                presenterScope.launch(Dispatchers.Main) {
                    viewState.showSuccess()
                }
            }

            override fun error(exception: VpnException) {
               presenterScope.launch(Dispatchers.Main) {
                   when(exception){
                       is VpnPermissionDeniedException->{

                       }

                   }
               }
            }
        })
    }
}