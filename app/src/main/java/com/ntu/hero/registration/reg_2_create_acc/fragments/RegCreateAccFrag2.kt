package com.ntu.hero.registration.reg_2_create_acc.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.ntu.hero.BuildConfig
import com.ntu.hero.R
import com.ntu.hero.api.api_clients.RetroAPIClient
import com.ntu.hero.api.api_models.Reg3RegistrationModel
import com.ntu.hero.databinding.Reg2CreateAccFrag2Binding
import com.ntu.hero.global.*
import com.ntu.hero.registration.reg_3_quick_login.RegQuickLoginActi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegCreateAccFrag2 : Fragment() {
    private lateinit var binding: Reg2CreateAccFrag2Binding

    private val miscHelper = MiscHelper()
    private val api = RetroAPIClient.api

    lateinit var phoneNumber: String
    lateinit var phoneNumberTxt: String

    // for resend countdown
    private val regCountdownTimer = CountdownHelper(30) // set countdown timer int here
    val isResendBtnEnabled = ObservableBoolean()

    // for numpad
    private val numpadHelper = NumpadHelper(4) // set max otp digits here
    var obsBox1 = ObservableField<String>()
    var obsBox2 = ObservableField<String>()
    var obsBox3 = ObservableField<String>()
    var obsBox4 = ObservableField<String>()

    // loading screen obs
    var obsShowLoading = ObservableBoolean(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.reg_2_create_acc_frag_2, container, false)
        binding.data = this

        // get phone number from pref
        val countryCode = MPreferences.getValue(GlobalVars.PREF_COUNTRY_CODE)
        val phone = MPreferences.getValue(GlobalVars.PREF_PHONE)
        phoneNumber = "+$countryCode $phone"
        phoneNumberTxt = "${getString(R.string.reg_2_2_desc)} $phoneNumber"

        // setup observer
        setupNumpadObs()
        setupTimerFuncs()

        return binding.root
    }

    private fun setupNumpadObs() {
        // function for registering as soon as user types 4 digits otp
        numpadHelper.setupObs(retroRegister, 0)

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
    }

    // funcs for countdown timer
    private fun setupTimerFuncs() {
        isResendBtnEnabled.set(true)

        // observe countdown helper
        regCountdownTimer.obsIsBtnEnabled.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                isResendBtnEnabled.set(regCountdownTimer.obsIsBtnEnabled.get())
            }
        })
    }

    //===== retro funcs
    // function for register with retro
    private val retroRegister = Runnable {
        obsShowLoading.set(true)

        val smsCode = "${obsBox1.get()}${obsBox2.get()}${obsBox3.get()}${obsBox4.get()}"
        val regToken = MPreferences.getValue(GlobalVars.PREF_REG_TOKEN)
        val email = MPreferences.getValue(GlobalVars.PREF_EMAIL)
        val pword = arguments?.getString("pword")
        val deviceID =
            Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
        val deviceModel = Build.MODEL
        val appVersion = BuildConfig.VERSION_NAME

        Log.d(GlobalVars.TAG1, "RegCreateAccFrag0, retroRegister: ")

        api.postRegistration(
            Reg3RegistrationModel(
                smsCode, regToken!!, email!!,
                pword!!, deviceID, deviceModel, appVersion, "pushToken"
            )
        ).enqueue(object :
            Callback<Reg3RegistrationModel> {
            override fun onResponse(
                call: Call<Reg3RegistrationModel>,
                response: Response<Reg3RegistrationModel>
            ) {
                if (!response.isSuccessful) {
                    retroRespUnsuc(response)
                    return
                }

                retroRespSuccess(response)

            }

            override fun onFailure(call: Call<Reg3RegistrationModel>, t: Throwable) {
                retroFailure(t)
            }
        })
    }

    //===== retro responses
    private fun retroRespSuccess(response: Response<Reg3RegistrationModel>) {
        obsShowLoading.set(false)

        Log.d(GlobalVars.TAG1, "RegCreateAccFrag2, onResponse: success = $response")
        val respBody = response.body()

        // save info to pref
        MPreferences.save(GlobalVars.PREF_TOKEN_TYPE, respBody?.tokenType ?: "")
        MPreferences.save(GlobalVars.PREF_ACCESS_TOKEN, respBody?.accessToken ?: "")
        MPreferences.saveint(
            GlobalVars.PREF_ACCESS_TOKEN_EXPIRES_IN,
            respBody?.accTokenExpiresIn ?: 0
        )
        MPreferences.save(GlobalVars.PREF_REFRESH_TOKEN, respBody?.refreshToken ?: "")

        // go to next acti
        val intent = Intent(context, RegQuickLoginActi::class.java)
        startActivity(intent)

        (context as Activity).finish()
    }

    private fun retroRespUnsuc(response: Response<*>) {
        // update UI
        obsShowLoading.set(false)

        miscHelper.handleUnsuccError(context, "retroRegister", response)
    }

    private fun retroFailure(throwable: Throwable) {
        // update UI
        obsShowLoading.set(false)

        miscHelper.handleFailureError(context, "retroRegister", throwable)
    }

    //===== databinding funcs
    fun numpadOnClick(v: View) {
        numpadHelper.numpadOnClick((v as TextView).text.toString())
    }

    fun delOnClick(_v: View) {
        numpadHelper.delOnClick()
    }

    fun resendBtn(_v: View) {
        // start countdown timer
        regCountdownTimer.startCountdown()

        // just go to login acti
        val toastTxt = "${getString(R.string.otp_resent)} $phoneNumber"

        MiscHelper().toastMsgStr(context, toastTxt, Toast.LENGTH_SHORT)
    }

    override fun onDestroy() {
        regCountdownTimer.stopCountdown(true)

        super.onDestroy()
    }
}