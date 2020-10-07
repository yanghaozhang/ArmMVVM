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
import com.example.armmvvm.ui.main.fragment.ChangeFragment
import com.example.armmvvm.ui.main.fragment.MainFragment
import com.example.armmvvm.ui.refreshlayout.ToolbarActivity
import com.example.armmvvm.ui.test.TestActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.DI
import org.kodein.di.android.di
import org.kodein.di.android.retainedSubDI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import timber.log.Timber


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

    val mMainViewModel: MainViewModel by viewModels() {
        DIViewModelFactory(di)
    }

    val mImageLoader: ImageLoader<ImageConfigImpl> by instance()

    var mTag = 0
    var mMutableTag = 0

    var mFragmentIndex = 0
    var mMutableFragmentIndex = 0

    val mMainFragment = MainFragment("UnPeekLiveData-UU")
    val mMainFragment2 = MainFragment("UnPeekLiveData-KK")
    val mChangeFragment = ChangeFragment("MutableLiveData-MM")
    val mChangeFragment2 = ChangeFragment("MutableLiveData-TT")

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        mMainViewModel.printMainActivity()
        mImageLoader.loadImage(this, ImageConfigImplBuilder {
            url =
                "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1600419019&di=459faf2d2c55150df94d1e1b91337fea&src=http://gss0.baidu.com/94o3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item/14ce36d3d539b6002ac5706de850352ac75cb7e4.jpg"
            imageView = iv_img
            placeHoldDrawable = resources.getDrawable(R.drawable.ic_launcher_background, null)
        })

        mMainViewModel.shareLiveData.observe(this, this::onStateChange)
        mMainViewModel.shareMutableLiveData.observe(this, this::onStateChange2)

        changeFragmentClick(frame_content)
        changeFragmentClickMutable(frame_content2)
    }

    fun onclick(view: View) {
        startActivity(Intent(this, TestActivity::class.java))
    }

    fun onclick2(view: View) {
        startActivity(Intent(this, ToolbarActivity::class.java))
    }

    private fun onStateChange(msg: String) {
        Timber.tag("MainActivity").d("onStateChange() called with: msg = $msg   %s ", "")
        tv_title.setText("change to ${mTag++} and from Model is $msg")
    }

    private fun onStateChange2(msg: String) {
        Timber.tag("MainActivity").d("onStateChange2() called with: msg = $msg   %s ", "")
        tv_title2.setText("change to ${mMutableTag++} and from Model is $msg")
    }

    fun changeClick(view: View) {
        mMainViewModel.updateTagInfo()
    }

    fun changeFragmentClick(view: View) {
        mFragmentIndex = if (mFragmentIndex == 0) 1 else 0

        val showFragment = if (mFragmentIndex == 0) mMainFragment else mMainFragment2
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.replace(R.id.frame_content, showFragment)
            .commit()
    }

    fun changeClickMutable(view: View) {
        mMainViewModel.updateMutableTagInfo()
    }

    fun changeFragmentClickMutable(view: View) {
        mMutableFragmentIndex = if (mMutableFragmentIndex == 0) 1 else 0

        val showFragment = if (mMutableFragmentIndex == 0) mChangeFragment else mChangeFragment2
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.replace(R.id.frame_content2, showFragment)
            .commit()
    }

}