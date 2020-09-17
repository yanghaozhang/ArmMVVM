/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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