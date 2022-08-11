package com.ntu.hero.api.api_models

import com.google.gson.annotations.SerializedName

data class Reg3RegistrationModel(
    // body
    @field:SerializedName("sms_code")
    val smsCode: String? = null,

    @field:SerializedName("registration_token")
    val regToken: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("device_unique_id")
    val deviceID: String? = null,

    @field:SerializedName("device_description")
    val deviceModel: String? = null,

    @field:SerializedName("app_version")
    val appVersion: String? = null,

    @field:SerializedName("push_token")
    val pushToken: String? = null,

    @field:SerializedName("grant_type")
    val grant_type: String? = "password",

    @field:SerializedName("device_type")
    val device_type: String? = "android",

    // resp
    @field:SerializedName("token_type")
    val tokenType: String? = null,

    @field:SerializedName("access_token")
    val accessToken: String? = null,

    @field:SerializedName("access_token_expires_in")
    val accTokenExpiresIn: Int? = null,

    @field:SerializedName("refresh_token")
    val refreshToken: String? = null,

    // resp: for login
    @field:SerializedName("passcode")
    val passcode: String? = null,

    @field:SerializedName("profile_name")
    val profileName: String? = null,

    @field:SerializedName("profile_picture")
    val profilePictureUrl: String? = null
    ) {
}