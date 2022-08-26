package com.example.flowpractice

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface IRetrofitService {
    @GET("ticker/ALL_KRW")
    suspend fun getData(): ResponseModel

    companion object {
        private const val BASE_URL = "https://api.bithumb.com/public/"

        fun create(): IRetrofitService {
            val gson = GsonBuilder().setLenient().create()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(IRetrofitService::class.java)
        }
    }
}