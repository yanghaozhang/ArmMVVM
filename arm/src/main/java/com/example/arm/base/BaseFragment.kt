package com.example.arm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.arm.integration.EventBusManager
import com.example.arm.integration.cache.Cache
import com.example.arm.integration.cache.CacheType
import com.example.arm.integration.lifecycle.FragmentLifecycleable
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.kodein.di.*
import org.kodein.di.android.x.di

/**
 *  author : yanghaozhang
 *  date : 2020/9/21 11:32
 *  description :
 */
abstract class BaseFragment<B : ViewBinding> : Fragment(), DIAware, IFragment<B>, FragmentLifecycleable {
    override val di: DI by di()

    private val mFragmentDelegate: ((Fragment) -> Unit) by instance()

    private val mSubject: BehaviorSubject<FragmentEvent> = BehaviorSubject.create()

    override val mCache: Cache<String, Any> by instance(arg = CacheType.FRAGMENT_CACHE)

    lateinit var binding: B

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentDelegate(this)
        if (useEventBus()) {
            EventBusManager.instance.register(this)
        }
        return initView(inflater, container, savedInstanceState).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mCache.clear()
        if (useEventBus()) {
            EventBusManager.instance.unregister(this)
        }
    }

    override fun provideLifecycleSubject(): Subject<FragmentEvent?>? = mSubject


    override fun useEventBus(): Boolean = true

}