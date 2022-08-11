package com.ntu.hero.api.api_models

import com.google.gson.annotations.SerializedName

data class Reg1AccessCodeModel(

    @field:SerializedName("access_code")
    val accessCode: String,

    @field:SerializedName("sms_token")
    val smsToken: String? = null
) {
}