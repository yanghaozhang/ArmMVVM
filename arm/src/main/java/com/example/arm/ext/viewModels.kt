package com.example.arm.ext

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.viewModelKt(
    noinline factoryProducer: (() -> VM)
): Lazy<VM> {
    return ViewModelLazy(VM::class, { viewModelStore }, factoryProducer)
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.viewModelKt(
    noinline factoryProducer: (() -> VM)
): Lazy<VM> {
    return ViewModelLazy(VM::class, { viewModelStore }, factoryProducer)
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.activityViewModelKt(
    noinline factoryProducer: (() -> VM)
): Lazy<VM> {
    return ViewModelLazy(VM::class, { requireActivity().viewModelStore }, factoryProducer)
}
