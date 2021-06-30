package com.example.vkvpn.view


import com.anchorfree.vpnsdk.vpnservice.VPNState
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

@OneExecution
interface HydraVpnView : MvpView {
    fun showDialog(flag: Boolean)
    fun showErrorMessage(message: String)
    fun showErrorViewState(firstViewState: Int, secondViewState: Int)
    fun showErrorSetView(image: Int, errorMessage: Int)
    fun showConnectToVpn()
    fun updateWebViewPage()
    fun showVpnState(state: VPNState)
    fun showStateReconnectFlag(reconnectFlag: Boolean)
    fun showCheckOrientation()
}
