package com.ntu.hero.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.ntu.hero.R
import com.ntu.hero.databinding.ChatSubmitActiBinding
import com.ntu.hero.global.BaseActivity
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.home.HomeActi


class ChatSubmitActi : BaseActivity() {
    private lateinit var binding: ChatSubmitActiBinding

    val obsTitleText = ObservableField<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setup main layout
        binding = DataBindingUtil.setContentView(this, R.layout.chat_submit_acti)
        binding.data = this

        // set title
        obsTitleText.set(GlobalVars.chat_submit_title)
    }

    //===== databinding funcs
    fun btnOnClick(_v: View) {
        onBackPressed()
    }


    //===== funcs

    override fun onBackPressed() {
        val intent = Intent(this, HomeActi::class.java)
        startActivity(intent)

        finishAffinity()
    }

}