/*
 * Copyright 2018 JessYan
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
package com.example.arm.base

/**
 * ================================================
 * Created by JessYan on 2018/7/27 15:32
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
object Platform {
    @JvmStatic
    val DEPENDENCY_AUTO_LAYOUT: Boolean

    @JvmStatic
    val DEPENDENCY_SUPPORT_DESIGN: Boolean

    @JvmStatic
    val DEPENDENCY_GLIDE: Boolean

    @JvmStatic
    val DEPENDENCY_ANDROID_EVENT_BUS: Boolean

    @JvmStatic
    val DEPENDENCY_EVENT_BUS: Boolean

    private fun findClassByClassName(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    init {
        DEPENDENCY_AUTO_LAYOUT = findClassByClassName("com.zhy.autolayout.AutoLayoutInfo")
        DEPENDENCY_SUPPORT_DESIGN =
            findClassByClassName("com.google.android.material.snackbar.Snackbar")
        DEPENDENCY_GLIDE = findClassByClassName("com.bumptech.glide.Glide")
        DEPENDENCY_ANDROID_EVENT_BUS = findClassByClassName("org.simple.eventbus.EventBus")
        DEPENDENCY_EVENT_BUS = findClassByClassName("org.greenrobot.eventbus.EventBus")
    }
}