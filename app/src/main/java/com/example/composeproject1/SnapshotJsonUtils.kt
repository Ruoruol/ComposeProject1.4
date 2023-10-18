package com.example.composeproject1

import android.content.Context
import android.util.ArrayMap
import com.example.composeproject1.bean.CityAreaBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject


object SnapshotJsonUtils {

    fun printJson(context: Context) {
        context.assets.open("snapshot.json").bufferedReader().use {
            it.readText()
        }.also {
            val cityMap = ArrayMap<String, MutableList<CityAreaBean>>()
            val list: List<CityAreaBean> =
                Gson().fromJson(it, object : TypeToken<List<CityAreaBean>>() {}.type)
            list.forEach { bean ->
                val areaList = if (cityMap.contains(bean.county)) {
                    cityMap[bean.county]!!
                } else {
                    val mutList = mutableListOf<CityAreaBean>()
                    cityMap[bean.county] = mutList
                    mutList
                }
                areaList += bean
            }
            val jsonArray = JSONArray()
            cityMap.forEach { t, u ->
                val json = JSONObject()
                json.put("cityName", t)
                val array = JSONArray()
                u.forEach { bean ->
                    val areaJson = JSONObject()
                    areaJson.put("areaId", bean.siteid)
                    areaJson.put("city", bean.county)
                    areaJson.put("areaName", bean.sitename)
                    array.put(areaJson)
                }
                json.put("areaList", array)
                jsonArray.put(json)
            }

        }
    }
}