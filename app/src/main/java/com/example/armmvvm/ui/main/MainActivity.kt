package com.example.armmvvm.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.arm.base.BaseActivity
import com.example.arm.ext.DIViewModelFactory
import com.example.arm.http.imageloader.ImageLoader
import com.example.armmvvm.R
import com.example.armmvvm.databinding.ActivityMainBinding
import com.example.armmvvm.http.imageloader.ImageConfigImpl
import com.example.armmvvm.http.imageloader.ImageConfigImplBuilder
import com.example.armmvvm.ui.livedata.LiveDataActivity
import com.example.armmvvm.ui.refreshlayout.ToolbarActivity
import com.example.armmvvm.ui.scrollview.PreNestedScrollViewActivity
import com.example.armmvvm.ui.test.TestActivity
import org.kodein.di.DI
import org.kodein.di.android.di
import org.kodein.di.android.retainedSubDI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:13
 *  description :
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val di: DI by retainedSubDI(di()) {
        bind<MainViewModel>() with singleton {
            MainViewModel(MainModel())
        }
    }

    val mMainViewModel: MainViewModel by viewModels() {
        DIViewModelFactory(di)
    }

    val mImageLoader: ImageLoader<ImageConfigImpl> by instance()

    override fun initView(savedInstanceState: Bundle?) = ActivityMainBinding.inflate(layoutInflater)

    override fun initData(savedInstanceState: Bundle?) {
        mImageLoader.loadImage(this, ImageConfigImplBuilder {
            url =
                "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1600419019&di=459faf2d2c55150df94d1e1b91337fea&src=http://gss0.baidu.com/94o3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item/14ce36d3d539b6002ac5706de850352ac75cb7e4.jpg"
            imageView = binding.ivImg
            placeHoldDrawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_launcher_background)
        })
    }

    fun onclick(view: View) {
        startActivity(Intent(this, TestActivity::class.java))
    }

    fun onclick2(view: View) {
        startActivity(Intent(this, ToolbarActivity::class.java))
    }

    fun onclick3(view: View) {
        startActivity(Intent(this, LiveDataActivity::class.java))
    }

    fun onclick4(view: View) {
        startActivity(Intent(this, PreNestedScrollViewActivity::class.java))
    }
}