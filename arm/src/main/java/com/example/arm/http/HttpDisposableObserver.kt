package com.example.arm.http

import com.example.arm.di.GlobalConfigModule
import com.example.arm.ext.AppDIAware
import io.reactivex.observers.DisposableObserver
import org.kodein.di.instance
import org.kodein.di.newInstance

/**
 *  author : yanghaozhang
 *  date : 2020/9/22 0:14
 *  description :
 */
abstract class HttpDisposableObserver<T>: DisposableObserver<T>() ,AppDIAware{

    val mErrorListener: ErrorListener? by newInstance { instance<GlobalConfigModule>().mErrorListener }

    override fun onNext(t: T) {

    }

    override fun onError(e: Throwable) {
        mErrorListener?.handleResponseError(e)
    }

    override fun onComplete() {

    }
}