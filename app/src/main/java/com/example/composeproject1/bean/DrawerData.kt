package com.example.composeproject1.bean

import android.app.Activity

data class DrawerData(
    val title: String,
    val targetClazz: Class<out Activity>,
    val flags: Int? = null
)