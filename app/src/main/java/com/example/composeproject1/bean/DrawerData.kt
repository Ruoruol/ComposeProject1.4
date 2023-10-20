package com.example.composeproject1.bean

import android.app.Activity
import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerData(
    val title: String,
    val targetClazz: Class<out Activity>,
    val imageVector: ImageVector,
    val flags: Int? = null
)