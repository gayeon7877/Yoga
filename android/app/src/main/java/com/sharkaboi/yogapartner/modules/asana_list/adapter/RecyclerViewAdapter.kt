package com.sharkaboi.yogapartner.modules.asana_list.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.sharkaboi.yogapartner.R
import com.sharkaboi.yogapartner.databinding.ItemLayoutBinding
import com.sharkaboi.yogapartner.modules.asana_list.DataItem
import com.sharkaboi.yogapartner.modules.asana_list.FirstFragment


class RecyclerViewAdapter(private val dataList: List<DataItem>, private val fragment: Fragment) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private var clickListener: OnItemClickListener? = null
    interface OnItemClickListner{
        fun onItemClick(view: View, position: Int)
    }
    //리스너 인터페이스 객체를 전달하는 메서드 선언
    fun setOnItemclickListner(onItemClickListner: OnItemClickListner){
        itemClickListner = onItemClickListner
    }

    //전달된 객체를 저장할 변수 정의
    private lateinit var itemClickListner: OnItemClickListner
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
                Log.d("clicked", position.toString())

                val nextFragment = FirstFragment()
                val fragmentManager = fragment.childFragmentManager
                val containerId = fragment.requireView().findViewById<ViewGroup>(R.id.list).id
                fragmentManager.beginTransaction()
                    .add(containerId, nextFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }

    }}
