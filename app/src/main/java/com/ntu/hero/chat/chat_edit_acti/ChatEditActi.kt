package com.ntu.hero.chat.chat_edit_acti

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.ntu.hero.R
import com.ntu.hero.databinding.ChatEditActiBinding
import com.ntu.hero.global.BaseActivity
import com.ntu.hero.global.GlobalVars

class ChatEditActi : BaseActivity() {
    lateinit var binding: ChatEditActiBinding

    private var btmBarFrag: Fragment? = null

    // vars from selected msg to edit
    val nonBoldedText = ObservableField<String>()
    val boldedText = ObservableField<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setup main layout
        binding = DataBindingUtil.setContentView(this, R.layout.chat_edit_acti)
        binding.data = this

        // setup toolbar
        setupBackText()

        // get vals from intent
        val question = intent.getStringExtra("question")
        val answer = intent.getStringExtra("answer")
        val questionUID = intent.getStringExtra("questionUID")
        val uuid = intent.getStringExtra("uuid")

        Log.d(GlobalVars.TAG1, "ChatEditActi: onCreate quid = $questionUID, uuid = $uuid")

        // setup obs
        nonBoldedText.set(question)
        boldedText.set(answer)

        // navigate to frag based on question type
//        ChatActiHelper(this).navigateToFrag()

        // setup btm bar frag
        btmBarFrag = supportFragmentManager.findFragmentById(R.id.btm_frag)
    }

    //===== databinding funcs

    //===== funcs


    //===== interface funcs


    //===== http funcs

    //===== retro responses

    //===== override funcs

}
