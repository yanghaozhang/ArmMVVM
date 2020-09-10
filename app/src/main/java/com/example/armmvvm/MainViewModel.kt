package com.example.armmvvm

import androidx.lifecycle.ViewModel
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/11 1:20
 *  description :
 */
class MainViewModel(private val activity: MainActivity) : ViewModel() {

    fun printMainActivity(){
        Timber.tag("MainViewModel").d("printMainActivity() called   %s ", activity.javaClass.simpleName)
    }


}