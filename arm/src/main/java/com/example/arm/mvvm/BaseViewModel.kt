package com.example.arm.mvvm

import androidx.lifecycle.AndroidViewModel
import com.example.arm.base.BaseApplication
import com.example.arm.di.GlobalConfigModule
import com.example.arm.http.ErrorListener
import com.example.arm.integration.EventBusManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.kodein.di.*
import org.kodein.di.android.x.di

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

    var mCompositeDisposable: CompositeDisposable? = null

    val mErrorListener: ErrorListener? by newInstance { instance<GlobalConfigModule>().mErrorListener }

    init {
        if (useEventBus) {
            EventBusManager.instance.register(this)
        }
    }

    open fun addDispose(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable!!.add(disposable) //将所有 Disposable 放入容器集中处理
    }

    override fun onCleared() {
        if (useEventBus) {
            EventBusManager.instance.unregister(this)
        }
        mCompositeDisposable?.clear()
    }
}