package com.example.armmvvm

import android.app.Application
import com.example.arm.mvvm.BaseViewModel
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/11 1:20
 *  description :
 */
class MainViewModel(application: Application) : BaseViewModel(application) {

    fun printMainActivity() {
        Timber.tag("MainViewModel").d("")
    }

}