package com.ntu.hero.services

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MiscHelper
import com.ntu.hero.sql.DatabaseHelper
import com.ntu.hero.sql.entity.AppUsage

class AccessibilitySrv : AccessibilityService() {
    private val miscHelper = MiscHelper()
    private val mHandler = Handler()
    private var inputText: String? = null
    private var showingApp: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        when (event.eventType) {
            // for app usage time
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> if (event.packageName != null) {
                val packageName = event.packageName.toString()

                // only do func if open different app
                if (packageName != showingApp) {
                    // TEMP - toast msg
                    Log.d(GlobalVars.TAG1, "AccessibilitySrv: onAccessibilityEvent you opened: $packageName")
//                    miscHelper.toastMsgStr(this, "You opened: $packageName", Toast.LENGTH_SHORT)

                    // check and add new item to sqlite
                    DatabaseHelper.checkAndInsertAU(AppUsage(auPackageName = packageName, auTimeStart = System.currentTimeMillis()))
                }

                // set packageName to currently showing app
                showingApp = packageName
            }

            // for keyboard input - ONLY in hero app (chatbot)
//            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
//                inputText = event.text.toString()
//
//                mHandler.removeCallbacksAndMessages(null)
//                mHandler.postDelayed({ toast("You typed: $inputText in $showingApp") }, 500)
//            }

            else -> {
            }
        }
    }

    override fun onInterrupt() {

    }

    private fun prevEqualstoCurrentAppChecker(cur: String): Boolean {
        return cur == showingApp
    }

    internal fun packageChecker(packageName: String) {
        if ("com.facebook.katana" == packageName || "com.instagram.android" == packageName
            || "com.whatsapp" == packageName || "com.google.android.youtube" == packageName
        ) {
            if (!prevEqualstoCurrentAppChecker(packageName)) {
                newAppOpenedAction(packageName)
            }
        }
        updatePreviousApp(packageName)

    }

    internal fun newAppOpenedAction(packageName: String) {
        Log.d(GlobalVars.TAG1, "AccessibilitySrv, newAppOpenedAction: packageName = $packageName")
//        HeroApp.getDatabase().dao()
//            .insertAppUsage(AppUsage(packageName, System.currentTimeMillis()))
    }

    internal fun updatePreviousApp(packageName: String) {
        if ("com.facebook.katana" == showingApp || "com.instagram.android" == showingApp
            || "com.whatsapp" == showingApp || "com.google.android.youtube" == showingApp
        ) {
            if (!prevEqualstoCurrentAppChecker(packageName)) {
                Log.d(
                    GlobalVars.TAG1,
                    "AccessibilitySrv, updatePreviousApp: packageName = $packageName"
                )

//                HeroApp.getDatabase().dao().updateAppUsage(
//                    HeroApp.getDatabase().dao().getAppUsageLastIndex(), System.currentTimeMillis()
//                )
            }
        }
    }
}
