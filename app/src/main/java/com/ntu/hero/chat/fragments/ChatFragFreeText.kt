package com.ntu.hero.chat.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import com.ntu.hero.R
import com.ntu.hero.chat.ChatActi
import com.ntu.hero.chat.ChatActiInterface
import com.ntu.hero.databinding.ChatFragFreeTextBinding
import com.ntu.hero.global.GlobalVars
import com.ntu.hero.global.MiscHelper
import com.ntu.hero.global.UIHelper
import com.ntu.hero.global.ValidationHelper
import com.ntu.hero.sql.DatabaseHelper
import com.ntu.hero.sql.entity.KeyboardUsage

class ChatFragFreeText : Fragment() {
    private val uiHelper = UIHelper()
    private val miscHelper = MiscHelper()

    private lateinit var binding: ChatFragFreeTextBinding

    val obsEditText = ObservableField<String>()

    // for validation
    private val validationHelper = ValidationHelper()
    val obsInputType = ObservableInt(InputType.TYPE_CLASS_TEXT)
    val obsIsError = ObservableBoolean()

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
            DataBindingUtil.inflate(inflater, R.layout.chat_frag_free_text, container, false)
        binding.data = this
        binding.chatActi = context as ChatActi

        // set inputtype based on args
        if (arguments!!.getBoolean("needValidate")) {
            obsInputType.set(InputType.TYPE_CLASS_NUMBER)
        }

        // set text change listener
        binding.tietInputField.addTextChangedListener(textWatcher)

//        binding.tietInputField.setOnEditorActionListener(object : TextView.OnEditorActionListener {
//            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
//                Log.d(GlobalVars.TAG1, "ChatFragFreeText: onEditorAction p1 = $p1, keyEvent = $p2")
//
//                return false
//            }
//
//        })

        return binding.root
    }

    //===== funcs
    //=== TextWatcher
    private object textWatcher : TextWatcher {
        val keyboardSessionHandler = Handler()
        var isTyping = false
        var startTime: Long = 0
        var endTime: Long = 0
        var totalKeyStrokes = 0
        var totalTextLength = 0
        var totalBackspaces = 0
        var totalAutocorrect = 0

        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            if (!text.isNullOrEmpty()) { // only do actions if started typing
                // increase key strokes everytime
                totalKeyStrokes++

                if (!isTyping) { // first keystroke - new session
                    // set isTyping to true (start of session)
                    isTyping = true

                    // set start timestamp
                    startTime = System.currentTimeMillis()

                } else { // existing session
                    // determine events
                    if (text.length > totalTextLength + 1) { // current text more than previous text + 1 - autocorrect
                        totalAutocorrect++

                    } else if (text.length < totalTextLength) { // current text less than previous text - backspace
                        totalBackspaces++

                    }

                }

                // set end time first
                endTime = System.currentTimeMillis()

                // set text length each time after done event determination
                totalTextLength = text.length

                Log.d(
                    GlobalVars.TAG1,
                    "textWatcher: onTextChanged totalKeys = $totalKeyStrokes, length = $totalTextLength, back = $totalBackspaces, auto = $totalAutocorrect"
                )

                // reset 5s session countdown
                keyboardSessionHandler.removeCallbacksAndMessages(null)

                // end session if > 5secs idle
                keyboardSessionHandler.postDelayed({
                    endKeyboardSession(false)

                }, 5000)
            }
        }

        // function to end session
        fun endKeyboardSession(needClearCallbacks: Boolean) {
            Log.d(
                GlobalVars.TAG1,
                "textWatcher: postDelayed startTime = $startTime, endTime = $endTime, totalKeys = $totalKeyStrokes, back = $totalBackspaces, auto = $totalAutocorrect"
            )

            if (needClearCallbacks) {
                // reset 5s session countdown
                keyboardSessionHandler.removeCallbacksAndMessages(null)
            }

            // insert data to sqlite
            DatabaseHelper.insertKU(
                KeyboardUsage(
                    keyTimeStart = startTime,
                    keyTimeEnd = endTime,
                    keyTotalAutocorrect = totalAutocorrect,
                    keyTotalBackspaces = totalBackspaces,
                    keyTotalStrokes = totalKeyStrokes
                )
            )

            // reset ALL session data
            isTyping = false
            startTime = 0
            totalKeyStrokes = 0
            totalTextLength = 0
            totalBackspaces = 0
            totalAutocorrect = 0
        }

    }

    private fun validate(): Boolean {
        val isErrorBool: Boolean
        if (arguments!!.getBoolean("needValidate")) { // need validate int
            when (validationHelper.isValidInt(obsEditText.get())) {
                0 -> { // empty
                    miscHelper.toastMsgInt(context, R.string.chat_vali_empty, Toast.LENGTH_SHORT)
                    isErrorBool = true
                }

                1 -> { // validated
                    isErrorBool = false
                }

                2 -> { // not int
                    miscHelper.toastMsgInt(context, R.string.chat_vali_int, Toast.LENGTH_SHORT)
                    isErrorBool = true
                }

                else -> {
                    isErrorBool = false
                }
            }

        } else { // just validate empty
            isErrorBool = validationHelper.isEmpty(obsEditText.get())
            if (isErrorBool) {
                miscHelper.toastMsgInt(context, R.string.chat_vali_empty, Toast.LENGTH_SHORT)
            }
        }

        obsIsError.set(isErrorBool)

        return !isErrorBool
    }


    //===== databinding funcs
    // holder onclick
    fun sendBtnOnClick(v: View) {
        Log.d(GlobalVars.TAG1, "ChatFragFreeText: sendBtnOnClick ")

        // end keyboard session first
        textWatcher.endKeyboardSession(true)

        // validate
        if (validate()) {
            // close keyboard
            uiHelper.hideFragKeyboard(context!!, v)

            // post to retro for next question
            chatActiInterface.postAnswerForNextQues(
                listOf(
                    obsEditText.get().toString() // send selected number to server
                ), null, true
            )
        }
    }
}