package com.ntu.hero.api.api_models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

// http model for question part
data class ChatRoomModel(
    // request
    @field:SerializedName("questionnaire_session_id")
    val sessionID: String? = null,

    @field:SerializedName("question_uid")
    val questionUID: String? = null,

    @field:SerializedName("answer")
    val answerList: List<String>? = null,

    @field:SerializedName("answer_timestamp")
    val answerTimeStamp: String? = null,

    @field:SerializedName("time_taken")
    val answerTimeTakenSec: String? = null,

    @field:SerializedName("uuid")
    val uuid: String? = null,


    // resp
    @field:SerializedName("question_type")
    val questionType: String? = null,

    @field:SerializedName("question")
    val questionBody: String? = null,

    @field:SerializedName("answer_index")
    val answerIndex: List<String>? = null,

    @field:SerializedName("question_timestamp")
    val questionTimeStamp: String? = null,

    @field:SerializedName("previous_question_uid")
    val prevQuesUID: String? = null,

    @field:SerializedName("percentage_complete")
    val percentage: Int = 0
)

// http model for review part
data class ChatRoomReviewModel(
    // request
    @field:SerializedName("questionnaire_session_id") // current session id
    val sessionID: String? = null,

    // resp
    @field:SerializedName("uuid") // uuid of answer as posted by frontend
    val uuid: String? = null,

    @field:SerializedName("answer_codes") // list of answer strs for multi answers - populate this instead of answers if got
    val answerCodes: List<String>? = null,

    @field:SerializedName("answers") // list of single answers - only populate this if no answerCodes
    val answers: List<String>? = null,

    @field:SerializedName("question_uid") // needed for editing
    val questionUID: String? = null,

    @field:SerializedName("question")
    val question: String? = null,

    @field:SerializedName("question_type")
    val questionType: String? = null
    )


// for each chat bubble item (front-end model)
data class ChatItemModel(
    var isEditable: Boolean? = false, // default not editable
    var isOutgoingInt: Int, // msg outgoing or incoming: 0 = in, 1 = out
    var uuid: String? = UUID.randomUUID().toString(), // unique id of question (generated frontend)
    var questionUID: String? = null, // question UID from backend (e.g. 12.5.4 - ONLY unique for set of question)
    var nonBoldedText: String, // question body (non-bold, on top)
    var boldedText: String? = null // answer text (bold, below question, can be null)
)

// for answer btns (with backend index and string)
@Parcelize
data class AnswerModel(
    var answerIndex: String,
    var answer: String
): Parcelable