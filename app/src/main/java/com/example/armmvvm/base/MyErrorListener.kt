package com.example.armmvvm.base

import com.example.arm.http.ErrorListener
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/11 2:02
 *  description :
 */
class MyErrorListener:ErrorListener {
    override fun handleResponseError(t: Throwable?) {
        Timber.tag("MyErrorListener").d("handleResponseError() called with: t = $t   %s ", "----")
    }

    override fun handleNormalError(t: Throwable?) {
        Timber.tag("MyErrorListener").d("handleNormalError() called with: t = $t   %s ", "----")
    }
}