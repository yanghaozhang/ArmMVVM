package com.example.armmvvm.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.example.arm.integration.lifecycle.ActivityDelegate
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/10 11:29
 *  description :
 */
class MyActivityDelegateImp(mActivity: AppCompatActivity) : ActivityDelegate(mActivity) {

    override fun onCreated(owner: LifecycleOwner) {
        Timber.tag("MY_ActivityDelegateImp").d("onCreated()  %s", mActivity?.javaClass?.simpleName)
    }

    override fun onStarted(owner: LifecycleOwner) {
        Timber.tag("MY_ActivityDelegateImp").d("onStarted()  %s", mActivity?.javaClass?.simpleName)
    }

    override fun onResumed(owner: LifecycleOwner) {
        Timber.tag("MY_ActivityDelegateImp").d("onResumed()  %s", mActivity?.javaClass?.simpleName)
    }

    override fun onPaused(owner: LifecycleOwner) {
        Timber.tag("MY_ActivityDelegateImp").d("onPaused()  %s", mActivity?.javaClass?.simpleName)
    }

    override fun onStopped(owner: LifecycleOwner) {
        Timber.tag("MY_ActivityDelegateImp").d("onStopped()  %s", mActivity?.javaClass?.simpleName)
    }

    override fun onDestroyed(owner: LifecycleOwner) {
        Timber.tag("MY_ActivityDelegateImp").d("onDestroyed()  %s", mActivity?.javaClass?.simpleName)
    }
}