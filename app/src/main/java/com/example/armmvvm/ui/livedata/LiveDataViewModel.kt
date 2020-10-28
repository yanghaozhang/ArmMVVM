package com.example.armmvvm.ui.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.arm.ext.UnPeekLiveData
import com.example.arm.mvvm.BaseViewModel
import com.example.arm.util.AppManager
import org.kodein.di.instance
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/11 1:20
 *  description : ViewModel 所需的Model通过构造提供
 */
class LiveDataViewModel(private val liveDataModel: LiveDataModel) : BaseViewModel() {

    val mAppManager: AppManager by instance {
        // 调用时执行
        Timber.tag("MainViewModel").d("null() called   %s ", "instance AppManager")
    }

    private val _unPeekLiveData:UnPeekLiveData<String> = UnPeekLiveData()
    val unPeekLiveData: LiveData<String> = _unPeekLiveData

    private val _mutableLiveData:MutableLiveData<String> = MutableLiveData()
    val mutableLiveData: LiveData<String> = _mutableLiveData

    var mUnPeekLiveDataTag = 0

    var mMutableLiveDataTag = 0

    fun updateTagInfo(){
        _unPeekLiveData.postValue("${++mUnPeekLiveDataTag}")
    }

    fun updateMutableTagInfo(){
        _mutableLiveData.postValue("${++mMutableLiveDataTag}")
    }

    override fun onCleared() {
        super.onCleared()
        liveDataModel.onDestroy()
    }
}