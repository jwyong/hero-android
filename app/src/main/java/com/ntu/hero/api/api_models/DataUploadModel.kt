package com.ntu.hero.api.api_models

import com.google.gson.annotations.SerializedName

data class DataUploadModel(
    // body
    @field:SerializedName("data_type")
    val dataType: String?,

    @field:SerializedName("data")
    val dataList: List<*>?,

    // resp
    @field:SerializedName("message")
    val message: String? = null
    ) {
}