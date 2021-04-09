package com.example.armmvvm.ui.viewpager

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arm.base.BaseFragment
import com.example.armmvvm.databinding.FragmentRecyclerViewBinding
import com.example.armmvvm.ui.scrollview.PreNestedScrollAdapter
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
class SimpleBackgroundFragment(var name: String) : BaseFragment<FragmentRecyclerViewBinding>() {

    override fun initView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentRecyclerViewBinding {
        Timber.d("--::create Fragment ")
        return FragmentRecyclerViewBinding.inflate(inflater, container, false).apply {
            val random = Random()
            val r = random.nextInt(256)
            val g = random.nextInt(256)
            val b = random.nextInt(256)
            tvFragmentName.text = name
            tvFragmentName.setBackgroundColor(Color.rgb(r, g, b))

            val list =
                mutableListOf("ss", "kk", "jj", "ss", "kk", "jj", "ss", "kk", "jj", "ss", "kk", "jj", "ss", "kk", "jj")
            list.addAll(list)
            list.addAll(list)
            list.addAll(list)
            recyclerView.layoutManager = LinearLayoutManager(container!!.context)
            val preNestedScrollAdapter = PreNestedScrollAdapter()
            preNestedScrollAdapter.textList = list
            recyclerView.adapter = preNestedScrollAdapter
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }
}
