package com.example.arm.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

@Deprecated("use LiveDataKt liveData.observe(owner,(T)->Unit)",
    ReplaceWith("liveData.observe(this, { observer(it) })")
)
fun <T> LifecycleOwner.observe(liveData: LiveData<T>, observer: (t: T) -> Unit) {
    liveData.observe(this, {
        observer(it)
    })
}
