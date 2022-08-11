package com.ntu.hero.api.api_models

import com.google.gson.annotations.SerializedName

data class Reg2PhoneNumberModel(

    // body
    @field:SerializedName("phone_prefix")
    val countryCode: String,

    @field:SerializedName("phone_number")
    val phoneNumber: String,

    @field:SerializedName("sms_token")
    val smsToken: String,

    @field:SerializedName("resend_counter")
    val resendCounter: Int,

    // resp
    @field:SerializedName("registration_token")
    val regToken: String? = null
) {
}