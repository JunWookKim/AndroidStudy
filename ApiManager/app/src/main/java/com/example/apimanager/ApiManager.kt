package com.example.apimanager

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class ApiManager private constructor(){

    companion object{
        val instance = ApiManager()
    }

    var isLogin = false
    var sSchool : String? = null
    var sCode : String? = null
    var sName : String? = null
    var lessons = mutableListOf<LessonInfo>()

    fun login(sSchool: String, sName: String, sGrade: String, sClass: String, sNumber: String, callback: Callback<LoginResponse>){
        IRetrofitService.create().login(LoginRequest(sSchool, sName, sGrade, sClass, sNumber)).enqueue(callback)
    }

    fun weekAllLesson(sSchool: String?, sCode: String?, callback: Callback<WeekAllLessonResponse>){
        IRetrofitService.create().weekAllLesson(WeekAllLessonRequest(sSchool, sCode)).enqueue(callback)
    }

    interface IRetrofitService{
        @POST("login")
        fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

        @POST("week-all-lesson")
        fun weekAllLesson(@Body weekAllLessonRequest: WeekAllLessonRequest): Call<WeekAllLessonResponse>

        companion object{
            private const val BASE_URL = "http://3.35.4.229:5006/app/"
            fun create() : IRetrofitService {
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