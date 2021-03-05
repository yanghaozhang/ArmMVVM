package com.example.arm.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory<E : ViewModel?>(val block: (Class<E>) -> E) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return block(modelClass as Class<E>) as T
    }
}