package com.example.vkvpn.dilog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.vkvpn.R
import com.example.vkvpn.utils.TextFont.textFontButton
import com.example.vkvpn.utils.TextFont.textFontTextView
import kotlinx.android.synthetic.main.permission_dialog.view.*

class VpnDialog : DialogFragment() {
    private lateinit var callBackVpn: CallBackVpn

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.permission_dialog, null)
        textFont(view)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.next.setOnClickListener {
            callBackVpn.vpnStatus()
            dismiss()
        }
        return view
    }

    fun newInstance(callBackVpn: CallBackVpn) {
        this.callBackVpn = callBackVpn
    }

    private fun textFont(view: View){
        view.message.textFontTextView("Montserrat-Regular.ttf")
        view.next.textFontButton("Montserrat-Bold.ttf")
        view.title.textFontTextView("Montserrat-Regular.ttf")
    }

    interface CallBackVpn {
        fun vpnStatus()
    }
}