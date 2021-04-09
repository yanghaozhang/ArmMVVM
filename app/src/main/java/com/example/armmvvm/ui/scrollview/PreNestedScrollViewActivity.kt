package com.example.armmvvm.ui.scrollview

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arm.base.BaseActivity
import com.example.armmvvm.databinding.ActivityPreNestedScrollViewBinding

/**
 * @author 创建人 ：yanghaozhang
 * @version 1.0
 * @package 包名 ：com.example.armmvvm.ui
 * @createTime 创建时间 ：2021/1/13
 * @modifyBy 修改人 ：
 * @modifyTime 修改时间 ：
 * @modifyMemo 修改备注：
 */
class PreNestedScrollViewActivity : BaseActivity<ActivityPreNestedScrollViewBinding>() {
    override fun initView(savedInstanceState: Bundle?) = ActivityPreNestedScrollViewBinding.inflate(layoutInflater)

    override fun initData(savedInstanceState: Bundle?) {
        val list = mutableListOf("ss","kk","jj","ss","kk","jj","ss","kk","jj","ss","kk","jj","ss","kk","jj")
        list.addAll(list)
        list.addAll(list)
        list.addAll(list)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val preNestedScrollAdapter = PreNestedScrollAdapter()
        preNestedScrollAdapter.textList = list
        binding.recyclerView.adapter = preNestedScrollAdapter
    }

}