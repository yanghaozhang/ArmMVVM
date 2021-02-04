package com.example.armmvvm.ui.livedata.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.example.arm.base.BaseFragment
import com.example.armmvvm.R
import com.example.armmvvm.ui.livedata.LiveDataViewModel
import kotlinx.android.synthetic.main.layout_fragment_live_data.*
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/21 14:01
 *  description :
 */
class MutableFragment(val mFragmentName: Any) : BaseFragment() {

    val liveDataViewModel: LiveDataViewModel by activityViewModels()

    var mTag = 0

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mRootView = inflater.inflate(R.layout.layout_fragment_live_data, container, false)
        return mRootView
    }

    override fun initData(savedInstanceState: Bundle?) {
        liveDataViewModel.mutableLiveData.observe(this, this::onStateChange)
        tv_main.text = "this is $mFragmentName \n Number is $mTag"
    }

    private fun onStateChange(msg: String) {
        Timber.tag("MutableFragment").d("onStateChange() called with: msg = $msg")
        tv_main.text = "this is $mFragmentName \n Increase Number is ${++mTag} and Model is $msg"
    }

}