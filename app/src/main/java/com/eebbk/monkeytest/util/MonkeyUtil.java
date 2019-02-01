package com.eebbk.monkeytest.util;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.text.TextUtils;

import com.eebbk.monkeytest.MonkeyTestApplication;
import com.orhanobut.logger.Logger;

import net.grandcentrix.tray.AppPreferences;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 作者：3010
 * 实现的主要功能：调用shell命令执行MonkeyTest
 * 创建日期：2017/4/17
 * 修改信息：
 */

public class MonkeyUtil {
    public static AppPreferences appPreferences;

    static {
        appPreferences = new AppPreferences(MonkeyTestApplication.getInstance());
    }

    public static AppPreferences getAppPreferences() {
        return appPreferences;
    }

    public static void execShell(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec("sh");
            //权限设置
//            Process p = Runtime.getRuntime().exec("su");
            //获取输出流
            OutputStream outputStream = p.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            //将命令写入
            dataOutputStream.writeBytes(cmd);
            //提交命令
            dataOutputStream.flush();
            //关闭流操作
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void execShell(Runtime runtime, String cmd) {
        try {
            Process p = runtime.exec("sh");
            //获取输出流
            OutputStream outputStream = p.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            //将命令写入
            dataOutputStream.writeBytes(cmd);
            //提交命令
            dataOutputStream.flush();
            //关闭流操作
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void stopMonkey() {
//        MonkeyUtil.execShell("pkill commands.monkey");
        MonkeyUtil.execShell("am force-stop com.eebbk.monkeytest:test");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void closeAirPlaneMode() {
        MonkeyUtil.execShell("settings put global airplane_mode_on 0");
        MonkeyUtil.execShell("am broadcast -a android.intent.action.AIRPLANE_MODE –ez state true");
    }

    public static void openWifi() {
        MonkeyUtil.execShell("svc wifi enable");
    }

    public static void setAlarm(Context context, int hour, int minute) {

        Intent intent = new Intent("com.eebbk.monkeytest.alarm");
        PendingIntent pending = PendingIntent.getBroadcast(context.getApplicationContext(), 12315, intent, 0);

        Calendar calendar = Calendar.getInstance();
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMinute = calendar.get(Calendar.MINUTE);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        boolean tomorrow = false;
        if (curHour > hour) {
            tomorrow = true;
        } else if (curHour < hour) {
            tomorrow = false;
        } else if (curHour == hour) {
            tomorrow = curMinute >= minute;
        }
        if (tomorrow) {
            calendar.add(Calendar.DATE, 1);
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);
        } else {
            ToastUtil.show("SDK_INT<19,定时设置失败");
        }
    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent("com.eebbk.monkeytest.alarm");
        PendingIntent pending = PendingIntent.getBroadcast(context.getApplicationContext(), 12315, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pending);
    }

    public static int getHour() {
        return appPreferences.getInt("hour", 0);
    }

    public static int getMinute() {
        return appPreferences.getInt("minute", 0);
    }

    public static void setHour(int hour) {
        appPreferences.put("hour", hour);
    }

    public static void setMinute(int minute) {
        appPreferences.put("minute", minute);
    }

    public static boolean getIsAlarmOn(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("monkeytest", Context.MODE_PRIVATE);
        return appPreferences.put("alarm", false);
    }

    public static void setIsAlarmOn(boolean on) {
        appPreferences.put("alarm", on);
    }

    public static boolean getIsKeepWifiOn(Context context) {
        return appPreferences.put("wifi", false);
    }

    public static void setIsKeepWifiOn(boolean on) {
        appPreferences.put("wifi", on);
    }

    public static boolean getIsKeepSoundOff(Context context) {
        return appPreferences.put("sound", false);
    }

    public static void setIsKeepSoundOff(boolean on) {
        appPreferences.put("sound", on);
    }

    public static String getMonkeyStr() {
        return appPreferences.getString("monkeystr", "");
    }

    public static void setMonkeyStr(String str) {
        appPreferences.put("monkeystr", str);
    }

    public static boolean getMonkeyState() {
        boolean state = appPreferences.getBoolean("monkeyrunstate", false);
        Logger.d("monkeyrunstate Get---->" + state);
        return state;
    }

    public static void setMonkeyState(boolean isStart) {
        appPreferences.put("monkeyrunstate", isStart);
        Logger.d("monkeyrunstate Set---->" + isStart);
    }

    public static boolean isMonkeyRunning(Context context) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            Logger.d("info.processName ->" + info.processName);
            if (info.processName.equals("com.eebbk.monkeytest"/*"com.android.commands.monkey"*/)) {
                //Log.i("Service2进程", ""+info.processName);
                isRunning = true;
            }
        }

        return isRunning;
    }

    public static void wakeUpAndUnlock(Context context) {
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire(5000);
        //释放
        wl.release();

        //屏锁管理器
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
    }

    public static void setHistoryString(String key, String value) {
        appPreferences.put(key, value);
    }

    public static String getHistoryString(String key, String defaultValue) {
        return appPreferences.getString(key, defaultValue);
    }

    public static void setHistoryStop(Boolean isStop) {
        appPreferences.put("isStop", isStop);
    }

    public static Boolean getHistoryStop() {
        return appPreferences.getBoolean("isStop", false);
    }

    //执行shell monkey时调用
    public static void setStartMonkeyTime(String startTime) {
        String date = startTime;
        if (TextUtils.isEmpty(date)) {
            //存储当前调用时间，方便后面查证
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
            date = sdf.format(new Date());
        }
        Logger.d("setStartMonkeyTime " + date);
        setHistoryString("lastest_exec_time", date);
        setStartMonkeyTimeMillis(System.currentTimeMillis());
        setHasUploadEvent(false);
        setHasUploadError(false);
    }

    public static String getStartMonkeyTime() {
        return MonkeyUtil.getHistoryString("lastest_exec_time", "2018-01-01");
    }

    //启动的时间戳
    public static long getStartMonkeyTimeMillis() {
        return appPreferences.getLong("StartMonkeyTimeMillis", 0);
    }

    public static void setStartMonkeyTimeMillis(long time) {
        appPreferences.put("StartMonkeyTimeMillis", time);
    }

    //是否上传了日志
    public static boolean getHasUploadEvent() {
        return appPreferences.getBoolean("HasUploadEvent", false);
    }

    public static void setHasUploadEvent(boolean lastUploadEvent) {
        appPreferences.put("HasUploadEvent", lastUploadEvent);
    }

    public static boolean getHasUploadError() {
        return appPreferences.getBoolean("HasUploadError", false);
    }

    public static void setHasUploadError(boolean lastUploadError) {
        appPreferences.put("HasUploadError", lastUploadError);
    }

    public static boolean getIsFirst() {
        return appPreferences.getBoolean("isFirst", true);
    }

    public static void setIsFirst(boolean isFirst) {
        appPreferences.put("isFirst", isFirst);
    }

    public static String getLocalMonkeyTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
        String date = sdf.format(new Date());
        return date;
    }
}
