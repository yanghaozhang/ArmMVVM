package com.example.armmvvm.ui.livedata

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.arm.base.BaseActivity
import com.example.arm.ext.DIViewModelFactory
import com.example.armmvvm.R
import com.example.armmvvm.ui.livedata.fragment.MutableFragment
import com.example.armmvvm.ui.livedata.fragment.UnPeekFragment
import kotlinx.android.synthetic.main.activity_live_data.*
import org.kodein.di.DI
import org.kodein.di.android.di
import org.kodein.di.android.retainedSubDI
import org.kodein.di.bind
import org.kodein.di.singleton
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/10/28
 *  description :
 */
class LiveDataActivity : BaseActivity() {

    override val di: DI by retainedSubDI(di()) {
        bind<LiveDataViewModel>() with singleton {
            LiveDataViewModel(LiveDataModel())
        }
    }

    val mLiveDataViewModel: LiveDataViewModel by viewModels() {
        DIViewModelFactory(di)
    }

    var mUnPeekFragmentIndex = 0
    var mMutableFragmentIndex = 0

    val mMainFragment = UnPeekFragment("UnPeekLiveData-UU")
    val mMainFragment2 = UnPeekFragment("UnPeekLiveData-KK")
    val mChangeFragment = MutableFragment("MutableLiveData-MM")
    val mChangeFragment2 = MutableFragment("MutableLiveData-TT")

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_live_data
    }

    override fun initData(savedInstanceState: Bundle?) {
        mLiveDataViewModel.unPeekLiveData.observe(this, this::onStateChange)
        mLiveDataViewModel.mutableLiveData.observe(this, this::onStateChange2)

        changeFragmentClickUnPeek(frame_content)
        changeFragmentClickMutable(frame_content2)
    }

    private fun onStateChange(msg: String) {
        Timber.tag("LiveDataActivity").d("onStateChange() called with: msg = $msg   %s ", "")
    }

    private fun onStateChange2(msg: String) {
        Timber.tag("LiveDataActivity").d("onStateChange2() called with: msg = $msg   %s ", "")
    }

    fun unPeekLiveDataChangeTag(view: View) {
        mLiveDataViewModel.updateTagInfo()
    }

    fun changeFragmentClickUnPeek(view: View) {
        mUnPeekFragmentIndex = if (mUnPeekFragmentIndex == 0) 1 else 0

        val showFragment = if (mUnPeekFragmentIndex == 0) mMainFragment else mMainFragment2
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.replace(R.id.frame_content, showFragment).commit()
    }

    fun mutableLiveDataChangeTag(view: View) {
        mLiveDataViewModel.updateMutableTagInfo()
    }

    fun changeFragmentClickMutable(view: View) {
        mMutableFragmentIndex = if (mMutableFragmentIndex == 0) 1 else 0

        val showFragment = if (mMutableFragmentIndex == 0) mChangeFragment else mChangeFragment2
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.replace(R.id.frame_content2, showFragment).commit()
    }
}