package com.example.arm.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.kodein.di.DI
import org.kodein.di.TT
import org.kodein.di.direct

class DIViewModelFactory(val di: DI) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
            di.direct.Instance(TT(modelClass))
}