package com.example.armmvvm.ui.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.arm.http.ResponseErrorObserver
import com.example.arm.http.Results
import com.example.arm.integration.lifecycle.Lifecycleable
import com.example.arm.mvvm.BaseViewModel
import com.example.arm.util.AppManager
import com.example.arm.util.RxLifecycleUtils
import com.example.armmvvm.http.net.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.jessyan.rxerrorhandler.handler.RetryWithDelay
import org.kodein.di.instance
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/18 11:04
 *  description :
 *  1,RxJava Lifecycle 需要传递Lifecycleable绑定生命周期
 *  2,CompositeDisposable
 *  3,viewModelScope 协程
 */
class TestViewModel(val model: TestModel) : BaseViewModel() {

    val mAppManager: AppManager by instance {
        // 调用时执行
        Timber.tag("TestViewModel").d("null() called   %s ", "instance AppManager")
    }

    override val useEventBus: Boolean
        get() = false

    private val _provinceLiveData = MutableLiveData<ResponseListBean<ProvinceBean>>()
    val provinceLiveData: LiveData<ResponseListBean<ProvinceBean>> = _provinceLiveData

    private val _cityLiveData = MutableLiveData<ResponseListBean<CityBean>>()
    val cityLiveData: LiveData<ResponseListBean<CityBean>> = _cityLiveData

    private val _weatherLiveData = MutableLiveData<ResponseBean<WeatherBean>>()
    val weatherLiveData: LiveData<ResponseBean<WeatherBean>> = _weatherLiveData

    fun getProvinceByRxLife(life: Lifecycleable<*>) {
        model.request()
            .subscribeOn(Schedulers.io())
            .retryWhen(RetryWithDelay(3, 2)) //遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
            .observeOn(AndroidSchedulers.mainThread())
            .compose(RxLifecycleUtils.bindToLifecycle(life))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
            .subscribe(object :
                ResponseErrorObserver<ResponseListBean<ProvinceBean>?>(mErrorListener) {
                override fun onNext(t: ResponseListBean<ProvinceBean>) {
                    _provinceLiveData.value = t
                }
            })
    }

    fun getProvinceByCompositeDisposable() {
        model.request()
            .subscribeOn(Schedulers.io())
            .retryWhen(RetryWithDelay(3, 2)) //遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(withDispose {
                _provinceLiveData.value = it
            })
    }

    fun getProvinceByCoroutines() {
        viewModelScope.launch {
            val requestProvince = requestByCoroutines {
                model.request2().execute()
            }
            when (requestProvince) {
                is Results.Failure -> {
                    mErrorListener?.handleResponseError(requestProvince.error)
                }
                is Results.Success -> {
                    _provinceLiveData.value = requestProvince.data!!
                }
            }
        }
    }

    fun geCityByCoroutines(bean: Map<String, String>) {
        viewModelScope.launch {
            val responseCity = requestByCoroutines {
                model.requestCity(bean).execute()
            }
            when (responseCity) {
                is Results.Failure -> {
                    mErrorListener?.handleResponseError(responseCity.error)
                }
                is Results.Success -> {
                    _cityLiveData.value = responseCity.data!!
                }
            }
        }
    }

    fun geWeatherByCoroutines(bean: Map<String, String>) {
        viewModelScope.launch {
            val response = requestByCoroutines {
                model.requestWeather(bean).execute()
            }
            when (response) {
                is Results.Failure -> {
                    mErrorListener?.handleResponseError(response.error)
                }
                is Results.Success -> {
                    _weatherLiveData.value = response.data!!
                }
            }
        }
    }

    fun geWeatherByCoroutines2(bean: Map<String, String>) {
        viewModelScope.launch {
            model.requestWeather2(bean)
                .collect {
                    // 将数据提供给 Activity 或者 Fragment
                    _weatherLiveData.postValue(it)
                }
        }
    }
}