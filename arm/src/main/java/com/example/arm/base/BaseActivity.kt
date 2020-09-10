package com.example.arm.base

import android.app.Activity
import android.os.Bundle
import android.view.InflateException
import androidx.appcompat.app.AppCompatActivity
import org.kodein.di.DIAware
import org.kodein.di.instance
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/10 13:48
 *  description :
 */
abstract class BaseActivity : AppCompatActivity(), IActivity, DIAware {
    private val mActivityDelegate: ((Activity) -> Unit)? by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag("BaseActivity").d("onCreate() called with: savedInstanceState = $savedInstanceState   %s ", "before")
        super.onCreate(savedInstanceState)
        Timber.tag("BaseActivity").d("onCreate() called with: savedInstanceState = $savedInstanceState   %s ", "after")

        mActivityDelegate?.invoke(this)
        try {
            val layoutResID = initView(savedInstanceState)
            //如果initView返回0,框架则不会调用setContentView(),当然也不会 Bind ButterKnife
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

    override fun useEventBus(): Boolean = true

    override fun useFragment(): Boolean = true
}