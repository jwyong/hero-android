package com.ntu.hero.welcome_back

import android.app.Activity
import android.content.Context
import android.util.Log
import com.ntu.hero.api.api_clients.RetroAPIClient
import com.ntu.hero.api.api_models.PincodeModel
import com.ntu.hero.global.EncrpytionHelper
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MPreferences
import com.ntu.hero.global.MiscHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WelcomeBackHelper {
    private val encrpytionHelper = EncrpytionHelper()
    private val miscHelper = MiscHelper()

    //===== funcs
    fun checkAndPostPincode(context: Context) {
        // only need post if pincode is NOT null and hvn't posted yet
        if (MPreferences.getIntValue(GlobalVars.PREF_PASSCODE_UPLOADED) != 1) {
            val pinCode = encrpytionHelper.decryptStrFromPref(GlobalVars.PREF_PINCODE)

            if (pinCode.isNotBlank()) {
                postPincode(pinCode)
            } else {
                miscHelper.logout(context, Runnable {
                    (context as Activity).finishAffinity()
                })
            }
        }
    }

    //===== retro funcs
    // post 6-digit pincode to server
    private fun postPincode(pinCode: String) {
        val header = MPreferences.getAccessToken()!!

        RetroAPIClient.api.postPincode(
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

        Log.d(GlobalVars.TAG1, "WelcomeBackHelper, retroRespSuccess: ")
    }

    private fun retroRespUnsuc(response: Response<*>) {
        // set pincode uploaded to 0
        MPreferences.saveint(GlobalVars.PREF_PASSCODE_UPLOADED, 0)

        Log.d(
            GlobalVars.TAG1,
            "WelcomeBackHelper, retroRespUnsuc: resp = ${response.errorBody()?.string()}"
        )

    }

    private fun retroFailure(throwable: Throwable) {
        // set pincode uploaded to 0
        MPreferences.saveint(GlobalVars.PREF_PASSCODE_UPLOADED, 0)

        Log.d(GlobalVars.TAG1, "WelcomeBackHelper, retroFailure: t = $throwable")
    }
}