package com.example.composeproject1.model

import android.content.res.Resources.NotFoundException
import android.util.Log
import com.example.composeproject1.bean.CityAreaBean
import com.example.composeproject1.bean.CityNetBean
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object CityRepository {
    suspend fun fetchArea(siteId: String): Result<CityAreaBean> {
        return withContext(Dispatchers.IO) {

            val client = OkHttpClient()

            // 替换为模拟错误的 URL

            // 替换为模拟错误的 URL
            val apiUrl =
                "https://data.moenv.gov.tw/api/v2/aqx_p_432?api_key=595c99b1-c44e-43e7-9398-a010e424e212" // 模拟错误的 URL
            try {
                val request: Request = Request.Builder()
                    .url(apiUrl)
                    .addHeader("Content-Type", "text/html;charset=utf-8")
                    .build()

                // 执行请求
                Log.d("MyApp", "Sending network request...")
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    // 读取响应数据
                    val result = response.body!!.string()
                    Log.i("MyApp", "result:\n $result")
                    val list: List<CityAreaBean> =
                        Gson().fromJson(result, CityNetBean::class.java).records
                    kotlin.run {
                        list.forEach {
                            if (it.siteid == siteId) {
                                return@withContext Result.success(it)
                            }
                        }
                    }
                    return@withContext Result.failure<CityAreaBean>(NotFoundException("not found id $siteId"))
                } else {
                    // 处理错误情况
                    // 获取错误状态码
                    val errorCode = response.code

                    // 获取错误消息
                    val errorMessage = response.message

                    return@withContext Result.failure<CityAreaBean>(IOException(" code $errorCode Error fetching data: $errorMessage"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext Result.failure<CityAreaBean>(e)
            }

        }
    }
}