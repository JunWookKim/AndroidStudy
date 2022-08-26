package com.example.myyoutube.model

import com.google.gson.annotations.SerializedName

data class VideoModel(
    val title : String,
    val sources : String,
    val subtitle : String,
    val thumb : String,
    val description : String
)
