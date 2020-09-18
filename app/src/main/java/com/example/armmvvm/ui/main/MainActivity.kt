package com.example.armmvvm.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.arm.base.BaseActivity
import com.example.arm.ext.DIViewModelFactory
import com.example.arm.http.imageloader.ImageLoader
import com.example.armmvvm.R
import com.example.armmvvm.http.imageloader.ImageConfigImpl
import com.example.armmvvm.http.imageloader.ImageConfigImplBuilder
import com.example.armmvvm.ui.test.TestActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.*
import org.kodein.di.android.di
import org.kodein.di.android.retainedSubDI


/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:13
 *  description :
 */
class MainActivity : BaseActivity() {

    override val di: DI by retainedSubDI(di()) {
        bind<MainViewModel>() with singleton {
            MainViewModel(MainModel())
        }
    }

    val mainViewModel: MainViewModel by viewModels() {
        DIViewModelFactory(di)
    }

    val mImageLoader by newInstance<ImageLoader<ImageConfigImpl>> { instance<ImageLoader<*>>() as ImageLoader<ImageConfigImpl> }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        mainViewModel.printMainActivity()
        mImageLoader.loadImage(this, ImageConfigImplBuilder {
            url = "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1600419019&di=459faf2d2c55150df94d1e1b91337fea&src=http://gss0.baidu.com/94o3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item/14ce36d3d539b6002ac5706de850352ac75cb7e4.jpg"
            imageView = iv_img
            placeHoldDrawable = resources.getDrawable(R.drawable.ic_launcher_background, null)
        })
    }

    fun onclick(view: View) {
        startActivity(Intent(this, TestActivity::class.java))
    }

}