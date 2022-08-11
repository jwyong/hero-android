package com.ntu.hero.chat.fragments.circles_hori

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.ntu.hero.R
import com.ntu.hero.api.api_models.ChatItemModel
import com.ntu.hero.chat.ChatActi
import com.ntu.hero.chat.ChatActiInterface
import com.ntu.hero.databinding.ChatFragCirclesHoriBinding
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MPreferences


class ChatFragCirclesHori : Fragment() {
    private lateinit var binding: ChatFragCirclesHoriBinding

    val obsAdapter = ObservableField<ChatFragCirclesHoriAdapter>()

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
                DataBindingUtil.inflate(
                        inflater,
                        com.ntu.hero.R.layout.chat_frag_circles_hori,
                        container,
                        false
                )
        binding.data = this

        val list = listOf(
                R.color.red,
                R.color.orange1,
                R.color.primaryBG,
                R.color.darkBlue1,
                R.color.black,
                R.color.grey1,
                R.color.lightBlue1,
                R.color.lightOrange1
        )
        obsAdapter.set(ChatFragCirclesHoriAdapter(this, list))

        return binding.root
    }

    //===== funcs


    //===== databinding funcs
    // holder onclick
    fun itemOnClick(selBgColour: Int) {
        // set colour theme to pref
        MPreferences.saveint(GlobalVars.PREF_CHAT_BG_COLOUR, selBgColour)
        val textColor = getTextColour(selBgColour)
        MPreferences.saveint(GlobalVars.PREF_CHAT_TEXT_COLOUR, textColor)

        // update UI color
        chatActiInterface.setThemeColour(selBgColour, textColor)

        // add msg
        chatActiInterface.navigateTo(R.id.emptyFrag, null)
        chatActiInterface.addToMsgList(ChatItemModel(isOutgoingInt = 0, nonBoldedText = getString(R.string.chat_setup_theme_3)))

        // post to retro for next
        // post to initial/next question based on session id
        chatActiInterface.getInitialOrNextQues(true)
    }

    // check if colour is light or dark
    private fun getTextColour(color: Int): Int {
        val textColourInt: Int
        if (ColorUtils.calculateLuminance(color) < 0.5) { // dark bg, white text
            textColourInt = R.color.white
        } else { // light bg, black text
            textColourInt = R.color.black
        }
        return ContextCompat.getColor(context!!, textColourInt)
    }
}