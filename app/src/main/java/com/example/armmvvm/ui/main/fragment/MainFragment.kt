package com.example.armmvvm.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.arm.base.BaseFragment
import com.example.arm.ext.observe
import com.example.armmvvm.R
import com.example.armmvvm.ui.main.MainViewModel
import kotlinx.android.synthetic.main.layout_fragment_main.*
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/21 11:29
 *  description :
 */
class MainFragment(val mFragmentName: Any) : BaseFragment() {

    val mMainViewModel: MainViewModel by activityViewModels()

    var mTag = 0

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mRootView = inflater.inflate(R.layout.layout_fragment_main, container, false)
        return mRootView
    }

    override fun initData(savedInstanceState: Bundle?) {
        Timber.tag("MainFragment")
            .d("onViewCreated() called with: view = $view, savedInstanceState = $savedInstanceState   %s ", "$mMainViewModel")

        observe(mMainViewModel.shareLiveData, this::onStateChange)
        tv_main.setText("this is $mFragmentName $mTag")

    }

    private fun onStateChange(msg: String) {
        Timber.tag("MainFragment").d("onStateChange() called with: msg = $msg   ")
        tv_main.setText("this is $mFragmentName  ${++mTag} and from Model is $msg")
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}