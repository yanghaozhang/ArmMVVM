package com.example.armmvvm.ui.test

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.arm.base.BaseActivity
import com.example.arm.di.GlobalConfigModule
import com.example.arm.ext.DIViewModelFactory
import com.example.arm.http.ErrorListener
import com.example.arm.http.imageloader.ImageLoader
import com.example.arm.integration.IRepositoryManager
import com.example.arm.util.TestUtil
import com.example.armmvvm.databinding.ActivityTestBinding
import com.example.armmvvm.http.imageloader.ImageConfigImpl
import com.example.armmvvm.http.net.*
import org.kodein.di.*
import org.kodein.di.android.di
import org.kodein.di.android.retainedSubDI
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class TestActivity : BaseActivity<ActivityTestBinding>() {

    override val di: DI by retainedSubDI(di()) {
        bind<TestViewModel>() with singleton {
            TestViewModel(TestModel())
        }
    }

    val mTestUtil: TestUtil by instance()

    val mImageLoader: ImageLoader<ImageConfigImpl> by instance()

    val mRepositoryManager: IRepositoryManager by instance()

    // 直接使用newInstance而不是di.newInstance,因为后者将立即实现,而此时Activity未能完全初始化,applicationContext为null,报NPE
    val mErrorListener: ErrorListener? by newInstance { instance<GlobalConfigModule>().mErrorListener }

    private val provinceAdapter = ProvinceAdapter()

    private val cityAdapter = CityAdapter()

    val mTestViewModel: TestViewModel by viewModels {
        DIViewModelFactory(di)
    }

    override fun initView(savedInstanceState: Bundle?) = ActivityTestBinding.inflate(layoutInflater)

    override fun initData(savedInstanceState: Bundle?) {
        printTest(savedInstanceState)
        mTestViewModel.provinceLiveData.observe(this, this::onNewProvince)
        mTestViewModel.cityLiveData.observe(this, this::onNewCity)
        mTestViewModel.weatherLiveData.observe(this, this::onNewWeather)
        binding.recyclerview.layoutManager = GridLayoutManager(this, 4)
        binding.recyclerview.adapter = provinceAdapter
        provinceAdapter.mOnClickListener = { _, province: ProvinceBean ->
            mTestViewModel.geCityByCoroutines(mapOf("province_id" to province.id))
        }

        binding.recyclerviewCity.layoutManager = GridLayoutManager(this, 4)
        binding.recyclerviewCity.adapter = cityAdapter
        cityAdapter.mOnClickListener = { _, city: CityBean ->
            var date = Date() //取时间
            val calendar: Calendar = GregorianCalendar()
            calendar.time = date
            calendar.add(Calendar.DATE, -1) //把日期往前减少一天，若想把日期向后推一天则将负数改为正数
            date = calendar.time
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val beanList = mapOf(
                "city_id" to city.id,
                "weather_date" to formatter.format(date)
            )
            // 如果使用Flow方法2,且不能在geWeatherByCoroutines3(beanList)前observe(),否则请求数据的参数为空
            // 且不能多次observe()
            /*mTestViewModel.geWeatherByCoroutines3(beanList)
            if (k) {
            //this::onNewWeather每次都是不同的一个对象,都相当于new Observer(){onNewWeather(value)}
                mTestViewModel.weatherLiveData2.observe(this, this::onNewWeather)
                k = false
            }*/
//            mTestViewModel.geWeatherByCoroutines4(beanList).observe(this, this::onNewWeather)
            mTestViewModel.geWeatherByCoroutines5(beanList)?.observe(this, this::onNewWeather)
        }
    }

    var k = true

    private fun onNewWeather(responseBean: ResponseBean<WeatherBean>) {
        val result = responseBean.result
        val weather =
            "${result.city_name}--${result.day_weather} --${result.day_temp} --${result.night_weather} --${result.night_temp} "
        binding.tvDetail.text = weather
        showMessage(weather)
    }

    private fun printTest(savedInstanceState: Bundle?) {
        Timber.tag("TestActivity").d(mErrorListener?.javaClass?.simpleName ?: "not exist")
        Timber.tag("TestActivity")
            .d("initData() called with: savedInstanceState = $savedInstanceState   %s ", "$mCache")
        Timber.tag("TestActivity")
            .d("initData() called with: savedInstanceState = $savedInstanceState   %s ", "$mTestUtil")
        Timber.tag("TestActivity")
            .d("initData() called with: savedInstanceState = $savedInstanceState   %s ", "$mImageLoader")
        Timber.tag("TestActivity")
            .d("initData() called with: savedInstanceState = $savedInstanceState   %s ", "$mRepositoryManager")
    }

    private fun onNewProvince(listBean: ResponseListBean<ProvinceBean>) {
        showMessage("get province success")
        provinceAdapter.mProvinceList = listBean.result.toMutableList()
        provinceAdapter.notifyDataSetChanged()
        cityAdapter.mCityList.clear()
        cityAdapter.notifyDataSetChanged()
    }

    private fun onNewCity(responseList: ResponseListBean<CityBean>) {
        showMessage("get city success")
        cityAdapter.mCityList = responseList.result.toMutableList()
        cityAdapter.notifyDataSetChanged()
    }

    fun getSupportProvince(view: View) {
        mTestViewModel.getProvinceByCoroutines()
    }

    fun getSupportProvinceByRxJava(view: View) {
        mTestViewModel.getProvinceByRxLife(this)
    }

    fun getSupportProvinceByCompositeDisposable(view: View) {
        mTestViewModel.getProvinceByCompositeDisposable()
    }
}