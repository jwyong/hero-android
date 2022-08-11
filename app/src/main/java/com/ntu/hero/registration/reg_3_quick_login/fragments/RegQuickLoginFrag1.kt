package com.ntu.hero.registration.reg_3_quick_login.fragments

import android.app.Activity
import android.content.Intent
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
import com.ntu.hero.api.api_clients.RetroAPIClient.api
import com.ntu.hero.api.api_models.PincodeModel
import com.ntu.hero.databinding.Reg3QuickLoginFrag1Binding
import com.ntu.hero.global.*
import com.ntu.hero.registration.reg_4_profile.RegProfileActi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegQuickLoginFrag1 : Fragment() {
    private lateinit var binding: Reg3QuickLoginFrag1Binding
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
    private lateinit var textSwitcher: TextSwitcher

    // for touchID
    var obsIsTouchIDAvail = ObservableBoolean()
    var obsIsTouchIDEnabled = ObservableBoolean()

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
                com.ntu.hero.R.layout.reg_3_quick_login_frag_1,
                container,
                false
            )
        binding.data = this

        // setup observer
        setupNumpadObs()

        // setup animation for text
        textSwitcher = binding.tvOtp
        pinCodeHelper.setupTextSwitcherAnimation(context, textSwitcher)

        // setup touchID
        setupTouchID()

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
        // confirm/save code action
        when (pinCodeHelper.createPinCode(numpadHelper.obsInputTxt.get().toString())) {
            1 -> { // first time enter code - show "confirm code"
                obsIsPinWrong.set(false)
                numpadHelper.obsInputTxt.set("")
                textSwitcher.setText(getString(com.ntu.hero.R.string.confirm_pin))
            }

            2 -> { // passed stg 2 - save + post pin code and go to next acti
                obsIsPinWrong.set(false)
                val pinCode = numpadHelper.obsInputTxt.get()

                MiscHelper().toastMsgStr(
                    context,
                    getString(com.ntu.hero.R.string.pin_code_confirmed),
                    Toast.LENGTH_SHORT
                )

                // encrypt and save pin to pref
                EncrpytionHelper().encryptAndSaveStr(pinCode!!, GlobalVars.PREF_PINCODE)

                // post 6-digit pincode to server
                postPincode(pinCode)

                // go to next acti
                val intent = Intent(context, RegProfileActi::class.java)
                startActivity(intent)
                (context as Activity).finish()
            }

            0 -> { // wrong code, reset all
                numpadHelper.obsInputTxt.set("")
                textSwitcher.setText(getString(com.ntu.hero.R.string.pin_code_no_match))

                // animate shake
                obsIsPinWrong.set(true)
                pinCodeHelper.startShakeAnim(binding.tvDot1)
            }
        }
    }

    // for touch id
    fun setupTouchID() {
        // check device touchID status
        when (BioMetricsHelper().phoneSupportsTouchID(context)) {
            0 -> { // no hardware - just disable btn
                obsIsTouchIDAvail.set(false)
            }

            else -> {
                obsIsTouchIDAvail.set(true)
            }
        }

        // check if touchID already enabled by user
        if (MPreferences.getIntValue(GlobalVars.PREF_TOUCH_ID_ENABLED) == 1) {
            obsIsTouchIDEnabled.set(true)

        } else { // hvnt enabled
            obsIsTouchIDEnabled.set(false)

        }
    }

    //===== databinding funcs
    fun numpadOnClick(number: String) {
        numpadHelper.numpadOnClick(number)
    }

    fun delOnClick(_v: View) {
        numpadHelper.delOnClick()
    }

    fun touchIDOnClick(_v: View) {
        BioMetricsHelper().enableDisableTouchID(context, obsIsTouchIDEnabled)
    }

    //===== retro funcs
    // post 6-digit pincode to server
    private fun postPincode(pinCode: String) {
        obsShowLoading.set(true)

        val header = MPreferences.getAccessToken()!!

        api.postPincode(
            header,
            PincodeModel(pinCode)
        ).enqueue(object :
            Callback<PincodeModel> {
            override fun onResponse(
                call: Call<PincodeModel>,
                response: Response<PincodeModel>
            ) {
                if (!response.isSuccessful) {
                    retroRespUnsuc(response)
                    return
                }

                retroRespSuccess(response)

            }

            override fun onFailure(call: Call<PincodeModel>, t: Throwable) {
                retroFailure(t)
            }
        })
    }

    //===== retro responses
    private fun retroRespSuccess(response: Response<PincodeModel>) {
        // set pincode uploaded to 1
        MPreferences.saveint(GlobalVars.PREF_PASSCODE_UPLOADED, 1)

        Log.d(GlobalVars.TAG1, "RegQuickLoginFrag1, retroRespSuccess: resp = ${response.message()}")
    }

    private fun retroRespUnsuc(response: Response<*>) {
        // set pincode uploaded to 0
        MPreferences.saveint(GlobalVars.PREF_PASSCODE_UPLOADED, 0)

        Log.d(GlobalVars.TAG1, "RegQuickLoginFrag1, retroRespUnsuc: resp = ${response.errorBody()?.string()}")

    }

    private fun retroFailure(throwable: Throwable) {
        // set pincode uploaded to 0
        MPreferences.saveint(GlobalVars.PREF_PASSCODE_UPLOADED, 0)

        Log.d(GlobalVars.TAG1, "RegQuickLoginFrag1, retroFailure: t = $throwable")
    }

}