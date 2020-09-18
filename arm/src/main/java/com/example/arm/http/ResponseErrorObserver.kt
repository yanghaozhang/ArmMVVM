package com.example.arm.http

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

abstract class ResponseErrorObserver<T>(var mErrorListener: ErrorListener?) : Observer<T> {
    override fun onSubscribe(d: Disposable) {}
    override fun onError(e: Throwable) {
        mErrorListener?.handleResponseError(e)

    }

    override fun onComplete() {}
}