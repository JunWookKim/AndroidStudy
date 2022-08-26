package com.example.apimanager

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class LoginRequest(
    val sSchool : String,
    val sName : String,
    val sGrade : String,
    val sClass : String,
    val sNumber : String
)

data class LoginResponse(
    @SerializedName("loginCode")
    val loginCode : Int,
    @SerializedName("sCode")
    val sCode : Int,
    @SerializedName("sSchool")
    val sSchool : String
)

data class WeekAllLessonRequest(
    val sSchool : String?,
    val sCode : String?
)

data class WeekAllLessonResponse(
    @SerializedName("classCode")
    val classCode : String?,
    @SerializedName("rows")
    val lessonInfoList : List<LessonInfo>?
)

@Parcelize
data class LessonInfo(
    val lCode : Int,
    val lName : String,
    val lContent : String,
    val lSubCode : Int,
    val lSubName : String,
    val lURL : String,
    val lDate : String,
    val lHour : String,
    val lStartTime : String,
    val lEndTime : String,
    val classCode : Int,
    val teacherCode : Int,
    val lProgress : Int
) : Parcelable
