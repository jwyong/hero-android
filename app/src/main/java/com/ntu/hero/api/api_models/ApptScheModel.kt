package com.ntu.hero.api.api_models

import com.google.gson.annotations.SerializedName

data class ApptScheModel(
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
    val prevQuesUID: String? = null
)

// for each sche item
data class ScheItemModel(
        var scheDate: String? = null,
        var scheItemTime: List<String>
)
