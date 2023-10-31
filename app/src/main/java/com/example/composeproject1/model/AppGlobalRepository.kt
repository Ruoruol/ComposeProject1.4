package com.example.composeproject1.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.util.Log
import com.example.composeproject1.App
import com.example.composeproject1.DBHelper
import com.example.composeproject1.bean.UserInfo
import com.example.composeproject1.database.UserData
import com.example.composeproject1.model.Constant.DbKey.DB_USER_TABLE_NAME
import com.example.composeproject1.model.Constant.DbKey.KEY_USER_ID
import com.example.composeproject1.model.Constant.DbKey.KEY_USER_NAME
import com.example.composeproject1.model.Constant.DbKey.KEY_USER_PASSWORD
import com.example.composeproject1.model.Constant.FileDbKey.KEY_SP_USER_ID
import com.example.composeproject1.model.DatabaseRepository.getUserInfoByAccountAndPassword
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
                App.appContext.getSharedPreferences("username", Context.MODE_PRIVATE).edit()
            editor.putString("userName", value)
            editor.apply()
        }
        get() {
            val pref = App.appContext.getSharedPreferences("username", Context.MODE_PRIVATE)
            return pref.getString("userName", "") ?: ""
        }

    var userPassWord: String
        set(value) {
            val editor: SharedPreferences.Editor =
                App.appContext.getSharedPreferences("username", Context.MODE_PRIVATE).edit()
            editor.putString("userPassWord", value)
            editor.apply()
        }
        get() {
            val pref = App.appContext.getSharedPreferences("username", Context.MODE_PRIVATE)
            return pref.getString("userPassWord", "") ?: ""
        }
    var userId: Long
        set(value) {
            val editor: SharedPreferences.Editor =
                App.appContext.getSharedPreferences("username", Context.MODE_PRIVATE).edit()
            editor.putLong(KEY_SP_USER_ID, value)
            editor.apply()
        }
        get() {
            val pref = App.appContext.getSharedPreferences("username", Context.MODE_PRIVATE)
            return pref.getLong(KEY_SP_USER_ID, -1)
        }

    fun isLogin(): Boolean {
        return loginStatusFlow.value
    }

    fun login(context: Context, account: String, password: String, func: (Long) -> Unit) {
        scope.launch {
            val userInfo = login(context, account, password)
            val isSuccess = userInfo != null
            if (isSuccess) {
                userName = userInfo!!.userName
                userId = userInfo.userId
            }
            withContext(Dispatchers.Main) {
                func(
                    if (isSuccess) userInfo!!.userId else -1
                )
            }
        }
    }

    fun logout() {
        userId = -1
        userName = ""
        userPassWord = ""
        (loginStatusFlow as MutableStateFlow).value = false
    }

    /* suspend fun login(context: Context, account: String, password: String): Boolean {
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
     }*/
    suspend fun login(context: Context, account: String, password: String): UserData? {
        return withContext(Dispatchers.IO) {
            val userData = getUserInfoByAccountAndPassword(account, password)
            if (userData != null) {
                (loginStatusFlow as MutableStateFlow).value = true
                return@withContext userData
            }
            return@withContext null
        }
    }

}