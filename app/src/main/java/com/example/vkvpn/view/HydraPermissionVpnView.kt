package com.example.vkvpn.view

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

@OneExecution
interface HydraPermissionVpnView : MvpView {
    fun showError()
    fun showSuccess()
    fun showNextActivity()
}