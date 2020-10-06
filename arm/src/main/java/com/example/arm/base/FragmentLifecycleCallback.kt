package com.example.arm.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import timber.log.Timber

const val TAG =  "FragmentLifecycle"
/**
 *  author : yanghaozhang
 *  date : 2020/10/6 17:02
 *  description :
 */
class FragmentLifecycleCallback: FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        Timber.tag(TAG).d("- onFragmentAttached %s", f.javaClass.simpleName)
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        Timber.tag(TAG).d("- onFragmentCreated %s", f.javaClass.simpleName)
    }

    override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
        Timber.tag(TAG).d("- onFragmentViewCreated %s", f.javaClass.simpleName)
    }

    override fun onFragmentActivityCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        Timber.tag(TAG).d("- onFragmentActivityCreated %s", f.javaClass.simpleName)
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        Timber.tag(TAG).d("- onFragmentStarted %s", f.javaClass.simpleName)
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        Timber.tag(TAG).d("- onFragmentResumed %s", f.javaClass.simpleName)
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        Timber.tag(TAG).d("- onFragmentPaused %s", f.javaClass.simpleName)
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        Timber.tag(TAG).d("- onFragmentStopped %s", f.javaClass.simpleName)
    }

    override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
        Timber.tag(TAG).d("- onFragmentSaveInstanceState %s", f.javaClass.simpleName)
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        Timber.tag(TAG).d("- onFragmentViewDestroyed %s", f.javaClass.simpleName)
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        Timber.tag(TAG).d("- onFragmentDestroyed %s", f.javaClass.simpleName)
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        Timber.tag(TAG).d("- onFragmentDetached %s", f.javaClass.simpleName)
    }
}