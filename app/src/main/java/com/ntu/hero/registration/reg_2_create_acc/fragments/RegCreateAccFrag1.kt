package com.ntu.hero.registration.reg_2_create_acc.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ntu.hero.R
import com.ntu.hero.api.api_clients.RetroAPIClient
import com.ntu.hero.api.api_models.Reg2PhoneNumberModel
import com.ntu.hero.databinding.Reg2CreateAccFrag1Binding
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MPreferences
import com.ntu.hero.global.MiscHelper
import com.ntu.hero.global.ValidationHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegCreateAccFrag1 : Fragment() {
    private lateinit var binding: Reg2CreateAccFrag1Binding
    private val miscHelper = MiscHelper()
    private val api = RetroAPIClient.api

    val obsPhoneNumber = ObservableField<String>()

    // form validation
    private val validationHelper = ValidationHelper()
    val obsIsErrorPhone = ObservableBoolean()

    // for btn
    val isBtnEnabled = ObservableBoolean(true)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.reg_2_create_acc_frag_1, container, false)
        binding.data = this

        // setup country code picker
        setupCCP()

        // set phone if already got
        obsPhoneNumber.set(MPreferences.getValue(GlobalVars.PREF_PHONE))

        return binding.root
    }

    //===== databinding funcs
    fun sendVerificationBtn(_v: View) {
        if (formValidated()) {
            // save to pref (country code and phone)
            MPreferences.save(GlobalVars.PREF_COUNTRY_CODE, binding.ccp.selectedCountryCode)
            MPreferences.save(GlobalVars.PREF_PHONE, obsPhoneNumber.get() ?: "")

            // update UI
            isBtnEnabled.set(false)

            // post number to get reg_token
            retroPostPhoneNumber()
        }
    }

    fun loginBtn(_v: View) {
        // just go to login acti
    }

    private fun formValidated(): Boolean {
        val phoneIsError = validationHelper.isEmpty(obsPhoneNumber.get())
        obsIsErrorPhone.set(phoneIsError)

        return !phoneIsError
    }

    //===== normal funcs
    private fun setupCCP() {
        val ccp = binding.ccp

        // set font
        val typeface = Typeface.createFromAsset(context!!.assets, "aktiv_grotesk_medium.ttf")
        ccp.setTypeFace(typeface)

        // set selected country if got from pref
        val selCountry = MPreferences.getValue(GlobalVars.PREF_COUNTRY_CODE)
        if (selCountry != null && selCountry.isNotEmpty()) {
            ccp.setDefaultCountryUsingPhoneCode(selCountry.toInt())
        } else {
            ccp.setDefaultCountryUsingPhoneCode(65)
        }
        ccp.resetToDefaultCountry()

    }

    //===== Retro funcs
    private fun retroPostPhoneNumber() {
        val phoneNumber = obsPhoneNumber.get()
        val smsToken = MPreferences.getValue(GlobalVars.PREF_SMS_TOKEN)

        Log.d(
            GlobalVars.TAG1,
            "RegCreateAccFrag1, retroPostPhoneNumber: cCode = ${binding.ccp.selectedCountryCode}, phone = $phoneNumber"
        )

        api.postPhoneNumber(
            Reg2PhoneNumberModel(
                binding.ccp.selectedCountryCode,
                phoneNumber ?: "",
                smsToken ?: "",
                0
            )
        ).enqueue(object :
            Callback<Reg2PhoneNumberModel> {
            override fun onResponse(
                call: Call<Reg2PhoneNumberModel>,
                response: Response<Reg2PhoneNumberModel>
            ) {
                if (!response.isSuccessful) {
                    retroRespUnsuc(response)
                    return
                }
                retroRespSuccess(response)
            }

            override fun onFailure(call: Call<Reg2PhoneNumberModel>, t: Throwable) {
                retroFailure(t)
            }
        })
    }

    //===== retro responses
    private fun retroRespSuccess(response: Response<Reg2PhoneNumberModel>) {
        Log.d(GlobalVars.TAG1, "RegCreateAccFrag1, onResponse: success = ${response.body()}")

        // save reg token to pref
        val regToken = response.body()?.regToken
        MPreferences.save(GlobalVars.PREF_REG_TOKEN, regToken ?: "")

        // send all info to next frag
        val b = arguments!!

        Navigation.findNavController(binding.root)
            .navigate(R.id.action_regCreateAccFrag1_to_regCreateAccFrag2, b)

        // update UI
        isBtnEnabled.set(true)
    }

    private fun retroRespUnsuc(response: Response<*>) {
        // update UI
        isBtnEnabled.set(true)

        miscHelper.handleUnsuccError(context, "retroPostPhoneNumber", response)
    }

    private fun retroFailure(throwable: Throwable) {
        // update UI
        isBtnEnabled.set(true)

        miscHelper.handleFailureError(context, "retroPostPhoneNumber", throwable)
    }
}