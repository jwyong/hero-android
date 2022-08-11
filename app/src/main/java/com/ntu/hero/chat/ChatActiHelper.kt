package com.ntu.hero.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.ntu.hero.R
import com.ntu.hero.api.api_models.AnswerModel
import com.ntu.hero.api.api_models.ChatRoomModel
import com.ntu.hero.global.GlobalVars
import java.io.Serializable

class ChatActiHelper(private val context: Context) {
    // navigate to answer fragment based on question type
    fun navigateToFrag(item: ChatRoomModel) {
        var actionID = R.id.action_to_chatFragFreeText
        val bundle = Bundle()

        // prep required vals
        val ansList = item.answerList!!
        val ansIndexList = item.answerIndex!!

        // filter based on question type
        when (item.questionType) {
            context.getString(R.string.ques_type_age), context.getString(R.string.ques_type_scale) -> { // age and scale - horizontal scroller
                actionID = R.id.action_to_chatFragScrollerHori

                bundle.putString("type", item.questionType)
            }

            context.getString(R.string.ques_type_free_text) -> { // free text - no validation
                actionID = R.id.action_to_chatFragFreeText

            }

            context.getString(R.string.ques_type_numerical) -> { // numerical - go to free text with int validation
                actionID = R.id.action_to_chatFragFreeText

                bundle.putBoolean("needValidate", true)
            }

            context.getString(R.string.ques_type_time) -> { // time - need design, go to free text for now
                actionID = R.id.action_to_chatFragFreeText

            }

            context.getString(R.string.ques_type_boolean) -> { // boolean - 2 hori btns
                actionID = R.id.action_to_chatFragBtnsHori

                bundle.putString("btn1Text", ansList[0])
                bundle.putString("btn1Index", ansIndexList[0])
                bundle.putString("btn2Text", ansList[1])
                bundle.putString("btn2Index", ansIndexList[1])
            }

            context.getString(R.string.ques_type_single_answer) -> { // single answer - multiple vertical btns
                actionID = R.id.action_to_chatFragBtnsVerti

                // create ansModel list
                val ansModelList: List<AnswerModel> = ansIndexList.mapIndexed { index, it ->
                    AnswerModel(it, ansList[index])
                }

                bundle.putSerializable("list", ansModelList as Serializable)
            }

            context.getString(R.string.ques_type_multiple_answer) -> { // multi answer - multiple vertical selections
                actionID = R.id.action_to_chatFragSelVerti

                // create ansModel list
                val ansModelList: List<AnswerModel> = ansIndexList.mapIndexed { index, it ->
                    AnswerModel(it, ansList[index])
                }

                bundle.putSerializable("list", ansModelList as Serializable)
            }

            context.getString(R.string.ques_type_end_boolean) -> { // end boolean (review) - go to hori btns with actionInt = 2
                actionID = R.id.action_to_chatFragBtnsHori

                bundle.putInt("actionInt", 2)
                bundle.putString("btn1Text", context.getString(R.string.review))
                bundle.putString("btn2Text", context.getString(R.string.submit))
            }

            context.getString(R.string.ques_type_statement) -> { // statement - dont navigate
                Log.d(GlobalVars.TAG1, "ChatActiHelper: navigateToFrag statement")
                return
            }

            context.getString(R.string.ques_type_lead) -> { // lead - show as statement for now
                Log.d(GlobalVars.TAG1, "ChatActiHelper: navigateToFrag lead")
                return
            }

            else -> { // generic - go to free text for now
                Log.d(
                    GlobalVars.TAG1,
                    "ChatActiHelper: navigateToFrag questType ELSE = ${item.questionType}"
                )
                actionID = R.id.action_to_chatFragFreeText
            }
        }

        // navigate
        (context as ChatActi).navigateTo(actionID, bundle)
    }
}