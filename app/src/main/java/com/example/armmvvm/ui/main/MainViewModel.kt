package com.example.armmvvm.ui.main

import com.example.arm.mvvm.BaseViewModel
import com.example.arm.util.AppManager
import org.kodein.di.instance
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/11 1:20
 *  description : ViewModel 所需的Model通过构造提供
 */
class MainViewModel(val mainModel: MainModel) : BaseViewModel() {

    val mAppManager: AppManager by instance {
        // 调用时执行
        Timber.tag("MainViewModel").d("null() called   %s ", "instance AppManager")
    }

    fun printMainActivity() {
        Timber.tag("MainViewModel").d("$mAppManager")
        mainModel.register()
    }

    override fun onCleared() {
        super.onCleared()
        mainModel.onDestroy()
    }
}