package com.example.composeproject1.utils

import android.widget.Toast
import com.example.composeproject1.App

object ToastUtils {
    fun shortToast(msg: String) {

        Toast.makeText(App.appContext, msg, Toast.LENGTH_SHORT).show()
    }
}