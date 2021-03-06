package com.example.arm.mvvm

import androidx.lifecycle.AndroidViewModel
import com.example.arm.base.BaseApplication
import com.example.arm.di.GlobalConfigModule
import com.example.arm.http.ErrorListener
import com.example.arm.http.HttpDisposableObserver
import com.example.arm.http.HttpRequestFailException
import com.example.arm.http.Results
import com.example.arm.integration.EventBusManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.*
import org.kodein.di.android.x.di
import retrofit2.Response

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

    val mCompositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    val mErrorListener: ErrorListener? by newInstance { instance<GlobalConfigModule>().mErrorListener }

    init {
        if (useEventBus) {
            EventBusManager.instance.register(this)
        }
    }

    open fun <T> withDispose(onNext: (any: T) -> Unit): HttpDisposableObserver<T> {
        val disposable = object : HttpDisposableObserver<T>() {
            override fun onNext(t: T) {
                onNext(t)
            }
        }
        mCompositeDisposable.add(disposable) //将所有 Disposable 放入容器集中处理
        return disposable
    }

    protected open suspend fun <R> requestByCoroutines(request: () -> Response<R>?): Results<R> = withContext(Dispatchers.IO) {
        val execute = try {
            request()
        } catch (e: Exception) {
            return@withContext Results.failure<R>(e)
        }

        if (execute?.isSuccessful == true) {
            return@withContext Results.success(execute.body()!!)
        }
        return@withContext Results.failure<R>(HttpRequestFailException())
    }

    override fun onCleared() {
        if (useEventBus) {
            EventBusManager.instance.unregister(this)
        }
        mCompositeDisposable.clear()
    }
}