package com.ntu.hero.registration.reg_1_access_code

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ntu.hero.R
import com.ntu.hero.api.api_clients.RetroAPIClient
import com.ntu.hero.api.api_models.Reg1AccessCodeModel
import com.ntu.hero.databinding.Reg1AccessCodeActiBinding
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MPreferences
import com.ntu.hero.global.MiscHelper
import com.ntu.hero.registration.login.LoginActi
import com.ntu.hero.registration.reg_2_create_acc.RegCreateAccActi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegAccessCodeActi : AppCompatActivity() {
    // helpers
    private val miscHelper = MiscHelper()
    private val api = RetroAPIClient.api

    private lateinit var binding: Reg1AccessCodeActiBinding
    val obsEditTxt = ObservableField<String>()

    // for btn
    val isBtnEnabled = ObservableBoolean(false)
    val isEditTxtEnabled = ObservableBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // save stage int
        MPreferences.setRegStageInt(1)

        // set observable for inputtxt
        obsEditTxt.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (obsEditTxt.get()?.length == 6) { // got value, can send
                    isBtnEnabled.set(true)
                } else {
                    isBtnEnabled.set(false)
                }
            }

        })

        binding = DataBindingUtil.setContentView(this, R.layout.reg_1_access_code_acti)
        binding.data = this

    }

    //===== databinding funcs
    // submit access code to server
    fun submitBtn(_v: View?) {
        // update UI
        isBtnEnabled.set(false)
        isEditTxtEnabled.set(false)

        // post access code to server
        retroPostAccessCode(obsEditTxt.get().toString())
    }

    fun loginBtn(_v: View) {
        val intent = Intent(this, LoginActi::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    //===== Retro funcs
    private fun retroPostAccessCode(accessCode: String) {
        api.postAccessCode(Reg1AccessCodeModel(accessCode)).enqueue(object :
            Callback<Reg1AccessCodeModel> {
            override fun onResponse(
                call: Call<Reg1AccessCodeModel>,
                response: Response<Reg1AccessCodeModel>
            ) {
                if (!response.isSuccessful) {
                    retroRespUnsuc(response)
                    return
                }

                retroRespSuccess(response)
            }

            override fun onFailure(call: Call<Reg1AccessCodeModel>, t: Throwable) {
                retroFailure(t)
            }
        })
    }

    //===== retro responses
    private fun retroRespSuccess(response: Response<Reg1AccessCodeModel>) {
        // save sms token in pref
        val smsToken = response.body()?.smsToken

        MPreferences.save(GlobalVars.PREF_SMS_TOKEN, smsToken ?: "")

        // go to account creation
        val intent = Intent(this, RegCreateAccActi::class.java)
        startActivity(intent)

        finish()
    }

    private fun retroRespUnsuc(response: Response<*>) {
        // update UI
        isBtnEnabled.set(true)
        isEditTxtEnabled.set(true)

        miscHelper.handleUnsuccError(this, "retroPostCreateProfile", response)
    }

    private fun retroFailure(throwable: Throwable) {
        // update UI
        isBtnEnabled.set(true)
        isEditTxtEnabled.set(true)

        miscHelper.handleFailureError(this, "retroPostCreateProfile", throwable)
    }
}