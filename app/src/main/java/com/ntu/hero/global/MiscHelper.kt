package com.ntu.hero.global

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.work.WorkManager
import com.ntu.hero.R
import com.ntu.hero.registration.login.LoginActi
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException


class MiscHelper {
    // logout from app
    fun logout(context: Context?, extraAction: Runnable?) {
        // set stage int to login
        MPreferences.setRegStageInt(6)

        // cancel all work managers
        WorkManager.getInstance(context!!).cancelAllWork()

        // go to login page
        val intent = Intent(context, LoginActi::class.java)
        context.startActivity(intent)

        // run extra actions if got
        extraAction?.run()
    }


    // for showing error toast from retro
    fun handleUnsuccError(context: Context?, funcName: String, response: Response<*>) {
        Log.d(GlobalVars.TAG1, "MiscHelper: handleUnsuccError resp = $response")

        val respErrorBody = JSONObject(response.errorBody()?.string())

        // log retro error
        Log.d(GlobalVars.TAG1, "MiscHelper, handleUnsuccError: $funcName, error = $response")

        // check if got error code to handle
        if (respErrorBody.has("code")) {
            val errorCode = "e_${respErrorBody["code"]}"
            val strID = context?.resources?.getIdentifier(errorCode, "string", context.packageName)

            if (strID != null && strID != 0) {
                // handle 4124 error: token expired - refresh token
                if (strID.equals("e_4124")) {

                }

                toastMsgInt(context, strID, Toast.LENGTH_LONG)
                return
            }
        }

        // no error code, return generic error toast
        toastMsgInt(context, R.string.onresponse_unsuccessful, Toast.LENGTH_LONG)
    }

    // for handling failure from retro
    fun handleFailureError(context: Context?, funcName: String, throwable: Throwable) {
        retroLogFailure(
            GlobalVars.TAG1,
            funcName,
            throwable
        )
        toastMsgInt(context, R.string.onfailure, Toast.LENGTH_SHORT)
    }

    fun retroLogUnsuc(TAG: String, funcName: String, response: Response<*>) {
        try {
            val errorMsg = "retroUnsuc: $funcName, errorMsg: ${response.errorBody()?.string()}"
            Log.e(TAG, errorMsg)

        } catch (e: IOException) {
            Log.d(TAG, "retroUnsucException: $funcName", e)
        }

    }

    fun retroLogFailure(TAG: String, funcName: String, throwable: Throwable) {
        val errorMsg = "retroFailed: $funcName"
        Log.e(TAG, errorMsg, throwable)

    }

    // toast msg from background thread (string)
    fun toastMsgStr(context: Context?, string: String, duration: Int) {
        if (context == null) {
            return
        }

        if (context is Activity) {
            context.runOnUiThread {
                Toast.makeText(context, string, duration).show()
            }
        } else {
            Toast.makeText(context, string, duration).show()
        }
    }

    // toast msg from background thread (int)
    fun toastMsgInt(context: Context?, string: Int, duration: Int) {
        if (context == null) {
            return
        }

        (context as Activity).runOnUiThread {
            Toast.makeText(context, string, duration).show()
        }
    }


    // open link in web browser
    fun openURLBrowser(context: Context, uriString: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uriString)
        startActivity(context, intent, null)
    }

}
