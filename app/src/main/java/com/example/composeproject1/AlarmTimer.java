package com.example.composeproject1;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.composeproject1.model.DatabaseRepository;

public class AlarmTimer {
    // 周期性的闹钟
    public final static String TIMER_ACTION_REPEATING = "com.e_eduspace.TIMER_ACTION_REPEATING";
    // 定时闹钟
    public final static String TIMER_ACTION = "com.e_eduspace.TIMER_ACTION";

    /**
     * 设置周期性闹钟
     *
     * @param context
     * @param firstTime
     * @param cycTime
     * @param action
     * @param AlarmManagerType 闹钟的类型，常用的有5个值：AlarmManager.ELAPSED_REALTIME、
     *                         AlarmManager.ELAPSED_REALTIME_WAKEUP、AlarmManager.RTC、
     *                         AlarmManager.RTC_WAKEUP、AlarmManager.POWER_OFF_WAKEUP
     */
    public static void setRepeatingAlarmTimer(Context context, long firstTime,
                                              long cycTime, String action, int AlarmManagerType) {
        Intent myIntent = new Intent();
        myIntent.setAction(action);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManagerType, firstTime, cycTime, sender);
        //param1：闹钟类型，param1：闹钟首次执行时间，param1：闹钟两次执行的间隔时间，param1：闹钟响应动作。
    }

    /**
     * 设置定时闹钟
     *
     * @param context
     * @param cycTime
     * @param action
     * @param AlarmManagerType 闹钟的类型，常用的有5个值：AlarmManager.ELAPSED_REALTIME、
     *                         AlarmManager.ELAPSED_REALTIME_WAKEUP、AlarmManager.RTC、
     *                         AlarmManager.RTC_WAKEUP、AlarmManager.POWER_OFF_WAKEUP
     */
    public static boolean setAlarmTimer(Context context, int requestId, Bundle bundle, long cycTime,
                                        String action, int AlarmManagerType) {
        Log.i("alarm_receiver_log", "start " + requestId);

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtras(bundle);
        alarmIntent.setAction(action);

        PendingIntent sender = PendingIntent.getBroadcast(context, requestId, alarmIntent, FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarm.canScheduleExactAlarms()) {
            alarm.setExactAndAllowWhileIdle(AlarmManagerType, cycTime, sender);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 取消闹钟
     *
     * @param context
     * @param action
     */
    public static void cancelAlarmTimer(Context context, int requestId, String action) {
        Intent myIntent = new Intent();
        myIntent.putExtra("cancel", true);
        Log.i("alarm_receiver_log", "cancel " + requestId);
        DatabaseRepository.INSTANCE.setMedicationInvalid(requestId);
        myIntent.setAction(action);
        PendingIntent sender = PendingIntent.getBroadcast(context, requestId, myIntent, FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(sender);
    }

}
