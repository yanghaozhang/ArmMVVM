package com.example.armmvvm.ui.main

import com.example.arm.mvvm.BaseModel
import okhttp3.HttpUrl
import org.kodein.di.instance
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/17 15:51
 *  description :
 */
class MainModel : BaseModel() {

    val mHttpUrl:HttpUrl by instance()

    fun register() {
        Timber.tag("MainModel").d("register() called   %s ", "$mHttpUrl")
    }
}