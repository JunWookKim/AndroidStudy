package com.example.bithumb_open_api_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bithumb_open_api_fragment.databinding.SimpleListBinding

class RecyclerAdapter(private val fragment: MainFragment, private val keyList : MutableList<String>, private val infoList : MutableList<Map<String, String>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    inner class MyViewHolder(val binding : SimpleListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = MyViewHolder(SimpleListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding =(holder as RecyclerAdapter.MyViewHolder).binding

        with(binding){
            textName.text = keyList[position]
            textOpening.text = infoList[position]["opening_price"]
            textClosing.text = infoList[position]["closing_price"]
            textTradeValue24H.text = infoList[position]["acc_trade_value_24H"]
        }
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, holder.layoutPosition)
            val mainActivity = this.fragment.activity as MainActivity
            mainActivity.replaceFragment("Detail", keyList[position], null)
        }
    }

    interface OnItemClickListener{
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

    override fun getItemCount(): Int {
        return infoList.size
    }

}