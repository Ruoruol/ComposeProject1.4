package com.example.composeproject1.utils

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

fun Context.requestAlarmPermission(
    lifecycleOwner: LifecycleOwner,
    permissionStateChangedFunc: ((Boolean) -> Unit)
) {
    val permissionHelper =
        AlarmPermissionHelper(this)
    try {
        if (permissionHelper.checkWithRequestPermission()) {
            permissionStateChangedFunc(true)
        } else {
            lifecycleOwner.lifecycleScope.launch {
                delay(500)
                lifecycleOwner.lifecycleScope.launchWhenResumed {
                    if (permissionHelper.hasPermission()) {
                        permissionStateChangedFunc.invoke(true)
                    } else {
                        permissionStateChangedFunc.invoke(false)
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        permissionStateChangedFunc.invoke(false)
    }

}

class AlarmPermissionHelper(
    private val context: Context,
) {
    fun checkWithRequestPermission(isGoSetting: Boolean = true): Boolean {
        return if (!hasPermission()) {
            if (isGoSetting) {
                context.startActivity(
                    Intent(
                        ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:" + context.packageName)
                    )
                )
            }
            false
        } else {
            true
        }
    }

    fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            true
        } else {
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm.canScheduleExactAlarms()
        }
    }
}