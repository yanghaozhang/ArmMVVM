package com.example.armmvvm.ui.test

import androidx.lifecycle.*
import com.example.arm.http.ResponseErrorObserver
import com.example.arm.http.Results
import com.example.arm.integration.lifecycle.Lifecycleable
import com.example.arm.mvvm.BaseViewModel
import com.example.arm.util.AppManager
import com.example.arm.util.RxLifecycleUtils
import com.example.armmvvm.http.net.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.*
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

    /**
     * Flow方法一
     */
    fun geWeatherByCoroutines2(bean: Map<String, String>) {
        viewModelScope.launch {
            model.requestWeather2(bean)
                .collect {
                    // 将数据提供给 Activity 或者 Fragment
                    _weatherLiveData.postValue(it)
                }
        }
    }

    /**
     * 注:
     * 一旦weatherLiveData2.observe()执行,就会立马获取数据-->CoroutineLiveData进行collect{}
     * 如果growZone.setValue()与旧值相同,那么将不会执行请求-->MutableStateFlow对新旧两值相同时不处理
     * 每次Observe都会生成新的监听:因为this::onNewWeather每次都是不同的一个对象,都相当于new Observer(){onNewWeather(value)}
     *
     * 另,如果通过function来获取LiveData并监听,好处在于能够解决上述问题,出现的问题为每次调用都会生成LiveData,会生成很多个LiveData
     *
     */
    private val growZone: MutableStateFlow<Map<String, String>> = MutableStateFlow(mutableMapOf())

    val weatherLiveData2 = growZone.flatMapLatest {
        Timber.tag("TAG----------").d("null() called $it")
        return@flatMapLatest model.requestWeather2(it)
    }
        .catch {
            Timber.e("kkkk")
        }
        .asLiveData()

    /**
     * Flow方法二
     *
     * 如果使用Flow方法2,且不能在geWeatherByCoroutines3(beanList)前observe(),否则请求数据的参数为空
     * 且不能多次observe()
     * 如果growZone.setValue()新值与旧值相同,那么将不会执行请求-->MutableStateFlow对新旧两值相同时不处理
     */
    fun geWeatherByCoroutines3(bean: Map<String, String>) {
        growZone.value = bean
    }

    /**
     * Flow方法三
     * 每次调用都会生成LiveData,导致Activity管理多个LiveData
     */
    fun geWeatherByCoroutines4(bean: Map<String, String>) = liveData {
        model.requestWeather2(bean)
            .collect {
                emit(it)
            }
    }

    private lateinit var growZone3 : MutableStateFlow<Map<String, String>>

    lateinit var weatherLiveData3 :LiveData<ResponseBean<WeatherBean>>

    /**
     * Flow方法四
     * 怪异
     * 如果growZone.setValue()新值与旧值相同,那么将不会执行请求-->MutableStateFlow对新旧两值相同时不处理
     */
    fun geWeatherByCoroutines5(bean: Map<String, String>): LiveData<ResponseBean<WeatherBean>>? {
        if (!this::growZone3.isInitialized) {
            growZone3 = MutableStateFlow(bean)
            weatherLiveData3 = growZone3.flatMapLatest {
                Timber.tag("TAG----------").d("null() called $it")
                return@flatMapLatest model.requestWeather2(it)
            }
                .catch {
                    Timber.e("kkkk")
                }
                .asLiveData()
            return weatherLiveData3
        }
        growZone3.value = bean
        return null
    }
}