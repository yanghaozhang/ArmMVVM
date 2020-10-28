package com.example.arm.base

object Platform {
    val DEPENDENCY_AUTO_LAYOUT = findClassByClassName("com.zhy.autolayout.AutoLayoutInfo")

    val DEPENDENCY_SUPPORT_DESIGN =findClassByClassName("com.google.android.material.snackbar.Snackbar")

    val DEPENDENCY_GLIDE = findClassByClassName("com.bumptech.glide.Glide")

    val DEPENDENCY_ANDROID_EVENT_BUS = findClassByClassName("org.simple.eventbus.EventBus")

    val DEPENDENCY_EVENT_BUS = findClassByClassName("org.greenrobot.eventbus.EventBus")

    private fun findClassByClassName(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }
}