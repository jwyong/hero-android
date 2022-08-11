package com.ntu.hero.global

import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextSwitcher
import com.ntu.hero.R

class PinCodeHelper() {
    private val encrpytionHelper = EncrpytionHelper()
    private var pinCode1: String? = null
    private lateinit var shakeAnim: Animation

    // setup animations (text switcher)
    fun setupTextSwitcherAnimation(context: Context?, textSwitcher: TextSwitcher) {
        // for title (enter pin)
//        textSwitcher.setFactory {
//            TextView(
//                ContextThemeWrapper(context, R.style.reg_heading_3_black), null, 0
//            )
//        }

        val inAnim = AnimationUtils.loadAnimation(
            context,
            android.R.anim.slide_in_left
        )
        val outAnim = AnimationUtils.loadAnimation(
            context,
            android.R.anim.slide_out_right
        )
        inAnim.duration = 200
        outAnim.duration = 200
        textSwitcher.inAnimation = inAnim
        textSwitcher.outAnimation = outAnim

        textSwitcher.setCurrentText(context?.getString(R.string.enter_pin))
    }

    // start animation on a view
    fun startShakeAnim(v: View) {
        val shakeAnim = AnimationUtils.loadAnimation(v.context, R.anim.shake)
        shakeAnim.duration = 500
        v.animation = shakeAnim
        v.animate()
    }

    // func for create/update pincode - need to confirm pincode
    fun createPinCode(pincode: String): Int {
        if (pinCode1 == null) { // first time key in code, animate "confirm code"
            pinCode1 = pincode

            // first time enter code, go to confirm code
            return 1

        } else { // confirming code
            val pinCode2 = pincode

            if (pinCode1.equals(pinCode2)) { // same code, save to pref and go to next acti

                return 2
            } else { // not same code, reset whole thing
                pinCode1 = null

                return 0
            }
        }
    }

    // func for checking pincode - welcome screen, etc (check with pref)
    fun checkPinCode(pincode: String): Boolean {
        val prefPincode = encrpytionHelper.decryptStrFromPref(GlobalVars.PREF_PINCODE)

        Log.d(GlobalVars.TAG1, "PinCodeHelper: checkPinCode pincode = $prefPincode")

        return prefPincode.equals(pincode)
    }

}