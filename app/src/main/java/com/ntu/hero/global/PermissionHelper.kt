package com.ntu.hero.global

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.ntu.hero.R
import com.ntu.hero.services.AccessibilitySrv

class PermissionHelper {
    // helpers
    private val miscHelper = MiscHelper()
    private val uiHelper = UIHelper()

    // check if got permissions based on array of permissions strings
    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    // check if accessibility is on
    fun isAccessibilitySettingsOn(context: Context): Boolean {
        Log.d(GlobalVars.TAG1, "PermissionHelper: isAccessibilitySettingsOn ")

        var accessibilityEnabled = 0
        val service = context.packageName + "/" + AccessibilitySrv::class.java.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                context.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
//            miscHelper.toastMsgInt(context, R.string.no_access_srv, Toast.LENGTH_LONG)

//            return false
        }

        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')

        if (accessibilityEnabled == 1) { // accessibility enabled
            val settingValue = Settings.Secure.getString(
                context.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )

            // check if correct accessibility settings enabled
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()

                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }

        // accessibility not enabled, show dialog and return false - have to wait for user to enable
        // manually from settings then recheck
        val positiveAction = Runnable {
            var intent = Intent(context, AccessibilitySrv::class.java)
            context.startService(intent)

            intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            context.startActivity(intent)

        }

        uiHelper.dialog2btn(
            context,
            R.string.access_srv_perm,
            R.string.settings,
            R.string.cancel,
            positiveAction,
            null,
            true
        )

        return false
    }

    // show dialogs asking for permissions, then ask based on user's input
    fun askForPermissions(
        context: Context,
        fragment: Fragment?,
        permissions: Array<String>,
        requestCode: Int,
        permPopupBody: Int?,
        negativeAction: Runnable?
    ) {
        var dialog: Dialog? = null

        val positiveAction = Runnable {
            if (fragment != null) { // is fragment
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fragment.requestPermissions(permissions, requestCode)

                } else {
                    Log.e(GlobalVars.TAG1, "PermissionHelper, askForPermissions: API 21")
                }
            } else {
                ActivityCompat.requestPermissions(context as Activity, permissions, requestCode)
            }
            dialog?.dismiss()
        }

        if (permPopupBody == null) { // null means no need show dialog
            positiveAction.run()
        } else {
            dialog = uiHelper.dialog2btn(
                context,
                permPopupBody,
                R.string.ok,
                R.string.cancel,
                positiveAction,
                negativeAction,
                false
            )
        }
    }

    // process permission results
    fun onPermResults(
        context: Context,
        grantResults: IntArray,
        permDeniedPopupBodyInt: Int,
        permGrantedFunc: Runnable?
    ) {
        // show "permissions settings" dialog if got false
        if (grantResults.contains(PackageManager.PERMISSION_DENIED)) { // got permission denied, show dialog
            Log.d(GlobalVars.TAG1, "PermissionHelper, onPermResults: denied")

            val positiveAction = Runnable {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", (context as Activity).packageName, null)
                intent.data = uri

                context.startActivity(intent)
            }

            uiHelper.dialog2btn(
                context,
                permDeniedPopupBodyInt,
                R.string.settings,
                R.string.cancel,
                positiveAction,
                null,
                false
            )

        } else { // permission granted, run function
            Log.d(GlobalVars.TAG1, "PermissionHelper, onPermResults: granted")

            permGrantedFunc?.run()
        }
    }
}