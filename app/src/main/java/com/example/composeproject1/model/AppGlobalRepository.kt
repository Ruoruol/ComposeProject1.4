package com.example.composeproject1.model

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import com.example.composeproject1.App
import com.example.composeproject1.Mysql
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object AppGlobalRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val loginStatusFlow: StateFlow<Boolean> = MutableStateFlow(userName.isNotEmpty())

    var userName: String
        set(value) {
            val editor: SharedPreferences.Editor =
                App.appContext.getSharedPreferences("data", Context.MODE_PRIVATE).edit()
            editor.putString("userName", value)
            editor.apply()
        }
        get() {
            val pref = App.appContext.getSharedPreferences("data", Context.MODE_PRIVATE)
            return pref.getString("userName", "") ?: ""
        }

    var userPassWord: String
        set(value) {
            val editor: SharedPreferences.Editor =
                App.appContext.getSharedPreferences("data", Context.MODE_PRIVATE).edit()
            editor.putString("userPassWord", value)
            editor.apply()
        }
        get() {
            val pref = App.appContext.getSharedPreferences("data", Context.MODE_PRIVATE)
            return pref.getString("userPassWord", "") ?: ""
        }

    fun isLogin(): Boolean {
        return loginStatusFlow.value
    }

    fun login(context: Context, account: String, password: String, func: (Boolean) -> Unit) {
        scope.launch {
            val isSuccess = login(context, account, password)
            withContext(Dispatchers.Main) {
                func(isSuccess)
            }
        }
    }

    fun logout() {
        userName = ""
        userPassWord = ""
        (loginStatusFlow as MutableStateFlow).value = false
    }

    suspend fun login(context: Context, account: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val mysql = Mysql(context, "tables.db", null, 1)
            val db = mysql.readableDatabase
            //查询用户名和密码相同的数据
            val cursor: Cursor = db.query(
                "tables",
                arrayOf("usname", "uspwd"),
                " usname=? and uspwd=?",
                arrayOf(account, password),
                null,
                null,
                null
            )

            val isSuccess = cursor.count != 0
            if (isSuccess) {
                cursor.moveToFirst()
                userName = account
                userPassWord = password
            }
            (loginStatusFlow as MutableStateFlow).value = isSuccess
            return@withContext isSuccess
        }
    }

}