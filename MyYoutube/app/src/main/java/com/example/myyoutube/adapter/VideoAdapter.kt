package com.example.myyoutube.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myyoutube.R
import com.example.myyoutube.databinding.FragmentPlayerBinding
import com.example.myyoutube.databinding.ItemVideoBinding
import com.example.myyoutube.model.VideoModel

class VideoAdapter(val callback : (String, String) -> Unit) : ListAdapter<VideoModel, VideoAdapter.ViewHolder>(diffUtil) {

//    inner class ViewHolder(private val view : View) : RecyclerView.ViewHolder(view){
    inner class ViewHolder(private val binding : ItemVideoBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : VideoModel){
//            val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
//            val subTitleTextView = view.findViewById<TextView>(R.id.subTitleTextView)
//            val thumbnailImageView = view.findViewById<ImageView>(R.id.thumbnailImageView)
//
//            titleTextView.text = item.title
//            subTitleTextView.text = item.subtitle
//            Glide.with(thumbnailImageView.context)
//                .load(item.thumb)
//                .into(thumbnailImageView)
//
//            view.setOnClickListener {
//                callback(item.sources, item.title)
//            }

            binding.titleTextView.text = item.title
            binding.subTitleTextView.text = item.subtitle
            Glide.with(binding.thumbnailImageView.context)
                .load(item.thumb)
                .into(binding.thumbnailImageView)

            binding.root.setOnClickListener {
                callback(item.sources, item.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false))
        return ViewHolder(ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<VideoModel>(){
            override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}