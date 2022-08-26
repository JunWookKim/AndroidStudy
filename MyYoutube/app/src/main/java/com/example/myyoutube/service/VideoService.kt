package com.example.myyoutube.service

import com.example.myyoutube.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET
import java.util.*

interface VideoService {
    @GET("v3/54bd04e6-07e2-4acb-9d3a-fd3ed170cdb9")
    fun listVideos() : Call<VideoDto>
}