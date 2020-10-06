package com.example.arm.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.arm.ext.AppDIAware
import com.example.arm.util.AppManager
import org.kodein.di.instance
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/10 11:19
 *  description :
 *  ActivityLifecycleCallbacks 监听所有Activity
 *  而LifecycleObserver只能管理订阅了生命周期的Activity,比如BaseActivity,而第三方的无法监听
 *  用哪一个需要根据需求
 */
class ActivityLifecycleCallback(private val mFragmentLifecycleCallbacks: MutableList<FragmentManager.FragmentLifecycleCallbacks>) :
    Application.ActivityLifecycleCallbacks, AppDIAware {

    val mAppManager: AppManager by instance()

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        Timber.tag("ActivityLifecycle").d("onActivityCreated() called with: activity = $activity, p1 = $p1 ")
        mAppManager.addActivity(activity)
        if (activity is FragmentActivity) {
            for (fragmentLifecycleCallback in mFragmentLifecycleCallbacks) {
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallback, true)
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.tag("ActivityLifecycle").d("onActivityStarted() called with: activity = $activity   ")
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.tag("ActivityLifecycle").d("onActivityResumed() called with: activity = $activity  ")
        mAppManager.currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        Timber.tag("ActivityLifecycle").d("onActivityPaused() called with: activity = $activity   ")
        if (mAppManager.currentActivity === activity) {
            mAppManager.currentActivity = null
        }
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.tag("ActivityLifecycle").d("onActivityStopped() called with: activity = $activity ")
    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
        Timber.tag("ActivityLifecycle").d("onActivitySaveInstanceState() called with: activity = $activity, p1 = $p1   ")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.tag("ActivityLifecycle").d("onActivityDestroyed() called with: activity = $activity   ")
        mAppManager.removeActivity(activity)
    }
}