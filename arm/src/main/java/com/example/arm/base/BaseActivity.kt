package com.example.arm.base

import android.app.Activity
import android.os.Bundle
import android.view.InflateException
import androidx.appcompat.app.AppCompatActivity
import com.example.arm.ext.toast
import com.example.arm.integration.cache.Cache
import com.example.arm.integration.cache.CacheType
import com.example.arm.integration.lifecycle.ActivityLifecycleable
import com.example.arm.mvvm.IView
import com.trello.rxlifecycle2.android.ActivityEvent
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.kodein.di.*
import org.kodein.di.android.di
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/10 13:48
 *  description :
 */
abstract class BaseActivity : AppCompatActivity(), IActivity, DIAware, ActivityLifecycleable, IView {

    override val di: DI by di()

    override val diContext: DIContext<*>
        get() = diContext(this)

    private val mActivityDelegate: ((Activity) -> Unit) by instance()

    private val mSubject: BehaviorSubject<ActivityEvent> = BehaviorSubject.create()

    override val mCache: Cache<String, Any> by instance(arg = CacheType.ACTIVITY_CACHE)

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag("BaseActivity").d("onCreate() called with: savedInstanceState = $savedInstanceState   %s ", "before")
        super.onCreate(savedInstanceState)
        Timber.tag("BaseActivity").d("onCreate() called with: savedInstanceState = $savedInstanceState   %s ", "after")
        // 添加AppCompatActivity生命周期监听
        mActivityDelegate.invoke(this)
        try {
            val layoutResID = initView(savedInstanceState)
            if (layoutResID != 0) {
                setContentView(layoutResID)
            }
        } catch (e: Exception) {
            if (e is InflateException) {
                throw e
            }
            e.printStackTrace()
        }
        initData(savedInstanceState)
    }

    override fun provideLifecycleSubject(): Subject<ActivityEvent?>? {
        return mSubject
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        toast(message)
    }

    override fun killMyself() {
        finish()
    }

    override fun useEventBus(): Boolean = true

    override fun useFragment(): Boolean = true
}