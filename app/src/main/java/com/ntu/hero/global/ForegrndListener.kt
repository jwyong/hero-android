package com.ntu.hero.global

import android.content.Intent
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.ntu.hero.Hero
import com.ntu.hero.welcome_back.WelcomeBackActi

class ForegrndListener : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Log.d(GlobalVars.TAG1, "ForegrndListener, onStart: ")

        // only need do actions if user logged in and can reach Home (regStage = 5)
        checkWelcomeBack()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {


        if (Hero.instance?.applicationContext is WelcomeBackActi) {
            Log.d(GlobalVars.TAG1, "ForegrndListener: onStop isWelcomeBackActi")
        } else {
            Log.d(GlobalVars.TAG1, "ForegrndListener: onStop is NOT WelcomeBackActi")

        }
    }

    private fun checkWelcomeBack() {
        if (MPreferences.getIntValue(GlobalVars.PREF_REG_STG) == 5) {
            // check if session expired
            val maxExpiryMins = 3
            val lastActiveLong = MPreferences.getLongValue(GlobalVars.PREF_LAST_ACTIVE) ?: 0

            // launch welcome back screen if more than xx mins inactive
            if (System.currentTimeMillis() - lastActiveLong > maxExpiryMins * 60 * 1000) {
                val context = Hero.instance?.applicationContext
                val intent = Intent(context, WelcomeBackActi::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context?.startActivity(intent)
            }
        }
    }
}
