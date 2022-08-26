package com.example.myyoutube

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myyoutube.adapter.VideoAdapter
import com.example.myyoutube.databinding.FragmentPlayerBinding
import com.example.myyoutube.dto.VideoDto
import com.example.myyoutube.service.VideoService
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

class PlayerFragment : Fragment(R.layout.fragment_player){

    var binding : FragmentPlayerBinding? = null
    private lateinit var videoAdapter : VideoAdapter
    var player : ExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        initMotionLayoutEvent(fragmentPlayerBinding)
        initRecyclerView(fragmentPlayerBinding)
        initPlayer(fragmentPlayerBinding)
        initControlButton(fragmentPlayerBinding)
        getVideoList()
    }

    //RecyclerView 세팅
    private fun initRecyclerView(fragmentPlayerBinding: FragmentPlayerBinding) {
        videoAdapter = VideoAdapter(callback = {url, title ->
            play(url, title)
        })

        fragmentPlayerBinding.fragmentRecyclerView.apply{
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    //MotionLayoutEvent 세팅
    private fun initMotionLayoutEvent(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playerMotionLayout?.let {
            it.setTransitionListener(object : MotionLayout.TransitionListener{
                override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) { }

                override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                    binding?.let {
                        (activity as MainActivity).also { mainActivity ->
                            mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout).progress = abs(progress)
                        }
                    }
                }

                override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) { }

                override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) { }
            })
        }
    }

    //ExoPlayer 세팅
    private fun initPlayer(fragmentPlayerBinding: FragmentPlayerBinding) {
        //context null 체크하여 ExoPlayer 객체 만들기
        context?.let{
            player = ExoPlayer.Builder(it).build()
        }
        //생성한 객체를 playerView 에 할당하기
        fragmentPlayerBinding.playerView.player = player
        //playerView 에 Listener 생성 -> 여러가지 함수 override 가능
        binding?.let{
            player?.addListener(object : Player.Listener{
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying){
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_pause_24)
                    }
                    else{
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }
            })
        }
    }

    //하단 재생/일시중지 controlButton 제어
    private fun initControlButton(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.bottomPlayerControlButton.setOnClickListener {
            //player null 체크
            val player = player ?: return@setOnClickListener
            if (player.isPlaying){ player.pause() }
            else { player.play() }
        }
    }

    //영상재생 함수(url -> dataSource -> mediaSource 로 변환해야 사용 가능)
    fun play(url : String, title : String){
        context?.let {
            val dataSourceFactory = DefaultDataSource.Factory(it)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
            player?.apply {
                setMediaSource(mediaSource)
                prepare()
                play()
            }
        }

        //영상이 시작되면 playerMotionLayout 의 transition 을 강제로 End 로 만듦(펼쳐진 상태) + 하단에 제목 부여
        binding?.let{
            it.playerMotionLayout.transitionToEnd()
            it.bottomTitleTextView.text = title
        }
    }

    private fun getVideoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also {
            it.listVideos()
                .enqueue(object : Callback<VideoDto> {
                    override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                        if (response.isSuccessful.not()) {
                            Log.d("MainActivity", "response not successful")
                        }

                        response.body()?.let { videoDto ->
                            videoAdapter.submitList(videoDto.videos)
                        }
                    }

                    override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                        Log.d("MainActivity", "통신 실패 ${t.message}")
                    }
                })
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        player?.release()
    }
}