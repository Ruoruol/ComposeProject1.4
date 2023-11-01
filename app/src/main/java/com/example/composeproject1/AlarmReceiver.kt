package com.example.composeproject1

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.composeproject1.AlarmTimer.TIMER_ACTION
import com.example.composeproject1.AlarmTimer.TIMER_ACTION_REPEATING
import com.example.composeproject1.model.AppGlobalRepository
import com.example.composeproject1.model.Constant
import com.example.composeproject1.model.DatabaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID


class AlarmReceiver : BroadcastReceiver() {
    private val NOTIFICATION_FLAG = 3
    private val CHANNEL_ID = "用藥通知"
    private val CHANNEL_ID_HISTORY = "行事曆"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(TIMER_ACTION_REPEATING)) {
            Log.i("alarm_receiver_log", "週期鬧鐘")
        } else if (intent.action.equals(TIMER_ACTION)) {
            val type = intent.getIntExtra("type", -1)
            if (type == Constant.TYPE_MEDICATION) {
                Log.i("alarm_receiver_log", "定時鬧鐘")
                val cancel = intent.getBooleanExtra("cancel", false)
                if (cancel) {
                    return
                }
                Log.i("alarm_receiver_log", "開始")
                val id = intent.getIntExtra("id", 0)
                if (id == 0) return
                scope.launch {
                    val medication = DatabaseRepository.getMedicationById(id) ?: return@launch
                    if (medication.isValid == 0) return@launch
                    DatabaseRepository.setMedicationInvalid(id)
                    withContext(Dispatchers.Main) {
                        val title = intent.getStringExtra("title")
                        val desc = intent.getStringExtra("desc")
                        Log.i("alarm_receiver_log", "$title $desc $id ${medication.title}")
                        createNotificationChannel(context)
                        val nextIntent = Intent(context, Myhome::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        val bundle = Bundle().apply {
                            putString("title", medication.title)
                            putString("desc", medication.description)
                        }
                        nextIntent.putExtras(bundle)
                        val pendingIntent: PendingIntent =
                            PendingIntent.getActivity(
                                context,
                                id,
                                nextIntent,
                                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle(title ?: "")
                            .setContentText(desc ?: "").setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true)
                        with(NotificationManagerCompat.from(context)) {
                            // notificationId is a unique int for each notification that you must define.
                            if (ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {

                                return@withContext
                            }
                            notify(id, builder.build())
                        }
                    }
                }
            } else if (type == Constant.TYPE_HISTORY) {
                dealHistory(context, intent)
            }
        }

    }

    private fun dealHistory(context: Context, intent: Intent) {
        val id = intent.getIntExtra("id", 0)
        if (id == 0) return
        scope.launch {
            DatabaseRepository.getDataHistoryById(id) ?: return@launch
            val title = intent.getStringExtra("title")
            createNotificationHistoryChannel(context)
            val nextIntent = Intent(context, CalenderActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(
                    context,
                    id,
                    nextIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            val builder = NotificationCompat.Builder(context, CHANNEL_ID_HISTORY)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("行事曆")
                .setContentText(title).setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true)
            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define.
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return@launch
                }
                notify(id, builder.build())
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "用藥通知"
            val descriptionText = "用於提醒用戶用藥"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

    private fun createNotificationHistoryChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "行事曆"
            val descriptionText = "用於用戶预约"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_HISTORY, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
