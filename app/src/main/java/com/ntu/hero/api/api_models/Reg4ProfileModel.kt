package com.ntu.hero.api.api_models

import com.google.gson.annotations.SerializedName

data class Reg4ProfileModel(
    // body
    @field:SerializedName("profile_name")
    val smsCode: String,

    @field:SerializedName("profile_picture")
    val regToken: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("device_unique_id")
    val deviceID: String,

    @field:SerializedName("device_description")
    val deviceModel: String,

    @field:SerializedName("app_version")
    val appVersion: String,

    @field:SerializedName("push_token")
    val pushToken: String,

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
    val refreshToken: String? = null
) {
}