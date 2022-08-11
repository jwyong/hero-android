package com.ntu.hero.registration.reg_3_quick_login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ntu.hero.R
import com.ntu.hero.global.MPreferences

class RegQuickLoginActi : AppCompatActivity() {
    private var frag: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reg_3_quick_login_acti)

        // save stage int
        MPreferences.setRegStageInt(3)

        frag = supportFragmentManager.findFragmentById(R.id.main_frag)

    }

    override fun onBackPressed() {
        if (Navigation.findNavController(frag?.view!!).navigateUp()) { // close fragment

        } else { // close activity
            finish()

        }
    }

}