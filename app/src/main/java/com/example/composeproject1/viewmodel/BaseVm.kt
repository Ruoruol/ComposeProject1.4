package com.example.composeproject1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeproject1.mvi.IEffect
import com.example.composeproject1.mvi.IEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseVm<E : IEvent>(application: Application) : AndroidViewModel(application) {
    private val _effect = MutableSharedFlow<List<IEffect>>()

    val effect: SharedFlow<List<IEffect>>
        get() = _effect

    abstract fun dispatchEvent(event: E)
    fun emitEffect(vararg effects: IEffect) {
        viewModelScope.launch {
            _effect.emit(effects.toList())
        }

    }

}