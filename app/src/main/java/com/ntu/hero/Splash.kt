package com.ntu.hero

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ntu.hero.global.MPreferences
import com.ntu.hero.home.HomeActi
import com.ntu.hero.registration.login.LoginActi
import com.ntu.hero.registration.reg_0_on_board.RegOnBoardActi
import com.ntu.hero.registration.reg_1_access_code.RegAccessCodeActi
import com.ntu.hero.registration.reg_2_create_acc.RegCreateAccActi
import com.ntu.hero.registration.reg_3_quick_login.RegQuickLoginActi
import com.ntu.hero.registration.reg_4_profile.RegProfileActi

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        // go to main acti depending on pref
        launchActiAndFinish()
    }

    //===== functions
    private fun launchActiAndFinish() {
        var regStage = MPreferences.getRegStageInt()
//        regStage = 3

        // regStage is tied to activity
        val intent: Intent
        when (regStage) {
            //=== registration
            0 -> {
                intent = Intent(this, RegOnBoardActi::class.java)
            }

            1 -> {
                intent = Intent(this, RegAccessCodeActi::class.java)
            }

            2 -> {
                intent = Intent(this, RegCreateAccActi::class.java)
            }

            3 -> {
                intent = Intent(this, RegQuickLoginActi::class.java)
            }

            4 -> {
                intent = Intent(this, RegProfileActi::class.java)
            }

            //=== home
            5 -> {
                intent = Intent(this, HomeActi::class.java)
            }

            //=== logged out (go to login page)
            6 -> {
                intent = Intent(this, LoginActi::class.java)
            }

            else -> {
                intent = Intent(this, RegOnBoardActi::class.java)
            }
        }
        startActivity(intent)

        finish()
    }
}