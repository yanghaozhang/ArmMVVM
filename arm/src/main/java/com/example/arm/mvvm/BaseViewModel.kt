package com.example.arm.mvvm

import androidx.lifecycle.AndroidViewModel
import com.example.arm.base.BaseApplication
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.DIContext
import org.kodein.di.android.x.di
import org.kodein.di.diContext
import org.simple.eventbus.EventBus

/**
 *  author : yanghaozhang
 *  date : 2020/9/16 10:55
 *  description :
 */
abstract class BaseViewModel : AndroidViewModel(BaseApplication.INSTANCE), DIAware, IViewModel {

    override val di: DI by di()

    override val diContext: DIContext<*>
        get() = diContext(this)

    open val useEventBus: Boolean = true

    init {
        if (useEventBus) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onCleared() {
        if (useEventBus) {
            EventBus.getDefault().unregister(this)
        }
    }
}