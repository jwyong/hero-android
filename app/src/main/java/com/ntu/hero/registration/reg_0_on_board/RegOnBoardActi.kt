package com.ntu.hero.registration.reg_0_on_board

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ntu.hero.R

class RegOnBoardActi : AppCompatActivity() {
    private var frag: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.reg_0_on_board_acti)

        frag = supportFragmentManager.findFragmentById(R.id.main_frag)

    }

    // got view pager problem if enabled
//    override fun onBackPressed() {
//        if (Navigation.findNavController(frag?.view!!).navigateUp()) { // close fragment
//
//        } else { // close activity
//            finish()
//
//        }
//    }

}