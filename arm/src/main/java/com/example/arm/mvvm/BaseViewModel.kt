package com.example.arm.mvvm

import androidx.lifecycle.AndroidViewModel
import com.example.arm.base.BaseApplication
import com.example.arm.integration.EventBusManager
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.DIContext
import org.kodein.di.android.x.di
import org.kodein.di.diContext

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
            EventBusManager.instance.register(this)
        }
    }

    override fun onCleared() {
        if (useEventBus) {
            EventBusManager.instance.unregister(this)
        }
    }
}