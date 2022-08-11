package com.ntu.hero.welcome_back

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ntu.hero.R
import com.ntu.hero.api.helpers.RefreshTokenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WelcomeBackActi : AppCompatActivity() {
    private var frag: Fragment? = null

    // helpers
    private val welcomeBackHelper = WelcomeBackHelper()
    private val refreshTokenHelper = RefreshTokenHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_back_acti)

        frag = supportFragmentManager.findFragmentById(R.id.main_frag)

        GlobalScope.launch(Dispatchers.IO) {
            // refresh token
            refreshTokenHelper.retroRefreshToken(this@WelcomeBackActi)

            // post pincode if not yet
            welcomeBackHelper.checkAndPostPincode(this@WelcomeBackActi)
        }
    }

    override fun onBackPressed() {
        if (Navigation.findNavController(frag?.view!!).navigateUp()) { // close fragment

        } else { // close all activities
            finishAffinity()
        }
    }
}