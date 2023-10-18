package com.example.composeproject1.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeproject1.bean.AreaEntity
import com.example.composeproject1.bean.CityAreaBean
import com.example.composeproject1.bean.CityAreaEntity
import com.example.composeproject1.model.CityRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.charset.Charset

class MainVm(private val application: Application) : AndroidViewModel(application) {
    private val originDataBeanList = mutableListOf<CityAreaEntity>()
    val cityList: List<String> = mutableStateListOf()
    val curSelectAreaList: List<AreaEntity> = mutableStateListOf()
    var curSelectCity: String? by mutableStateOf(null)
    var curSelectArea: AreaEntity? by mutableStateOf(null)
    var curQuality: CityAreaBean? by mutableStateOf(null)
    private fun initEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            application.assets.open("area.json").bufferedReader(Charset.defaultCharset()).use {
                it.readText()
            }.also {
                val list: List<CityAreaEntity> =
                    Gson().fromJson(it, object : TypeToken<List<CityAreaEntity>>() {}.type)
                originDataBeanList.addAll(list)
                list.forEach { entity ->
                    cityList.add(entity.cityName)
                }
            }
        }
    }

    private fun <T> List<T>.add(t: T) {
        (this as MutableList<T>).add(t)
    }

    private fun <T> List<T>.addAll(list: List<T>) {
        (this as MutableList<T>).addAll(list)
    }

    private fun <T> List<T>.clear() {
        (this as MutableList<T>).clear()
    }


    private fun selectCity(cityName: String) {
        cityList.indexOf(cityName).takeIf { it >= 0 }?.let { index ->
            curSelectCity = cityName
            curSelectAreaList.clear()
            curSelectAreaList.addAll(originDataBeanList[index].areaList)
        }
    }

    private fun selectArea(areaId: String) {
        curSelectAreaList.indexOfFirst { it.areaId == areaId }.takeIf { it >= 0 }?.let { index ->
            curSelectArea = curSelectAreaList[index]
            viewModelScope.launch {
                val beanResult = CityRepository.fetchArea(areaId)
                Log.i("MyApp", "beanResult: ${beanResult.exceptionOrNull()?.message}")
                if (beanResult.isSuccess) {
                    curQuality = beanResult.getOrNull()
                } else {
                    curQuality=null
                    Toast.makeText(application, "获取数据失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun dispatchEvent(event: MainEvent) {
        when (event) {
            is MainEvent.Init -> {
                initEvent()
            }

            is MainEvent.SelectCity -> {
                selectCity(event.cityName)
            }

            is MainEvent.SelectArea -> {
                selectArea(event.areaId)
            }
            is MainEvent.Refresh ->{
                curSelectArea?.let {
                    selectArea(it.areaId)
                }
            }

            else -> {

            }
        }
    }
}

sealed class MainEvent {
    object Init : MainEvent()
    class SelectCity(val cityName: String) : MainEvent()
    class SelectArea(val areaId: String) : MainEvent()
    object Refresh: MainEvent()
}