package com.ntu.hero.registration.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ntu.hero.BuildConfig
import com.ntu.hero.R
import com.ntu.hero.api.api_clients.RetroAPIClient
import com.ntu.hero.api.api_models.Reg3RegistrationModel
import com.ntu.hero.databinding.LoginActiBinding
import com.ntu.hero.global.*
import com.ntu.hero.home.HomeActi
import com.ntu.hero.registration.reg_1_access_code.RegAccessCodeActi
import com.ntu.hero.registration.reg_3_quick_login.RegQuickLoginActi
import com.ntu.hero.registration.reg_4_profile.RegProfileActi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActi : AppCompatActivity() {
    // helpers
    private val miscHelper = MiscHelper()
    private val api = RetroAPIClient.api
    private val permissionHelper = PermissionHelper()

    private lateinit var binding: LoginActiBinding

    var obsEmailText = ObservableField<String>()
    var obsPwordText = ObservableField<String>()

    private val validationHelper = ValidationHelper()
    var obsIsErrorEmail = ObservableBoolean(false)
    var obsIsErrorPword = ObservableBoolean(false)

    // for btn
    val isBtnEnabled = ObservableBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.login_acti)
        binding.data = this

        // set email if got
        obsEmailText.set(MPreferences.getValue(GlobalVars.PREF_EMAIL))
    }

    //===== databinding funcs
    // submit access code to server
    fun loginBtn(_v: View?) {
        if (formValidated()) {
            // save email to pref
            val email = obsEmailText.get().toString()
            MPreferences.save(GlobalVars.PREF_EMAIL, email)

            // post access code to server
            retroLogin(email, obsPwordText.get()!!)
        }
    }

    fun signupBtn(_v: View?) {
        // go to access code acti
        val intent = Intent(this, RegAccessCodeActi::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    //===== Retro funcs
    private fun retroLogin(email: String, pword: String) {
        // update UI
        isBtnEnabled.set(false)

        val deviceID =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val deviceModel = Build.MODEL
        val appVersion = BuildConfig.VERSION_NAME

        api.postLogin(
            Reg3RegistrationModel(
                null, null, email,
                pword, deviceID, deviceModel, appVersion, "pushToken"
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
        val respBody = response.body()
        Log.d(GlobalVars.TAG1, "LoginActi, retroRespSuccess: resp = ${respBody}")

        // save info to pref
        MPreferences.save(GlobalVars.PREF_TOKEN_TYPE, respBody?.tokenType ?: "")
        MPreferences.save(GlobalVars.PREF_ACCESS_TOKEN, respBody?.accessToken ?: "")
        MPreferences.saveint(
            GlobalVars.PREF_ACCESS_TOKEN_EXPIRES_IN,
            respBody?.accTokenExpiresIn ?: 0
        )
        MPreferences.save(GlobalVars.PREF_REFRESH_TOKEN, respBody?.refreshToken ?: "")

        // login resp


        Log.d(GlobalVars.TAG1, "LoginActi, retroRespSuccess: login pincode = ${respBody?.passcode}")
        if (respBody?.passcode.isNullOrBlank()) {
            // no pincode yet, go to quick login
            val intent = Intent(this, RegQuickLoginActi::class.java)
            startActivity(intent)

        } else if (respBody?.profileName.isNullOrBlank()) { // no profile name yet, go profile
            val intent = Intent(this, RegProfileActi::class.java)
            startActivity(intent)

        } else { // got all info, go to home
            // save pincode and set post pincode to 1 (uploaded)
            MPreferences.saveint(GlobalVars.PREF_PASSCODE_UPLOADED, 1)

            // encrypt and save pincode
            EncrpytionHelper().encryptAndSaveStr(respBody?.passcode!!, GlobalVars.PREF_PINCODE)

            // save profile
            MPreferences.save(GlobalVars.PREF_USER_NAME, respBody.profileName!!)
            MPreferences.save(GlobalVars.PREF_USER_PROFILE_IMG, respBody.profilePictureUrl?: "")

            // go to home
            val intent = Intent(this, HomeActi::class.java)
            startActivity(intent)

        }

        finishAffinity()
    }

    private fun retroRespUnsuc(response: Response<*>) {
        // update UI
        isBtnEnabled.set(true)

        miscHelper.handleUnsuccError(this, "retroLogin", response)
    }

    private fun retroFailure(throwable: Throwable) {
        // update UI
        isBtnEnabled.set(true)

        miscHelper.handleFailureError(this, "retroLogin", throwable)
    }

    //===== funcs
    private fun formValidated(): Boolean {
        val emailIsError = validationHelper.isValidEmail(obsEmailText.get()) != 1
        obsIsErrorEmail.set(emailIsError)

        val pwordIsError = validationHelper.isValidPword(obsPwordText.get(), 8, 20) != 1
        obsIsErrorPword.set(pwordIsError)

        return !emailIsError && !pwordIsError
    }
}