package com.example.bithumb_open_api_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bithumb_open_api_fragment.databinding.HistoryListBinding
import java.text.SimpleDateFormat
import java.util.*

class DetailRecyclerAdapter(
    private val fragment: DetailFragment,
    private val name: String,
    private val closingPrice: List<String>,
    private val timestamp: List<Long>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class MyViewHolder(val binding : HistoryListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  = MyViewHolder(HistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as DetailRecyclerAdapter.MyViewHolder).binding

        with(binding){
            textTimestamp.text = timestampToDate(timestamp[position])
            textClosing.text = closingPrice[position] + " 원"
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, holder.layoutPosition)
            val mainActivity = fragment.activity as MainActivity
            mainActivity.replaceFragment("History_detail", name, timestamp[position])
        }
    }
    override fun getItemCount() = timestamp.size

    interface OnItemClickListener{
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

    private fun timestampToDate(timestamp: Long) = SimpleDateFormat("yyyy-MM-dd, hh:mm:ss", Locale.KOREA).format(timestamp)
}