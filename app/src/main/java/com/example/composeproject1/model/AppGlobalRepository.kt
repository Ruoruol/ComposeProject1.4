package com.example.composeproject1.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.util.Log
import com.example.composeproject1.App
import com.example.composeproject1.DBHelper
import com.example.composeproject1.bean.UserInfo
import com.example.composeproject1.model.Constant.DbKey.DB_USER_TABLE_NAME
import com.example.composeproject1.model.Constant.DbKey.KEY_USER_ID
import com.example.composeproject1.model.Constant.DbKey.KEY_USER_NAME
import com.example.composeproject1.model.Constant.DbKey.KEY_USER_PASSWORD
import com.example.composeproject1.model.Constant.FileDbKey.KEY_SP_USER_ID
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
            withContext(Dispatchers.Main) {
                if (isSuccess) {
                    userName = userInfo!!.userName
                    userId = userInfo.userId
                }
                func(
                    if (isSuccess) userInfo!!.userId else -1
                )
            }
        }
    }

    fun logout() {
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
    suspend fun login(context: Context, account: String, password: String): UserInfo? {
        return withContext(Dispatchers.IO) {
            // 查询用户名和密码相同的数据
            // 初始化数据库连接

            // 初始化数据库连接
            val mysql = DBHelper(App.appContext)
            val db = mysql.getWritableDatabase()
            // 查询用户名和密码相同的数据
            val cursor: Cursor = db.query(
                DB_USER_TABLE_NAME,
                arrayOf(KEY_USER_ID, KEY_USER_NAME, KEY_USER_PASSWORD),
                "${KEY_USER_NAME}=? and ${KEY_USER_PASSWORD}=?",
                arrayOf(account, password),
                null,
                null,
                null
            )

            val flag = cursor.count
            if (flag != 0) {
                cursor.moveToFirst()
                val userIdIndex = cursor.getColumnIndex(KEY_USER_ID)
                val userNameIndex = cursor.getColumnIndex(KEY_USER_NAME)
                Log.i(
                    "msgddd",
                    "${cursor.columnCount} userIdIndex:${userIdIndex} userNameIndex:${userNameIndex}"
                )
                @SuppressLint("Range") val userId =
                    cursor.getLong(userIdIndex)
                (loginStatusFlow as MutableStateFlow).value = true
                return@withContext UserInfo(
                    userId = userId,
                    userName = cursor.getString(userNameIndex)
                )
            }
            return@withContext null
        }
    }

}