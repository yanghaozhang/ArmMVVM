package com.example.armmvvm.ui.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.arm.http.HttpDisposableObserver
import com.example.arm.http.ResponseErrorObserver
import com.example.arm.integration.lifecycle.Lifecycleable
import com.example.arm.mvvm.BaseViewModel
import com.example.arm.util.AppManager
import com.example.arm.util.RxLifecycleUtils
import com.example.armmvvm.http.net.ProvinceBean
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.jessyan.rxerrorhandler.handler.RetryWithDelay
import org.kodein.di.instance
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/18 11:04
 *  description :
 *  RxJava Lifecycle 需要传递Lifecycleable绑定生命周期
 *  CompositeDisposable
 *  viewModelScope 协程
 */
class TestViewModel(val model: TestModel) : BaseViewModel() {

    val mAppManager: AppManager by instance {
        // 调用时执行
        Timber.tag("TestViewModel").d("null() called   %s ", "instance AppManager")
    }

    private val _provinceLiveData = MutableLiveData<ProvinceBean>()
    val provinceLiveData: LiveData<ProvinceBean> = _provinceLiveData

    //
    //
    //
    fun getProvinceByRx(life: Lifecycleable<*>) {
        model.request()
            .subscribeOn(Schedulers.io())
            .retryWhen(RetryWithDelay(3, 2)) //遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
            .observeOn(AndroidSchedulers.mainThread())
            .compose(RxLifecycleUtils.bindToLifecycle(life))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
            .subscribe(object : ResponseErrorObserver<ProvinceBean?>(mErrorListener) {
                override fun onNext(t: ProvinceBean) {
                    _provinceLiveData.value = t
                }
            });
    }

    fun getProvinceByCompositeDisposable() {
        val disposable = object : HttpDisposableObserver<ProvinceBean>() {
            override fun onNext(t: ProvinceBean) {
                _provinceLiveData.value = t
            }
        }
        addDispose(disposable)
        model.request()
            .subscribeOn(Schedulers.io())
            .retryWhen(RetryWithDelay(3, 2)) //遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(disposable);
    }

    fun getProvince() {
        viewModelScope.launch {
            val requestProvince = requestProvince()
            Timber.tag("TestViewModel").d("getProvince() called   %s ", "$requestProvince")
            if (requestProvince != null) {
                _provinceLiveData.value = requestProvince
            }
        }
    }

    private suspend fun requestProvince(): ProvinceBean? = withContext(Dispatchers.IO) {
        val execute = try {
            model.request2().execute()
        } catch (e: Exception) {
            mErrorListener?.handleResponseError(e)
            return@withContext null
        }
        if (execute?.isSuccessful == true) {
            return@withContext execute.body()
        } else {
            return@withContext null
        }
    }


}