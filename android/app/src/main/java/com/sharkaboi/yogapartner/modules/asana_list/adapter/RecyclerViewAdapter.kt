package com.sharkaboi.yogapartner.modules.asana_list.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.sharkaboi.yogapartner.R
import com.sharkaboi.yogapartner.databinding.ItemLayoutBinding
import com.sharkaboi.yogapartner.modules.asana_list.DataItem
import com.sharkaboi.yogapartner.modules.asana_list.FirstFragment


class RecyclerViewAdapter(private val dataList: List<DataItem>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private var clickListener: OnItemClickListener? = null

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
    fun getItem(position: Int): DataItem {
        return dataList[position]
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    inner class ViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(item: DataItem) {
            binding.textTitle.text = item.title
            binding.imageview.setImageResource(item.imageResId)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                clickListener?.onItemClick(position)
                Log.d("clicked","clicked")

            }
        }
    }
}
