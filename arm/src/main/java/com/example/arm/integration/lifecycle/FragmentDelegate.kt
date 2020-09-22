package com.example.arm.integration.lifecycle

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 *  author : yanghaozhang
 *  date : 2020/9/22 16:45
 *  description :
 */
open class FragmentDelegate(protected var mFragment: Fragment?) : LifecycleObserver {

    init {
        if (mFragment is LifecycleOwner) {
            (mFragment as LifecycleOwner).lifecycle.addObserver(this)
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreated(owner: LifecycleOwner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStarted(owner: LifecycleOwner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResumed(owner: LifecycleOwner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPaused(owner: LifecycleOwner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStopped(owner: LifecycleOwner) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        onDestroyed(owner)
        mFragment = null
    }

    open fun onDestroyed(owner: LifecycleOwner) {

    }
}