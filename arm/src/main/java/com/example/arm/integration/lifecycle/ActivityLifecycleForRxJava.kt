package com.example.arm.integration.lifecycle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.trello.rxlifecycle2.android.ActivityEvent
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/16 17:51
 *  description :
 */
class ActivityLifecycleForRxJava(mActivity: AppCompatActivity) : ActivityDelegate(mActivity) {

    private var mLifecycleable: ActivityLifecycleable? = mActivity as? ActivityLifecycleable

    override fun onCreated(owner: LifecycleOwner) {
        Timber.tag("LifecycleForRxJava").d("onCreated()  %s", mActivity?.javaClass?.simpleName)
        mLifecycleable?.provideLifecycleSubject()?.onNext(ActivityEvent.CREATE)
    }

    override fun onStarted(owner: LifecycleOwner) {
        Timber.tag("LifecycleForRxJava").d("onStarted()  %s", mActivity?.javaClass?.simpleName)
        mLifecycleable?.provideLifecycleSubject()?.onNext(ActivityEvent.START)
    }

    override fun onResumed(owner: LifecycleOwner) {
        Timber.tag("LifecycleForRxJava").d("onResumed()  %s", mActivity?.javaClass?.simpleName)
        mLifecycleable?.provideLifecycleSubject()?.onNext(ActivityEvent.RESUME)
    }

    override fun onPaused(owner: LifecycleOwner) {
        Timber.tag("LifecycleForRxJava").d("onPaused()  %s", mActivity?.javaClass?.simpleName)
        mLifecycleable?.provideLifecycleSubject()?.onNext(ActivityEvent.PAUSE)
    }

    override fun onStopped(owner: LifecycleOwner) {
        Timber.tag("LifecycleForRxJava").d("onStopped()  %s", mActivity?.javaClass?.simpleName)
        mLifecycleable?.provideLifecycleSubject()?.onNext(ActivityEvent.STOP)
    }

    override fun onDestroyed(owner: LifecycleOwner) {
        Timber.tag("LifecycleForRxJava").d("onDestroyed()  %s", mActivity?.javaClass?.simpleName)
        mLifecycleable?.provideLifecycleSubject()?.onNext(ActivityEvent.DESTROY)
        mLifecycleable = null
    }
}