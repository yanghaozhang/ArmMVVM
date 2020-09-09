package com.example.armmvvm.base

import android.util.Log
import com.example.armmvvm.BuildConfig
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:55
 *  description :
 */
class MyTimberTree : Timber.DebugTree() {

    val TAG = "Arm::"

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return BuildConfig.DEBUG || priority >= Log.WARN
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, TAG + tag, message, t)
    }
}