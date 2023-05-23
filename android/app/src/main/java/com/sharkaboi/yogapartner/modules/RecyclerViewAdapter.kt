package com.sharkaboi.yogapartner.modules

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sharkaboi.yogapartner.R

class RecyclerViewAdapter(private val dataList: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        // 아이템 클릭 이벤트 처리
        holder.itemView.setOnClickListener {
         //   onItemClickListener?.onItemClick(item)
        }

        // holder.textView.text = item
    }
    // 아이템 클릭 리스너 변수 선언
    private var onItemClickListener: AdapterView.OnItemClickListener? = null

    // 아이템 클릭 리스너 설정 메서드
    fun setOnItemClickListener(listener: AdapterView.OnItemClickListener) {
        onItemClickListener = listener
    }





    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
    }
}
