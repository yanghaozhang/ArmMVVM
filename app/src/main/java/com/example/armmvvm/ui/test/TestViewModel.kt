package com.example.armmvvm.ui.test

import com.example.arm.mvvm.BaseViewModel
import com.example.arm.util.AppManager
import org.kodein.di.instance
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/18 11:04
 *  description :
 */
class TestViewModel(val model: TestModel) : BaseViewModel() {

    val mAppManager: AppManager by instance {
        // 调用时执行
        Timber.tag("TestViewModel").d("null() called   %s ", "instance AppManager")
    }

    fun printMainActivity() {
        Timber.tag("TestViewModel").d("$mAppManager")
        model.logInstance()
    }

}