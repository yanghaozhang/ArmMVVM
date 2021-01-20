package com.example.armmvvm.ui.viewpager

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arm.base.BaseFragment
import com.example.armmvvm.R
import com.example.armmvvm.ui.scrollview.PreNestedScrollAdapter
import kotlinx.android.synthetic.main.fragment_recycler_view.view.*
import timber.log.Timber
import java.util.*

/**
 *
 * @author 创建人 ：yanghaozhang
 * @version 1.0
 * @package 包名 ：com.example.armmvvm.ui.viewpager
 * @createTime 创建时间 ：2021/1/19
 * @modifyBy 修改人 ：
 * @modifyTime 修改时间 ：
 * @modifyMemo 修改备注：
 */
class SimpleBackgroundFragment(var name: String): BaseFragment() {

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("--::create Fragment ")
        val view = inflater.inflate(R.layout.fragment_recycler_view, container, false)

        val random = Random()
        val r = random.nextInt(256)
        val g = random.nextInt(256)
        val b = random.nextInt(256)
        view.tv_fragment_name.text = name
        view.tv_fragment_name.setBackgroundColor(Color.rgb(r, g, b))

        val list = mutableListOf("ss", "kk", "jj", "ss", "kk", "jj", "ss", "kk", "jj", "ss", "kk", "jj", "ss", "kk", "jj")
        list.addAll(list)
        list.addAll(list)
        list.addAll(list)
        view.recyclerView.layoutManager = LinearLayoutManager(container!!.context)
        val preNestedScrollAdapter = PreNestedScrollAdapter()
        preNestedScrollAdapter.textList = list
        view.recyclerView.adapter = preNestedScrollAdapter
        return view
    }

    override fun initData(savedInstanceState: Bundle?) {
    }
}
