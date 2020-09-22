package com.example.armmvvm.ui.test

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.armmvvm.http.net.ProvinceBean

/**
 *  author : yanghaozhang
 *  date : 2020/9/22 9:32
 *  description :
 */
class ProvinceAdapter : RecyclerView.Adapter<ProvinceAdapter.ProvinceViewHolder>() {

    var mProvinceList = mutableListOf<ProvinceBean>()

    var mOnClickListener: ((View, ProvinceBean) -> Unit)? = null

    override fun getItemCount(): Int {
        return mProvinceList.size
    }

    override fun onBindViewHolder(holder: ProvinceViewHolder, position: Int) {
        (holder.itemView as TextView).setText(mProvinceList[position].province)
        holder.itemView.setOnClickListener {
            mOnClickListener?.invoke(holder.itemView, mProvinceList[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProvinceViewHolder {
        val textView = TextView(parent.context)
        textView.setPadding(0, 10, 0, 10)
        return ProvinceViewHolder(textView)
    }

    class ProvinceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}


