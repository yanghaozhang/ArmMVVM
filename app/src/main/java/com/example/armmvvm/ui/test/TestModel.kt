package com.example.armmvvm.ui.test

import com.example.arm.mvvm.BaseModel
import okhttp3.HttpUrl
import org.kodein.di.instance
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/18 11:04
 *  description :
 */
class TestModel :BaseModel() {

    val mHttpUrl: HttpUrl by instance()

    fun logInstance() {
        Timber.tag("TestModel").d("register() called   %s ", "$mHttpUrl")
    }
}