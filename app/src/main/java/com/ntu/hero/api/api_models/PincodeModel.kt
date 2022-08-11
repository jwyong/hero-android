package com.ntu.hero.api.api_models

import com.google.gson.annotations.SerializedName

data class PincodeModel(
    // body
    @field:SerializedName("passcode")
    val smsCode: String?,

    // resp
    @field:SerializedName("message")
    val message: String? = null
    ) {
}