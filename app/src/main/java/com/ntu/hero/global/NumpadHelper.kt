package com.ntu.hero.global

import android.os.Handler
import androidx.databinding.Observable
import androidx.databinding.ObservableField

class NumpadHelper(val otpMaxChars: Int) {
    var obsInputTxt = ObservableField<String>()
    var obsBox1 = ObservableField<String>()
    var obsBox2 = ObservableField<String>()
    var obsBox3 = ObservableField<String>()
    var obsBox4 = ObservableField<String>()
    var obsBox5 = ObservableField<String>()
    var obsBox6 = ObservableField<String>()

    // for pincode confirmation

    fun setupObs(completeFunc: Runnable, funcDelayMillis: Long) {
        obsInputTxt.set("")
        obsInputTxt.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val currentTxt = obsInputTxt.get() ?: ""
                val length = currentTxt.length

                when (length) {
                    6 -> {
                        obsBox6.set(currentTxt.substring(5, 6))

                        // send otp when full length (4 digits)
                        if (otpMaxChars == 6) {
                            Handler().postDelayed({
                                completeFunc.run()
                            }, funcDelayMillis)
                        }
                    }

                    5 -> {
                        obsBox5.set(currentTxt.substring(4, 5))
                        obsBox6.set("")
                    }

                    4 -> {
                        obsBox4.set(currentTxt.substring(3, 4))

                        // send otp when full length (4 digits)
                        if (otpMaxChars == 4) {
                            Handler().postDelayed({
                                completeFunc.run()
                            }, funcDelayMillis)
                        } else {
                            obsBox5.set("")
                        }
                    }

                    3 -> {
                        obsBox3.set(currentTxt.substring(2, 3))
                        obsBox4.set("")
                    }

                    2 -> {
                        obsBox2.set(currentTxt.substring(1, 2))
                        obsBox3.set("")
                    }

                    1 -> {
                        obsBox1.set(currentTxt.substring(0, 1))
                        obsBox2.set("")
                    }

                    0 -> {
                        obsBox1.set("")
                        obsBox2.set("")
                        obsBox3.set("")
                        obsBox4.set("")
                        obsBox5.set("")
                        obsBox6.set("")
                    }
                }
            }

        })
    }

    fun numpadOnClick(numpadTxt: String) {
        val currentTxt = obsInputTxt.get() ?: ""

        if (currentTxt.length < otpMaxChars) { // only add text if less than max chars
            obsInputTxt.set("$currentTxt$numpadTxt")
        }
    }

    fun delOnClick() {
        val currentTxt = obsInputTxt.get() ?: ""

        if (currentTxt.isNotEmpty()) {
            val str = currentTxt.substring(0, currentTxt.length - 1)

            obsInputTxt.set(str)
        }
    }
}