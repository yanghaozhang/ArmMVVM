package com.example.armmvvm.ui.main

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
class MainViewModel(val mainModel: MainModel) : BaseViewModel() {
    override fun onCleared() {
        super.onCleared()
        mainModel.onDestroy()
    }
}