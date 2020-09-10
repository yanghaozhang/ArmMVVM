package com.example.armmvvm.base

import com.example.arm.http.ErrorListener

/**
 *  author : yanghaozhang
 *  date : 2020/9/11 2:02
 *  description :
 */
class MyErrorListener:ErrorListener {
    override fun handleResponseError(t: Throwable?) {
    }

    override fun handleNormalError(t: Throwable?) {
    }
}