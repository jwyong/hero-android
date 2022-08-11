package com.ntu.hero.chat.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.ntu.hero.R
import com.ntu.hero.api.api_models.ChatItemModel
import com.ntu.hero.chat.ChatActi
import com.ntu.hero.chat.ChatActiInterface
import com.ntu.hero.chat.ChatSubmitActi
import com.ntu.hero.databinding.ChatFragBtnsHoriBinding

class ChatFragBtnsHori : Fragment() {
    private lateinit var binding: ChatFragBtnsHoriBinding

    // obs
    val obsBtn1Text = ObservableField<String>()
    val obsBtn2Text = ObservableField<String>()

    // for posting
    private var btn1IndexStr: String? = null
    private var btn2IndexStr: String? = null

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
            DataBindingUtil.inflate(inflater, R.layout.chat_frag_btns_hori, container, false)
        binding.data = this

        // set btn text and index (based on backend)
        obsBtn1Text.set(arguments?.getString("btn1Text"))
        obsBtn2Text.set(arguments?.getString("btn2Text"))

        btn1IndexStr = arguments?.getString("btn1Index")
        btn1IndexStr = arguments?.getString("btn2Index")

        return binding.root
    }


    //===== databinding funcs
    fun btn1Onclick(_v: View) { // yes btn
        // do different func if is back btns
        if (arguments!!.containsKey("actionInt")) { // got actions
            chatActiInterface.navigateTo(R.id.action_to_emptyFrag, null)

            var delayedAction: Runnable? = null
            when (arguments!!.getInt("actionInt")) { // front-end actions
                0 -> { // back btn
                    delayedAction = backBtn1Func()
                }

                1 -> { // set colour
                    delayedAction = setColourBtn1Func()
                }

                2 -> { // review - post to server, no delay
                    reviewBtn1Func()
                    return
                }
            }

            // do action after delay
            Handler().postDelayed({
                delayedAction?.run()
            }, 1000)

        } else { // normal post funcs
            postBtnFunc(btn1IndexStr!!)
        }
    }

    fun btn2Onclick(_v: View) { //no btn
        if (arguments!!.containsKey("actionInt")) { // got actions
            // navigate to empty frag first
            chatActiInterface.navigateTo(R.id.action_to_emptyFrag, null)

            when (arguments!!.getInt("actionInt")) {
                0 -> { // back btn
                    backBtn2Func()
                }

                1 -> { // set colour
                    setColourBtn2Func()
                }

                2 -> { // review
                    reviewBtn2Func()
                    return
                }
            }

        } else { // normal actions
            postBtnFunc(btn2IndexStr!!)
        }
    }

    //===== funcs
    // post funcs
    private fun postBtnFunc(btnIndexStr: String) {
        // post to retro for next question
        chatActiInterface.postAnswerForNextQues(
            listOf(
                btnIndexStr // send selected number to server
            ), null, true
        )
    }

    //=== btn1 funcs
    private fun backBtn1Func(): Runnable {
        chatActiInterface.addToMsgList(
            ChatItemModel(
                nonBoldedText = getString(R.string.yes_sure),
                isOutgoingInt = 1
            )
        )

        chatActiInterface.addToMsgList(
            ChatItemModel(
                nonBoldedText = getString(R.string.chat_exit_hero_bye),
                isOutgoingInt = 0
            )
        )

        // finish activity
        return Runnable { chatActiInterface.finishActi() }
    }

    private fun setColourBtn1Func(): Runnable {
        chatActiInterface.addToMsgList(
            ChatItemModel(
                nonBoldedText = getString(R.string.yes_please),
                isOutgoingInt = 1
            )
        )

        // add msg and go to colours selection
        return Runnable {
            chatActiInterface.addToMsgList(
                ChatItemModel(
                    nonBoldedText = getString(R.string.chat_setup_theme_2),
                    isOutgoingInt = 0
                )
            )

            chatActiInterface.navigateTo(
                R.id.action_to_chatFragCirclesHori,
                null
            )
        }
    }

    // edit review - post to review endpoint
    private fun reviewBtn1Func() {
        // post to retro for review
        chatActiInterface.retroReview()
    }


    //=== btn2 funcs
    private fun backBtn2Func() {
        // show continue msg
        chatActiInterface.addToMsgList(
            ChatItemModel(
                isOutgoingInt = 1,
                nonBoldedText = getString(R.string.no_stay)
            )
        )

        chatActiInterface.addToMsgList(
            ChatItemModel(
                isOutgoingInt = 0,
                nonBoldedText = getString(R.string.chat_continue_hero)
            )
        )

        // reset back btn boolean
        chatActiInterface.resetBackBtn()

        // resume questionnaire
        chatActiInterface.getInitialOrPrevQues()
    }

    private fun setColourBtn2Func() {
        chatActiInterface.addToMsgList(
            ChatItemModel(
                nonBoldedText = getString(R.string.no_thanks),
                isOutgoingInt = 1
            )
        )

        // add msg and then post
        chatActiInterface.getInitialOrNextQues(true)
    }

    private fun reviewBtn2Func() {
        // post submit action
        val intent = Intent(context, ChatSubmitActi::class.java)
        startActivity(intent)
    }

}