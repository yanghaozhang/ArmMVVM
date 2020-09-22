package com.example.arm.integration.lifecycle

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.arm.ext.AppDIAware
import com.trello.rxlifecycle2.android.FragmentEvent
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/10 11:29
 *  description :
 */
class FragmentDelegateForRxJava(mFragment: Fragment?) : FragmentDelegate(mFragment), AppDIAware {

    private var mLifecycleable: FragmentLifecycleable? = mFragment as? FragmentLifecycleable

    override fun onCreated(owner: LifecycleOwner) {
        Timber.tag("Fragment-ForRxJava").d("onCreated()  %s", mFragment?.javaClass?.simpleName)
        mLifecycleable?.provideLifecycleSubject()?.onNext(FragmentEvent.CREATE)
    }

    override fun onStarted(owner: LifecycleOwner) {
        Timber.tag("Fragment-ForRxJava").d("onStarted()  %s", mFragment?.javaClass?.simpleName)
        mLifecycleable?.provideLifecycleSubject()?.onNext(FragmentEvent.START)
    }

    override fun onResumed(owner: LifecycleOwner) {
        Timber.tag("Fragment-ForRxJava").d("onResumed()  %s", mFragment?.javaClass?.simpleName)
        mLifecycleable?.provideLifecycleSubject()?.onNext(FragmentEvent.RESUME)
    }

    override fun onPaused(owner: LifecycleOwner) {
        Timber.tag("Fragment-ForRxJava").d("onPaused()  %s", mFragment?.javaClass?.simpleName)
        mLifecycleable?.provideLifecycleSubject()?.onNext(FragmentEvent.PAUSE)
    }

    override fun onStopped(owner: LifecycleOwner) {
        Timber.tag("Fragment-ForRxJava").d("onStopped()  %s", mFragment?.javaClass?.simpleName)
        mLifecycleable?.provideLifecycleSubject()?.onNext(FragmentEvent.STOP)
    }

    override fun onDestroyed(owner: LifecycleOwner) {
        Timber.tag("Fragment-ForRxJava").d("onDestroyed()  %s", mFragment?.javaClass?.simpleName)
        mLifecycleable?.provideLifecycleSubject()?.onNext(FragmentEvent.DESTROY)
        mLifecycleable = null
    }
}