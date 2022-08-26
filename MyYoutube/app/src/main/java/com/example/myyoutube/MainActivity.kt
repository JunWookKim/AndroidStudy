package com.example.myyoutube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myyoutube.adapter.VideoAdapter
import com.example.myyoutube.dto.VideoDto
import com.example.myyoutube.service.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var videoAdapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, PlayerFragment()).commit()

        videoAdapter = VideoAdapter(callback = {url, title ->
            // PlayerFragment 에 있는 play 함수를 어떻게 불러올까?
            // supportFragmentManager 에서 모든 fragment 가져옴 -> 그 중 PlayerFragment 인 녀석만 받아와서 -> PlayerFragment 로 형변환 후 내장 함수 호출
            supportFragmentManager.fragments.find{ it is PlayerFragment}?.let{
                (it as PlayerFragment).play(url, title)
            }
        })
        findViewById<RecyclerView>(R.id.mainRecyclerView).apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }

        getVideoList()
    }

    private fun getVideoList(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also{
            it.listVideos()
                .enqueue(object : Callback<VideoDto> {
                    override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                        if (response.isSuccessful.not()){
                            Log.d("MainActivity", "response fail")
                        }

                        response.body()?.let{videoDto ->
                            videoAdapter.submitList(videoDto.videos)
                        }
                    }

                    override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                        Log.d("MainActivity", "통신 실패 ${t.message}")
                    }
                })
        }
    }
}