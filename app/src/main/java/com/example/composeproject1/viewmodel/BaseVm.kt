package com.example.composeproject1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.composeproject1.mvi.IEvent

abstract class BaseVm<E : IEvent>(application: Application) : AndroidViewModel(application) {
    abstract fun dispatchEvent(event: E)

}