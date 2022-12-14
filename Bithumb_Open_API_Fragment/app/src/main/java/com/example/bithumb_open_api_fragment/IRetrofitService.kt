package com.example.bithumb_open_api_fragment

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface IRetrofitService {
    interface IRetrofitService {
        @GET("public/ticker/ALL_KRW")
        suspend fun getData() : ResponseModel

        companion object {
            private const val BASE_URL = "https://api.bithumb.com/"

            fun create() : IRetrofitService{
                val gson : Gson = GsonBuilder().setLenient().create()

                return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(IRetrofitService::class.java)
            }
        }
    }
}