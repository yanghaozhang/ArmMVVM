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
        logState("onCreated()")
        mLifecycleable?.provideLifecycleSubject()?.onNext(FragmentEvent.CREATE)
    }

    override fun onStarted(owner: LifecycleOwner) {
        logState("onStarted()")
        mLifecycleable?.provideLifecycleSubject()?.onNext(FragmentEvent.START)
    }

    override fun onResumed(owner: LifecycleOwner) {
        logState("onResumed()")
        mLifecycleable?.provideLifecycleSubject()?.onNext(FragmentEvent.RESUME)
    }

    override fun onPaused(owner: LifecycleOwner) {
        logState("onPaused()")
        mLifecycleable?.provideLifecycleSubject()?.onNext(FragmentEvent.PAUSE)
    }

    override fun onStopped(owner: LifecycleOwner) {
        logState("onStopped()")
        mLifecycleable?.provideLifecycleSubject()?.onNext(FragmentEvent.STOP)
    }

    override fun onDestroyed(owner: LifecycleOwner) {
        logState("onDestroyed()")
        mLifecycleable?.provideLifecycleSubject()?.onNext(FragmentEvent.DESTROY)
        mLifecycleable = null
    }

    private fun logState(state: String) {
//        Timber.tag("Fragment-ForRxJava").d("$state  %s", mFragment?.javaClass?.simpleName)
    }
}
