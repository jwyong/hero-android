package com.ntu.hero.chat.fragments.btns_verti

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.ntu.hero.R
import com.ntu.hero.api.api_models.AnswerModel
import com.ntu.hero.chat.ChatActi
import com.ntu.hero.chat.ChatActiInterface
import com.ntu.hero.chat.fragments.circles_hori.ChatFragBtnsVertiAdapter
import com.ntu.hero.databinding.ChatFragBtnsVertiBinding

class ChatFragBtnsVerti : Fragment() {
    private lateinit var binding: ChatFragBtnsVertiBinding

    val obsAdapter = ObservableField<ChatFragBtnsVertiAdapter>()

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
            DataBindingUtil.inflate(inflater, R.layout.chat_frag_btns_verti, container, false)
        binding.data = this

        val list = arguments!!.getSerializable("list") as List<AnswerModel>

        obsAdapter.set(ChatFragBtnsVertiAdapter(this, list))

        return binding.root
    }

    //===== funcs


    //===== databinding funcs
    // holder onclick - post answer list
    fun itemOnClick(answerModel: AnswerModel) {
        // post to retro for next question
        chatActiInterface.postAnswerForNextQues(
            listOf(
                answerModel.answerIndex // send selected number to server
            ),
            listOf(answerModel.answer), true
        )
    }
}