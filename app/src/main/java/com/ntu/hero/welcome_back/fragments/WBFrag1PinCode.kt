package com.ntu.hero.welcome_back.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextSwitcher
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.ntu.hero.R
import com.ntu.hero.databinding.WbFrag1PinCodeBinding
import com.ntu.hero.global.*
import com.ntu.hero.welcome_back.WelcomeBackActi


class WBFrag1PinCode : Fragment() {
    private lateinit var binding: WbFrag1PinCodeBinding
    private val miscHelper = MiscHelper()

    // for numpad
    private val numpadHelper = NumpadHelper(6) // set max otp digits here
    var obsBox1 = ObservableField<String>()
    var obsBox2 = ObservableField<String>()
    var obsBox3 = ObservableField<String>()
    var obsBox4 = ObservableField<String>()
    var obsBox5 = ObservableField<String>()
    var obsBox6 = ObservableField<String>()

    // for pincode confirmation
    var obsIsPinWrong = ObservableBoolean()
    private val pinCodeHelper = PinCodeHelper()
    private lateinit var textSwitcher: TextSwitcher // for animation

    // obs
    var obsShowLoading = ObservableBoolean(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                com.ntu.hero.R.layout.wb_frag1_pin_code,
                container,
                false
            )
        binding.data = this

        // setup observer
        setupNumpadObs()

        // setup animation for text
        textSwitcher = binding.tvOtp
        pinCodeHelper.setupTextSwitcherAnimation(context, textSwitcher)

        return binding.root
    }

    private fun setupNumpadObs() {
        numpadHelper.setupObs(onComplete6Digit, 100)

        // setup observers between this and helper
        numpadHelper.obsBox1.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                obsBox1.set(numpadHelper.obsBox1.get())
            }
        })

        numpadHelper.obsBox2.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                obsBox2.set(numpadHelper.obsBox2.get())
            }
        })

        numpadHelper.obsBox3.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                obsBox3.set(numpadHelper.obsBox3.get())
            }
        })

        numpadHelper.obsBox4.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                obsBox4.set(numpadHelper.obsBox4.get())
            }
        })

        numpadHelper.obsBox5.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                obsBox5.set(numpadHelper.obsBox5.get())
            }
        })

        numpadHelper.obsBox6.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                obsBox6.set(numpadHelper.obsBox6.get())
            }
        })
    }

    // function for completion of inputting 6-digit pin code
    private val onComplete6Digit = Runnable {
        // verify code actions
        if (pinCodeHelper.checkPinCode(numpadHelper.obsInputTxt.get().toString())) {
            // verified - go to home
            // toast success
            miscHelper.toastMsgInt(
                context,
                R.string.pin_code_verified,
                Toast.LENGTH_SHORT
            )

            // save pref
            MPreferences.saveLong(GlobalVars.PREF_LAST_ACTIVE, System.currentTimeMillis())

            // finish
            (context as WelcomeBackActi).finish()
        } else {
            // not verified - reset and set error text
            numpadHelper.obsInputTxt.set("")
            textSwitcher.setText(getString(R.string.pin_code_invalid))

            // animate shake
            obsIsPinWrong.set(true)
            pinCodeHelper.startShakeAnim(binding.tvDot1)
        }
    }

    //===== databinding funcs
    fun numpadOnClick(number: String) {
        numpadHelper.numpadOnClick(number)
    }

    fun delOnClick(_v: View) {
        numpadHelper.delOnClick()
    }

    fun forgotPinOnClick(_v: View) {
        Log.d(GlobalVars.TAG1, "WBFrag1PinCode, forgotPinOnClick: ")
    }
}
