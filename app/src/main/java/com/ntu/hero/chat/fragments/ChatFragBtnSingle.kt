package com.ntu.hero.chat.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.ntu.hero.R
import com.ntu.hero.api.helpers.RefreshTokenHelper
import com.ntu.hero.chat.ChatActi
import com.ntu.hero.chat.ChatActiInterface
import com.ntu.hero.chat.ChatSubmitActi
import com.ntu.hero.databinding.ChatFragBtnSingleBinding

class ChatFragBtnSingle : Fragment() {
    private lateinit var binding: ChatFragBtnSingleBinding
    private val refreshTokenHelper = RefreshTokenHelper()

    // obs
    val obsBtnText = ObservableField<String>()

    // for acti-frag comm
    private lateinit var chatActiInterface: ChatActiInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)

        chatActiInterface = (context as ChatActi)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.chat_frag_btn_single, container, false)
        binding.data = this

        obsBtnText.set(arguments?.getString("btnText"))

        return binding.root
    }

    //===== databinding funcs
    fun btnOnclick(_v: View) {
        if (arguments!!.containsKey("actionInt")) { // got actions
            when (arguments!!.getInt("actionInt")) {
                0 -> { // back to dashboard
                    chatActiInterface.finishActi()
                }

                1 -> { // submit questionnaire
                    val intent = Intent(context, ChatSubmitActi::class.java)
                    startActivity(intent)
                }

                2 -> { // retry (resp failure)
                    // try refresh token first (background thread)
//                    GlobalScope.launch(Dispatchers.IO) {
//                        refreshTokenHelper.retroRefreshToken()

                        chatActiInterface.getInitialOrNextQues(false)
//                    }
                }
            }
        } else { // post normal
            // navigate based on resp (retro in acti)
            val b = Bundle()
            b.putString("btn1Text", "Review")
            b.putString("btn2Text", "Submit")
            chatActiInterface.navigateTo(R.id.action_to_chatFragBtnsHori, b)
        }
    }

    //===== funcs
}