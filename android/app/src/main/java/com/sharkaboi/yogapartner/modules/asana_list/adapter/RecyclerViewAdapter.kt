package com.sharkaboi.yogapartner.modules.asana_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sharkaboi.yogapartner.R
import com.sharkaboi.yogapartner.databinding.ItemLayoutBinding
import com.sharkaboi.yogapartner.modules.asana_list.DataItem

class RecyclerViewAdapter(private val dataList: List<DataItem>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataItem) {
            binding.textTitle.text = item.title
            binding.imageview.setImageResource(item.imageResId)
        }

    }
}
