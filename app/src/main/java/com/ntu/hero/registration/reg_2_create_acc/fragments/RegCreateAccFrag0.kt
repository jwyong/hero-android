package com.ntu.hero.registration.reg_2_create_acc.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ntu.hero.R
import com.ntu.hero.databinding.Reg2CreateAccFrag0Binding
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MPreferences
import com.ntu.hero.global.ValidationHelper
import com.ntu.hero.registration.login.LoginActi

class RegCreateAccFrag0 : Fragment() {
    private lateinit var binding: Reg2CreateAccFrag0Binding

    var obsEmailText = ObservableField<String>()
    var obsPwordText = ObservableField<String>()

    // form validation
    private val validationHelper = ValidationHelper()
    var obsIsErrorEmail = ObservableBoolean(false)
    var obsIsErrorPword = ObservableBoolean(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.reg_2_create_acc_frag_0, container, false)
        binding.data = this

        // set email if got
        obsEmailText.set(MPreferences.getValue(GlobalVars.PREF_EMAIL))

        return binding.root
    }


    //===== databinding funcs
    fun registerBtn(_v: View) {
        // go to next frag only after validate form
        if (formValidated()) {
            // save email to pref
            val email = obsEmailText.get().toString()
            MPreferences.save(GlobalVars.PREF_EMAIL, email)

            // send all info to next frag
            val b = Bundle()

            b.putString("pword", obsPwordText.get().toString())

            Navigation.findNavController(binding.root)
                .navigate(R.id.action_regCreateAccFrag0_to_regCreateAccFrag1, b)
        }
    }

    fun loginBtn(_v: View) {
        val intent = Intent(context, LoginActi::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
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