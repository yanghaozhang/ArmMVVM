package com.example.arm.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.arm.integration.cache.Cache
import com.example.arm.util.AppManager

/**
 *  author : yanghaozhang
 *  date : 2020/9/10 11:19
 *  description :
 */
@Deprecated("use android Lifecycle")
class ActivityLifecycle(
    private var mAppManager: AppManager,
    private var mApplication: Application,
    private var mExtrasCache: Cache<String, Any>
) : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        mAppManager.addActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        mAppManager.currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        if (mAppManager.currentActivity == activity) {
            mAppManager.currentActivity = null
        }
    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        mAppManager.removeActivity(activity)
    }


}