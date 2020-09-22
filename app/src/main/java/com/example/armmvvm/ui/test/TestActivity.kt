package com.example.armmvvm.ui.test

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.arm.base.BaseActivity
import com.example.arm.di.GlobalConfigModule
import com.example.arm.ext.DIViewModelFactory
import com.example.arm.ext.observe
import com.example.arm.http.ErrorListener
import com.example.arm.http.imageloader.ImageLoader
import com.example.arm.integration.IRepositoryManager
import com.example.arm.util.TestUtil
import com.example.armmvvm.R
import com.example.armmvvm.http.imageloader.ImageConfigImpl
import com.example.armmvvm.http.net.*
import kotlinx.android.synthetic.main.activity_test.*
import okhttp3.HttpUrl
import org.kodein.di.*
import org.kodein.di.android.di
import org.kodein.di.android.retainedSubDI
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class TestActivity : BaseActivity() {

    override val di: DI by retainedSubDI(di()) {
        bind<TestViewModel>() with singleton {
            TestViewModel(TestModel())
        }
    }

    val mHttpUrl: HttpUrl by instance()

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

    override fun initView(savedInstanceState: Bundle?) = R.layout.activity_test

    override fun initData(savedInstanceState: Bundle?) {
        printTest(savedInstanceState)
        observe(mTestViewModel.provinceLiveData, this::onNewProvince)
        observe(mTestViewModel.cityLiveData, this::onNewCity)
        observe(mTestViewModel.weatherLiveData, this::onNewWeather)
        recyclerview.layoutManager = GridLayoutManager(this, 4)
        recyclerview.adapter = provinceAdapter
        provinceAdapter.mOnClickListener = { _, province: ProvinceBean ->
            mTestViewModel.geCityByCoroutines(mapOf("province_id" to province.id))
        }

        recyclerview_city.layoutManager = GridLayoutManager(this, 4)
        recyclerview_city.adapter = cityAdapter
        cityAdapter.mOnClickListener = { _, city: CityBean ->
              var date = Date() //取时间
              val calendar: Calendar = GregorianCalendar()
              calendar.time = date
              calendar.add(Calendar.DATE, -1) //把日期往前减少一天，若想把日期向后推一天则将负数改为正数
              date = calendar.time
              val formatter = SimpleDateFormat("yyyy-MM-dd")
              mTestViewModel.geWeatherByCoroutines(mapOf("city_id" to city.id, "weather_date" to formatter.format(date)))
        }
    }

    private fun onNewWeather(responseBean: ResponseBean<WeatherBean>) {
        val result = responseBean.result
        tv_detail.setText("${result.city_name}--${result.day_weather} --${result.day_temp} --${result.night_weather} --${result.night_temp} ")
    }

    private fun printTest(savedInstanceState: Bundle?) {
        Timber.tag("TestActivity").d(mHttpUrl.url().toString())
        Timber.tag("TestActivity").d(mErrorListener?.javaClass?.simpleName ?: "not exist")
        Timber.tag("TestActivity").d("initData() called with: savedInstanceState = $savedInstanceState   %s ", "$mCache")
        Timber.tag("TestActivity").d("initData() called with: savedInstanceState = $savedInstanceState   %s ", "$mTestUtil")
        Timber.tag("TestActivity").d("initData() called with: savedInstanceState = $savedInstanceState   %s ", "$mImageLoader")
        Timber.tag("TestActivity")
            .d("initData() called with: savedInstanceState = $savedInstanceState   %s ", "$mRepositoryManager")
    }

    private fun onNewProvince(listBean: ResponseListBean<ProvinceBean>) {
        showMessage("province_new")
        provinceAdapter.mProvinceList = listBean.result.toMutableList()
        provinceAdapter.notifyDataSetChanged()
    }

    private fun onNewCity(responseList: ResponseListBean<CityBean>) {
        showMessage("city_new")
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