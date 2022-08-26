package com.example.flowpractice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flowpractice.databinding.SimpleListBinding

class RecyclerAdapter(val keyList : MutableList<String>) : ListAdapter<InfoModel, RecyclerAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding : SimpleListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(infoModel: InfoModel){
            with(binding){
                textOpening.text = infoModel.opening
                textClosing.text = infoModel.closing
                textTradeValue24H.text = infoModel.tradeValue24H
            }
        }

        fun setKeyList(position: Int){
            with(binding){
                textName.text = keyList[position]
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SimpleListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setKeyList(position)
        return holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<InfoModel>(){
            override fun areItemsTheSame(oldItem: InfoModel, newItem: InfoModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: InfoModel, newItem: InfoModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}