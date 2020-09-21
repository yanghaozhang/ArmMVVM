package com.example.arm.integration.lifecycle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.example.arm.ext.AppDIAware
import com.example.arm.util.AppManager
import org.kodein.di.instance
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/10 11:29
 *  description :
 */
class ActivityDelegateImp(mActivity: AppCompatActivity) : ActivityDelegate(mActivity),AppDIAware {

    val mAppManager: AppManager by instance()

    override fun onCreated(owner: LifecycleOwner) {
        Timber.tag("ActivityDelegateImp").d("onCreated()  %s", mActivity?.javaClass?.simpleName)
        mActivity?.run {
            mAppManager.addActivity(this)
        }
    }

    override fun onStarted(owner: LifecycleOwner) {
        Timber.tag("ActivityDelegateImp").d("onStarted()  %s", mActivity?.javaClass?.simpleName)
    }

    override fun onResumed(owner: LifecycleOwner) {
        Timber.tag("ActivityDelegateImp").d("onResumed()  %s", mActivity?.javaClass?.simpleName)
        mActivity?.run {
            mAppManager.currentActivity = this
        }
    }

    override fun onPaused(owner: LifecycleOwner) {
        Timber.tag("ActivityDelegateImp").d("onPaused()  %s", mActivity?.javaClass?.simpleName)
        mActivity?.run {
            if (this == mAppManager.currentActivity) {
                mAppManager.currentActivity = null
            }
        }
    }

    override fun onStopped(owner: LifecycleOwner) {
        Timber.tag("ActivityDelegateImp").d("onStopped()  %s", mActivity?.javaClass?.simpleName)
    }

    override fun onDestroyed(owner: LifecycleOwner) {
        mActivity?.run {
            mAppManager.removeActivity(this)
        }
        super.onDestroyed(owner)
        Timber.tag("ActivityDelegateImp").d("onDestroyed()  %s", mActivity?.javaClass?.simpleName)
    }
}