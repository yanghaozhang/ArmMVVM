package com.example.armmvvm.ui.livedata.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.arm.base.BaseFragment
import com.example.armmvvm.databinding.FragmentLiveDataBinding
import com.example.armmvvm.ui.livedata.LiveDataViewModel
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/21 11:29
 *  description :
 */
class UnPeekFragment(private val mFragmentName: Any) : BaseFragment<FragmentLiveDataBinding>() {

    val liveDataViewModel: LiveDataViewModel by activityViewModels()

    var mTag = 0

    override fun initView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentLiveDataBinding {
        return FragmentLiveDataBinding.inflate(inflater, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        liveDataViewModel.unPeekLiveData.observe(this, this::onStateChange)
        binding.tvMain.text = "this is $mFragmentName \n Number is $mTag"
    }

    private fun onStateChange(msg: String) {
        Timber.tag("UnPeekFragment").d("onStateChange() called with: msg = $msg   ")
        binding.tvMain.text = "this is $mFragmentName \n Increase Number is ${++mTag} and Model is $msg"
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}