package com.example.arm.ext

import android.content.Context
import android.widget.Toast

private var toast: Toast? = null

fun Context.toast(value: String) = toast(value, Toast.LENGTH_SHORT)

fun Context.toast(value: String, duration: Int) {
    toast?.cancel()
    toast = Toast.makeText(this, value, duration).apply { show() }
}
