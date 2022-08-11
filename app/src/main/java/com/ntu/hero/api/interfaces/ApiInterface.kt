package com.ntu.hero.api.interfaces

import com.ntu.hero.api.api_models.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


/**
 * Created by jay on 16/10/18.
 */

interface ApiInterface {
    //===== REGISTRATION
    // reg1 - key in access code given by NTU
    @POST("v1/auth/access_code")
    fun postAccessCode(@Body accessCodeModel: Reg1AccessCodeModel): Call<Reg1AccessCodeModel>

    // reg 2 - post phone number to server to get OTP
    @POST("v1/auth/sms")
    fun postPhoneNumber(@Body phoneNumberModel: Reg2PhoneNumberModel): Call<Reg2PhoneNumberModel>

    // reg 3 - post OTP to server with reg dets and complete registration
    @POST("v1/auth/register")
    fun postRegistration(@Body regModel: Reg3RegistrationModel): Call<Reg3RegistrationModel>

    // reg 3 - 6-digit pincode
    @POST("v1/user/create_passcode")
    fun postPincode(@Header("Authorization") header: String, @Body body: PincodeModel): Call<PincodeModel>

    // reg 4 - post user profile
    @Multipart
    @POST("v1/user/update_profile")
    fun postProfile(
        @Header("Authorization") header: String,
        @Part profileName: MultipartBody.Part,
        @Part uploadCode: MultipartBody.Part,
        @Part profileImg: MultipartBody.Part
    ): Call<BasicResponseModel>

    // login - same as register endpoint, without sms and reg token
    @POST("v1/auth/login")
    fun postLogin(@Body loginModel: Reg3RegistrationModel): Call<Reg3RegistrationModel>


    //===== CHATBOT
    // get questionnaire first question
    @GET("v1/questionnaire/start")
    fun getInitialQuestions(@Header("Authorization") header: String): Call<List<ChatRoomModel>>

    // post answer to get next question
    @POST("v1/questionnaire/response")
    fun postAnswer(@Header("Authorization") header: String, @Body requestBody: ChatRoomModel): Call<List<ChatRoomModel>>

    // post answer to get next question
    @POST("v1/questionnaire/review/")
    fun postToReview(@Header("Authorization") header: String, @Body requestBody: ChatRoomReviewModel): Call<List<ChatRoomReviewModel>>

    //===== DATA COLLECTION
    @POST("v1/data/upload")
    fun postData(@Header("Authorization") header: String, @Body requestBody: DataUploadModel): Call<DataUploadModel>
}
