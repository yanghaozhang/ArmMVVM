package com.example.arm.util

import com.example.arm.integration.lifecycle.ActivityLifecycleable
import com.example.arm.integration.lifecycle.FragmentLifecycleable
import com.example.arm.integration.lifecycle.Lifecycleable
import com.example.arm.mvvm.IView
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.android.RxLifecycleAndroid

/**
 * ================================================
 * 使用此类操作 RxLifecycle 的特性
 *
 *
 * Created by JessYan on 26/08/2017 17:52
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
class RxLifecycleUtils private constructor() {
    companion object {
        /**
         * 绑定 Activity 的指定生命周期
         *
         * @param view
         * @param event
         * @param <T>
         * @return
        </T> */
        fun <T> bindUntilEvent(
            view: IView,
            event: ActivityEvent
        ): LifecycleTransformer<T> {
            return if (view is ActivityLifecycleable) {
                bindUntilEvent(view as ActivityLifecycleable, event)
            } else {
                throw IllegalArgumentException("view isn't ActivityLifecycleable")
            }
        }

        /**
         * 绑定 Fragment 的指定生命周期
         *
         * @param view
         * @param event
         * @param <T>
         * @return
        </T> */
        fun <T> bindUntilEvent(
            view: IView,
            event: FragmentEvent
        ): LifecycleTransformer<T> {
            return if (view is FragmentLifecycleable) {
                bindUntilEvent(view as FragmentLifecycleable, event)
            } else {
                throw IllegalArgumentException("view isn't FragmentLifecycleable")
            }
        }

        fun <T, R> bindUntilEvent(
            lifecycleable: Lifecycleable<R>,
            event: R
        ): LifecycleTransformer<T> {
            return RxLifecycle.bindUntilEvent(lifecycleable.provideLifecycleSubject()!!, event)
        }

        /**
         * 绑定 Activity/Fragment 的生命周期
         *
         * @param view
         * @param <T>
         * @return
        </T> */
        fun <T> bindToLifecycle(view: IView): LifecycleTransformer<T> {
            return if (view is Lifecycleable<*>) {
                bindToLifecycle(view as Lifecycleable<*>)
            } else {
                throw IllegalArgumentException("view isn't Lifecycleable")
            }
        }

        fun <T> bindToLifecycle(lifecycleable: Lifecycleable<*>): LifecycleTransformer<T> {
            return if (lifecycleable is ActivityLifecycleable) {
                RxLifecycleAndroid.bindActivity(lifecycleable.provideLifecycleSubject()!!)
            } else if (lifecycleable is FragmentLifecycleable) {
                RxLifecycleAndroid.bindFragment(lifecycleable.provideLifecycleSubject()!!)
            } else {
                throw IllegalArgumentException("Lifecycleable not match")
            }
        }
    }

    init {
        throw IllegalStateException("you can't instantiate me!")
    }
}