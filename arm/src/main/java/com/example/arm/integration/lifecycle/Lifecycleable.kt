package com.example.arm.integration.lifecycle

import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.subjects.Subject

interface Lifecycleable<T> {
    fun provideLifecycleSubject(): Subject<T>?
}

interface FragmentLifecycleable : Lifecycleable<FragmentEvent?>

interface ActivityLifecycleable : Lifecycleable<ActivityEvent?>