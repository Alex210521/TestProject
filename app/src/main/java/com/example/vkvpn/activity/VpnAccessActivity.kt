package com.example.vkvpn.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.vkvpn.R
import com.example.vkvpn.di.modules.SavePreferences
import com.example.vkvpn.dilog.VpnDialog
import com.example.vkvpn.presenter.HydraPermissionVpnPresenter
import com.example.vkvpn.utils.Constant.OPEN_APPLICATION_FIRST_TIME
import com.example.vkvpn.view.HydraPermissionVpnView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_vpn_access.*
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class VpnAccessActivity: MvpAppCompatActivity(), HydraPermissionVpnView {
    @Inject
    lateinit var hydraPermissionVpnPresenter: Provider<HydraPermissionVpnPresenter>
    @Inject
    lateinit var savePreferences: SavePreferences
    private val presenter by moxyPresenter { hydraPermissionVpnPresenter.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vpn_access)
        showVpnDialog()
        restart.setOnClickListener {
            errorLayout.visibility = View.GONE
            presenter.checkVpnPermission()
        }
    }

    private fun showVpnDialog() {
        val vpnDialog = VpnDialog()
        vpnDialog.show(supportFragmentManager, "vpnDialog")
        vpnDialog.newInstance(object : VpnDialog.CallBackVpn {
            override fun vpnStatus() {
                presenter.checkVpnPermission()
            }
        })
    }

    override fun showError() {
        errorLayout.visibility = View.VISIBLE
    }

    override fun showSuccess() {
        savePreferences.saveOpenFirst(true, OPEN_APPLICATION_FIRST_TIME)
        showNextActivity()
    }

    override fun showNextActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}