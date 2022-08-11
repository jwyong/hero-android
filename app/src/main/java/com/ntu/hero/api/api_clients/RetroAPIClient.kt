package com.ntu.hero.api.api_clients

/* Created by jayyong on 16/10/2018. */

import com.ntu.hero.api.interfaces.ApiInterface
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetroAPIClient {
    val BASE_URL = "https://hero.soapptech.com/api/"

    private var retrofit: Retrofit
    private var apiInterface: ApiInterface

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(RetroOkClient().okHttpClient)
                .build()

        apiInterface = retrofit.create(ApiInterface::class.java)

    }


    val api: ApiInterface
        get() {
            return apiInterface
        }
}
