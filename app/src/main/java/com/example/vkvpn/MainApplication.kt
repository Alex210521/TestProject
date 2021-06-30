package com.example.vkvpn

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.anchorfree.partner.api.ClientInfo
import com.anchorfree.sdk.HydraTransportConfig
import com.anchorfree.sdk.TransportConfig
import com.anchorfree.sdk.UnifiedSDK
import com.anchorfree.sdk.UnifiedSDKConfig
import com.anchorfree.vpnsdk.callbacks.CompletableCallback
import com.example.vkvpn.di.modules.ResourceModule
import com.northghost.caketube.OpenVpnTransportConfig
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {
    private lateinit var hostUrl: String
    private lateinit var carrierId: String


    override fun onCreate() {
        super.onCreate()
        hostUrl= this.getString(R.string.hostUrl)
        carrierId=this.getString(R.string.carrierId)
        initHydraSdk()
    }

    private fun initHydraSdk() {
        val clientInfo = ClientInfo.newBuilder()
            .baseUrl(hostUrl)
            .carrierId(carrierId)
            .build()

        val transportConfigList: MutableList<TransportConfig> = ArrayList()
        transportConfigList.add(HydraTransportConfig.create())
        transportConfigList.add(OpenVpnTransportConfig.tcp())
        transportConfigList.add(OpenVpnTransportConfig.udp())
        UnifiedSDK.update(transportConfigList, CompletableCallback.EMPTY)
        val config = UnifiedSDKConfig.newBuilder().idfaEnabled(false).build()
        val unifiedSdk = UnifiedSDK.getInstance(clientInfo, config)
    }
}