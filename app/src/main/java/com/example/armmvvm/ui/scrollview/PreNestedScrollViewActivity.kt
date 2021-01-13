package com.example.armmvvm.ui.scrollview

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arm.base.BaseActivity
import com.example.armmvvm.R
import kotlinx.android.synthetic.main.activity_pre_nested_scroll_view.*

/**
 * @author 创建人 ：yanghaozhang
 * @version 1.0
 * @package 包名 ：com.example.armmvvm.ui
 * @createTime 创建时间 ：2021/1/13
 * @modifyBy 修改人 ：
 * @modifyTime 修改时间 ：
 * @modifyMemo 修改备注：
 */
class PreNestedScrollViewActivity : BaseActivity() {
    override fun initView(savedInstanceState: Bundle?): Int =
        R.layout.activity_pre_nested_scroll_view

    override fun initData(savedInstanceState: Bundle?) {
        val list = mutableListOf("ss","kk","jj","ss","kk","jj","ss","kk","jj","ss","kk","jj","ss","kk","jj")
        list.addAll(list)
        list.addAll(list)
        list.addAll(list)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val preNestedScrollAdapter = PreNestedScrollAdapter()
        preNestedScrollAdapter.textList = list
        recyclerView.adapter = preNestedScrollAdapter
    }

}