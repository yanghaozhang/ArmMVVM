package com.example.armmvvm.ui.livedata.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.arm.base.BaseFragment
import com.example.armmvvm.R
import com.example.armmvvm.ui.livedata.LiveDataViewModel
import kotlinx.android.synthetic.main.layout_fragment_live_data.*
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/21 11:29
 *  description :
 */
class UnPeekFragment(val mFragmentName: Any) : BaseFragment() {

    val liveDataViewModel: LiveDataViewModel by activityViewModels()

    var mTag = 0

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mRootView = inflater.inflate(R.layout.layout_fragment_live_data, container, false)
        return mRootView
    }

    override fun initData(savedInstanceState: Bundle?) {
        Timber.tag("MainFragment")
                .d("onViewCreated() called with: view = $view, savedInstanceState = $savedInstanceState   %s ", "$liveDataViewModel")

        liveDataViewModel.unPeekLiveData.observe(this, this::onStateChange)
        tv_main.setText("this is $mFragmentName \n Number is $mTag")

    }

    private fun onStateChange(msg: String) {
        Timber.tag("MainFragment").d("onStateChange() called with: msg = $msg   ")
        tv_main.setText("this is $mFragmentName \n Increase Number is ${++mTag} and Model is $msg")
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}