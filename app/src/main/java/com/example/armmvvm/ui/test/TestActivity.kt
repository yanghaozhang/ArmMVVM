package com.example.armmvvm.ui.test

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
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
import com.example.armmvvm.http.net.ProvinceBean
import okhttp3.HttpUrl
import org.kodein.di.*
import org.kodein.di.android.di
import org.kodein.di.android.retainedSubDI
import timber.log.Timber

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

    val mTestViewModel: TestViewModel by viewModels {
        DIViewModelFactory(di)
    }

    override fun initView(savedInstanceState: Bundle?) = R.layout.activity_test

    override fun initData(savedInstanceState: Bundle?) {
        printTest(savedInstanceState)
        observe(mTestViewModel.provinceLiveData, this::onNewProvince)
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

    private fun onNewProvince(bean: ProvinceBean) {
        showMessage("province_new")
    }

    fun getSupportProvince(view: View) {
        mTestViewModel.getProvince()
    }

    fun getSupportProvinceByRxJava(view: View) {
        mTestViewModel.getProvinceByRx(this)
    }
    fun getSupportProvinceByCompositeDisposable(view: View) {
        mTestViewModel.getProvinceByCompositeDisposable()
    }
}