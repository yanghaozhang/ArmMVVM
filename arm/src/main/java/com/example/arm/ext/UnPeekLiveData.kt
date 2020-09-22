package com.example.arm.ext

/**
 *  author : yanghaozhang
 *  date : 2020/9/16 11:14
 *  description :
 */
class UnPeekLiveData<T> : ProtectedUnPeekLiveData<T>() {

    public override fun setValue(value: T?) {
        super.setValue(value)
    }

    public override fun postValue(value: T?) {
        super.postValue(value)
    }
}