package com.example.armmvvm.ui.test

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.armmvvm.http.net.CityBean

/**
 *  author : yanghaozhang
 *  date : 2020/9/22 9:32
 *  description :
 */
class CityAdapter : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    var mCityList = mutableListOf<CityBean>()

    var mOnClickListener: ((View, CityBean) -> Unit)? = null

    override fun getItemCount(): Int {
        return mCityList.size
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        (holder.itemView as TextView).setText(mCityList[position].city_name)
        holder.itemView.setOnClickListener {
            mOnClickListener?.invoke(holder.itemView, mCityList[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val textView = TextView(parent.context)
        textView.setPadding(0, 10, 0, 10)
        return CityViewHolder(textView)
    }

    class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}


