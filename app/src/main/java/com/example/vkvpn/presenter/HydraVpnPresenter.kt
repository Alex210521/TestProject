package com.example.vkvpn.presenter

import android.util.Log
import android.view.View
import com.anchorfree.partner.api.auth.AuthMethod
import com.anchorfree.partner.api.response.User
import com.anchorfree.partner.exceptions.RequestException
import com.anchorfree.reporting.TrackingConstants
import com.anchorfree.sdk.SessionConfig
import com.anchorfree.sdk.UnifiedSDK
import com.anchorfree.sdk.rules.TrafficRule
import com.anchorfree.vpnsdk.callbacks.Callback
import com.anchorfree.vpnsdk.callbacks.CompletableCallback
import com.anchorfree.vpnsdk.callbacks.VpnStateListener
import com.anchorfree.vpnsdk.exceptions.*
import com.anchorfree.vpnsdk.transporthydra.HydraTransport
import com.anchorfree.vpnsdk.transporthydra.HydraVpnTransportException
import com.anchorfree.vpnsdk.vpnservice.VPNState
import com.anchorfree.vpnsdk.vpnservice.credentials.AppPolicy
import com.example.vkvpn.R
import com.example.vkvpn.di.modules.ResourceModule
import com.example.vkvpn.view.HydraVpnView
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

@InjectViewState
class HydraVpnPresenter @Inject constructor(
    private val resourceModule: ResourceModule
) : MvpPresenter<HydraVpnView>(), VpnStateListener {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        UnifiedSDK.addVpnStateListener(this)
        connectToVpn()
    }

    override fun destroyView(view: HydraVpnView?) {
        super.destroyView(view)
        disconnectVpn()
    }

    override fun vpnStateChanged(vpnState: VPNState) {
        updateUI()
    }

    override fun vpnError(error: VpnException) {
        updateUI()
        handleError(error)
    }

    fun stateDialog(dialogFlag: Boolean) {
        viewState.showDialog(dialogFlag)
    }

    private fun disconnectVpn() {
        UnifiedSDK.getInstance().vpn.stop(
            TrackingConstants.GprReasons.M_UI,
            object : CompletableCallback {
                override fun complete() {
                    UnifiedSDK.getVpnState(object : Callback<VPNState> {
                        override fun success(vpnState: VPNState) {
                            updateUI()
                        }

                        override fun failure(error: VpnException) {
                            handleError(error)
                        }

                    })
                }

                override fun error(error: VpnException) {
                    handleError(error)
                }

            })
    }

    private fun updateWebView(){
        UnifiedSDK.getInstance().vpn.stop(
            TrackingConstants.GprReasons.M_UI,
            object : CompletableCallback {
                override fun complete() {
                    updateUI()
                    viewState.showDialog(true)
                    loginToVpn()
                }

                override fun error(e: VpnException) {
                    updateUI()
                    viewState.showDialog(false)
                }

            })
    }


    private fun updateVpnState() {
        UnifiedSDK.getInstance().vpn.stop(
            TrackingConstants.GprReasons.M_UI,
            object : CompletableCallback {
                override fun complete() {
                    connectToVpn()
                }

                override fun error(e: VpnException) {
                    updateUI()
                    handleError(e)
                }

            })
    }

    fun repeatButtonClick() {
        connectToVpn()
    }

    fun checkViewState(firstViewState: Int, secondViewState: Int) {
        viewState.showErrorViewState(firstViewState, secondViewState)
    }

    fun errorMessage(message: String) {
        viewState.showErrorMessage(message)
    }

    fun errorLayoutComponent(image: Int, errorMessage: Int) {
        viewState.showErrorSetView(image, errorMessage)
    }

    fun checkOrientation() {
        viewState.showCheckOrientation()
    }

    private fun connectToVpn() {
        viewState.showDialog(true)
        if (UnifiedSDK.getInstance().backend.isLoggedIn) {
            UnifiedSDK.getInstance().vpn.start(
                SessionConfig.Builder()
                    .withPolicy(AppPolicy.forAll())
                    .withReason(TrackingConstants.GprReasons.M_UI)
                    .withTransport(HydraTransport.TRANSPORT_ID)
                    .withVirtualLocation("ru")
                    .build(), object : CompletableCallback {
                    override fun complete() {
                        updateUI()
                        viewState.showConnectToVpn()
                    }

                    override fun error(error: VpnException) {
                        updateUI()
                        handleError(error)
                        viewState.showDialog(false)
                    }
                })
        } else {
            loginToVpn()
        }
    }


    private fun updateUI() {
        UnifiedSDK.getVpnState(object : Callback<VPNState> {
            override fun success(state: VPNState) {
                when (state) {
                    VPNState.CONNECTED -> viewState.showVpnState(VPNState.CONNECTED)
                    VPNState.PAUSED -> viewState.showVpnState(VPNState.PAUSED)

                }
            }

            override fun failure(error: VpnException) {
                handleError(error)
            }

        })
    }

    private fun loginToVpn() {
        val authMethod = AuthMethod.anonymous()
        UnifiedSDK.getInstance().backend.login(authMethod, object : Callback<User> {
            override fun success(user: User) {
                updateUI()
                connectToVpn()
            }

            override fun failure(error: VpnException) {
                updateUI()
                handleError(error)
                viewState.showDialog(false)

            }

        })
    }


    fun handleError(e: Throwable?) {
        when (e) {
            is NetworkRelatedException -> checkVpnErrorState(
                R.drawable.wifi,
                R.string.no_connection
            )
            is VpnException -> {
                when (e) {
                    is VpnPermissionRevokedException -> {
                        checkVpnErrorState(R.drawable.warning, R.string.no_permission)
                    }
                    is VpnPermissionDeniedException -> {
                        checkVpnErrorState(
                            R.drawable.warning,
                            R.string.cancel_permission
                        )
                    }
                    is HydraVpnTransportException -> {
                        when (e.code) {
                            HydraVpnTransportException.HYDRA_ERROR_BROKEN -> {
                                checkVpnErrorState(R.drawable.warning, R.string.connection_vpn)
                            }
                            HydraVpnTransportException.HYDRA_DCN_BLOCKED_BW -> {
                                checkVpnErrorState(R.drawable.warning, R.string.client_traffic)
                            }
                            else -> {
                                checkVpnErrorState(R.drawable.warning, R.string.error_vpn)
                            }
                        }
                    }
                }
            }
            is HttpsURLConnection -> when (e.content) {
                RequestException.CODE_NOT_AUTHORIZED -> {
                    checkVpnErrorState(
                        R.drawable.warning,
                        R.string.user_unauthorized
                    )
                }
                RequestException.CODE_TRAFFIC_EXCEED -> {
                    checkVpnErrorState(
                        R.drawable.warning,
                        R.string.server_unavailable
                    )
                }
                else -> {
                    checkVpnErrorState(R.drawable.warning, R.string.another_error)
                }

            }
            is WrongStateException -> {
                updateVpnState()
            }


        }

    }




    private fun checkVpnErrorState(errorImage: Int, errorMessage: Int) {
        viewState.showCheckOrientation()
        viewState.showErrorViewState(View.VISIBLE, View.GONE)
        viewState.showErrorSetView(errorImage, errorMessage)
        viewState.showStateReconnectFlag(true)
    }


}