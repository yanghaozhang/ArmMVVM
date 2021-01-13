package com.example.armmvvm.ui.scrollview

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.arm.ext.dpInt

/**
 *
 * @author 创建人 ：yanghaozhang
 * @version 1.0
 * @package 包名 ：com.example.armmvvm.ui.scrollview
 * @createTime 创建时间 ：2021/1/13
 * @modifyBy 修改人 ：
 * @modifyTime 修改时间 ：
 * @modifyMemo 修改备注：
 */
class PreNestedScrollAdapter : RecyclerView.Adapter<PreNestedScrollAdapter.ViewHolder>() {

    var textList: List<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = TextView(parent.context)
        textView.setPadding(0, 30, 0, 30)
        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50f.dpInt)
        textView.gravity = Gravity.CENTER
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemView is TextView) {
            holder.itemView.text = textList?.get(position) ?: "kkk"
        }
    }

    override fun getItemCount(): Int {
        return textList?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}