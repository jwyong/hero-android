package com.ntu.hero.api.helpers

import android.content.Context
import android.util.Log
import com.ntu.hero.api.api_clients.RetroAPIClient.api
import com.ntu.hero.api.api_models.Reg3RegistrationModel
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MPreferences
import com.ntu.hero.global.MiscHelper
import retrofit2.Response
import java.net.SocketTimeoutException

class RefreshTokenHelper {
    private val miscHelper = MiscHelper()

    @Synchronized
    fun retroRefreshToken(context: Context): Boolean {
        try {
            val response = api.postLogin(
                Reg3RegistrationModel(
                    grant_type = "refresh_token",
                    refreshToken = MPreferences.getValue(GlobalVars.PREF_REFRESH_TOKEN)
                )
            ).execute()

            if (response.isSuccessful) {
                Log.d(GlobalVars.TAG1, "RefreshTokenHelper: retroRefreshToken success")

                return retroRespSuccess(response)

            } else {
                Log.d(
                    GlobalVars.TAG1,
                    "RefreshTokenHelper: retroRefreshToken failure = ${response.errorBody()?.string()}"
                )

                // logout if refresh token expired
                miscHelper.logout(context, null)

                return false
            }
        } catch (e: SocketTimeoutException) {
            Log.e(GlobalVars.TAG1, "RefreshTokenHelper: retroRefreshToken ", e)

            return false
        }
    }

    //===== retro responses
    private fun retroRespSuccess(response: Response<Reg3RegistrationModel>): Boolean {
        val respBody = response.body()

        Log.d(GlobalVars.TAG1, "RefreshTokenHelper: retroRespSuccess respbody = $respBody")

        // save new access token to pref
        MPreferences.save(GlobalVars.PREF_ACCESS_TOKEN, respBody?.accessToken ?: "")
        MPreferences.save(GlobalVars.PREF_REFRESH_TOKEN, respBody?.refreshToken ?: "")

        return true
    }
}