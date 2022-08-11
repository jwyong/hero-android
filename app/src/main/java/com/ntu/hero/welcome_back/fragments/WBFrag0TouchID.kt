package com.ntu.hero.welcome_back.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ntu.hero.R
import com.ntu.hero.databinding.WbFrag0TouchIdBinding
import com.ntu.hero.global.BioMetricsHelper
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MPreferences
import com.ntu.hero.global.MiscHelper
import com.ntu.hero.welcome_back.WelcomeBackActi
import com.ntu.hero.welcome_back.WelcomeBackHelper

class WBFrag0TouchID : Fragment() {
    private lateinit var binding: WbFrag0TouchIdBinding

    val obsShowTouchID = ObservableBoolean()

    // helpers
    private val welcomeBackHelper = WelcomeBackHelper()
    private val bioMetricsHelper = BioMetricsHelper()
    private val miscHelper = MiscHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.wb_frag0_touch_id, container, false)
        binding.data = this

        // ask for touch id (show popup)
        if (MPreferences.getIntValue(GlobalVars.PREF_TOUCH_ID_ENABLED) == 1) {
            showTouchIDPopup()
        }

        setupTouchID()

        return binding.root
    }

    //===== databinding funcs
    fun touchIDBtn(_v: View) {
        showTouchIDPopup()
    }

    fun pinCodeBtn(_v: View) {
        // go to pincode page
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_WBFrag0TouchID_to_WBFrag1PinCode)
    }

    fun loginBtn(_v: View) {
        // go to login page
        miscHelper.logout(context, Runnable {
            (context as Activity).finishAffinity()
        })
    }

    //===== normal funcs
    // disable/enable touch id btn
    private fun setupTouchID() {
        // for btn visibility/enabled state
        when (BioMetricsHelper().phoneSupportsTouchID(context)) {
            0 -> { // no touch hardware, don't show touch ID btn
                obsShowTouchID.set(false)
            }

            else -> { // show btn for other cases
                obsShowTouchID.set(true)
            }
        }
    }

    private fun showTouchIDPopup() {
        val authSuccessFunc = Runnable {
            // toast success
            miscHelper.toastMsgInt(
                context,
                R.string.touch_id_verified,
                Toast.LENGTH_SHORT
            )

            // save pref
            MPreferences.saveLong(GlobalVars.PREF_LAST_ACTIVE, System.currentTimeMillis())

            // finish
            (context as WelcomeBackActi).finish()
        }

        BioMetricsHelper().verifyTouchID(context, authSuccessFunc)
    }

}