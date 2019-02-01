package com.eebbk.monkeytest.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.eebbk.monkeytest.R;
import com.eebbk.monkeytest.receiver.AlarmReceiver;
import com.eebbk.monkeytest.receiver.SettingsContentObserver;
import com.eebbk.monkeytest.util.MonkeyCommandUtils;
import com.eebbk.monkeytest.util.MonkeyUtil;
import com.eebbk.monkeytest.util.ToastUtil;
import com.orhanobut.logger.Logger;

/**
 * 作者：lj
 * 实现的主要功能：
 * 创建日期：17-7-7
 * 修改信息：
 */
public class StartMonkeyService extends Service {

    private static long mStartMonkeyTimeStamp = 0;
    private SettingsContentObserver mSettingsContentObserver;
    private AlarmReceiver mAlarmReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("MonkeyTest onCreated");

        compatApi26Service();

        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(mBatInfoReceiver, filter);

        IntentFilter usbFilter = new IntentFilter("android.hardware.usb.action.USB_STATE");
//        usbFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mUsbBroadcastReceiver, usbFilter);

        mAlarmReceiver = new AlarmReceiver();
        IntentFilter wifiFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mAlarmReceiver, wifiFilter);

        mSettingsContentObserver = new SettingsContentObserver(this, new Handler());
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (!MonkeyUtil.getMonkeyState(this)) {
//            Logger.e("MonkeyTest has onStartCommanded twice!");
////            Intent intent2 = new Intent(StartMonkeyService.this, MonkeyTestResultActivity.class);
////            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            StartMonkeyService.this.startActivity(intent2);
////            MonkeyUtil.setMonkeyState(this, true);
//            return START_NOT_STICKY;
//        } else
        {
            Logger.d("MonkeyTest onStartCommanded");
            if (!MonkeyUtil.getMonkeyState()) {
                startShellMonkey();
            }
            Intent intentControlService = new Intent(this, MonkeyControlService.class);
//            intentControlService.putExtra(MonkeyControlService.pid_key,android.os.Process.myPid());
            startService(intentControlService);
            if (MonkeyUtil.getIsKeepSoundOff(this)) {
                setAudioOff(this);
            }
            return START_STICKY;
        }


    }

    /**
     * 功能： 8.0以上不允许后台运行service，待完善
     *
     * @author LuoTingWei
     * @date 2018/12/15
     */
    void compatApi26Service() {
        if (Build.VERSION.SDK_INT >= 26) {//兼容8.0
            NotificationChannel channel = new NotificationChannel(getPackageName(), getString(R.string.app_name), NotificationManager.IMPORTANCE_MIN);
            channel.enableVibration(false);//去除振动

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) manager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(getApplicationContext(), getPackageName())
                    .setContentTitle("正在后台运行")
                    .setSmallIcon(R.mipmap.ic_launcher);

            startForeground(1, builder.build());
        }
    }


    @Override
    public void onDestroy() {
        Logger.d("MonkeyTest Destroyed");
        // onDestory()方法中解除注册
        if (mBatInfoReceiver != null) {
            try {
                unregisterReceiver(mBatInfoReceiver);
                unregisterReceiver(mUsbBroadcastReceiver);
                unregisterReceiver(mAlarmReceiver);
                getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        MonkeyUtil.setMonkeyState(false);
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


//    private static class LauncherActivityHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//            if (msg.what == 1) {
////
////                Intent intent = new Intent();
////                intent.setAction("com.eebbk.monkeytest.launchapp");
////                StartMonkeyService.this.sendBroadcast(intent);
//                LogUtil.e("启动无入口App的Activity");
//                startNoneLaunchApp();
//
//                //     mLauncherhandler.sendEmptyMessageDelayed(1, 1000);
//            } else if (msg.what == 5) {
//                LogUtil.e("启动Activity检查结果");
////                Intent intent2 = new Intent(StartMonkeyService.this, SettingActivity.class);
////                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                startActivity(intent2);
//                MonkeyUtil.execShell("am start -n com.eebbk.monkeytest/.activity.MainActivity");
//            }
//        }
//    }

//    private static void startNoneLaunchApp() {
//        MonkeyUtil.execShell("am start -n com.eebbk.tools.otaupdate/com.eebbk.tools.otaupdate.update.activity.UpdateMainActivity");
//    }

    /**
     * 功能： 执行shell启动Monkey
     */
    private void startShellMonkey() {
        MonkeyUtil.setMonkeyState(true);
        MonkeyUtil.setStartMonkeyTime(null);

        MonkeyUtil.execShell("setprop persist.sys.bgclean false");
        MonkeyCommandUtils.createMonkeyDir();

        String monkeyStr = "monkey -vvv";
        monkeyStr += MonkeyCommandUtils.getPkgString(MonkeyUtil.getHistoryString("pkgString", ""));
        monkeyStr += MonkeyCommandUtils.getDelayString(MonkeyUtil.getHistoryString("delayString", "300"));
        monkeyStr += MonkeyCommandUtils.getMotionPctString(MonkeyUtil.getHistoryString("motionString", "15"));
        monkeyStr += MonkeyCommandUtils.getTouchPctString(MonkeyUtil.getHistoryString("touchString", "15"));
        monkeyStr += MonkeyCommandUtils.getSysPctString(MonkeyUtil.getHistoryString("systemString", "15"));
        monkeyStr += MonkeyCommandUtils.getLaunchPctString(MonkeyUtil.getHistoryString("launchString", "15"));
        monkeyStr += MonkeyCommandUtils.getStopString(MonkeyUtil.getHistoryStop());
        monkeyStr += MonkeyCommandUtils.getTimesString(MonkeyUtil.getHistoryString("timeString", "3000000"));
        monkeyStr += MonkeyCommandUtils.getLogStr();
        monkeyStr += MonkeyCommandUtils.getRefreshStr();
        // 保存monkey 字符串 用来定时跑
        MonkeyUtil.setMonkeyStr(monkeyStr);
        MonkeyUtil.execShell(Runtime.getRuntime(), monkeyStr + "&");
        //获取系统时间的10位的时间戳
        mStartMonkeyTimeStamp = MonkeyUtil.getStartMonkeyTimeMillis();
        Logger.d(">>>>>>>>>>>>>>>>>>>> " + monkeyStr + "\n" + mStartMonkeyTimeStamp);

    }

    /**
     * 功能： 关闭monkey服务，显示结果
     *
     * @author LuoTingWei
     * @date 2018/12/15
     */
    public void stopAndShowResult() {
        Logger.d("-------------------------------> 关闭当前MonkeyTest");
        MonkeyUtil.setMonkeyState(false);

        Intent intentControlService = new Intent(StartMonkeyService.this, MonkeyControlService.class);
        intentControlService.putExtra(MonkeyControlService.pid_key, android.os.Process.myPid());
        intentControlService.putExtra(MonkeyControlService.status_key, true);
        intentControlService.setAction(MonkeyControlService.action);
        startService(intentControlService);//发送状态给MonkeyControlService，显示结果界面
        stopSelf();
        MonkeyUtil.stopMonkey();
    }

    /**
     * 监听系统按钮
     */
    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";


        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

                if (TextUtils.equals(reason, SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    // 自己随意控制程序，关闭...
                    long pressTime = System.currentTimeMillis();
//                    Logger.d("test", "HomeKey" + (pressTime - mHomeKeyPressedTime));

                    if (pressTime - mHomeKeyPressedTime >= 400) {
                        mHomeKeyPressedCount = 0;
                    } else {
                        mHomeKeyPressedCount++;
                    }

                    mHomeKeyPressedTime = pressTime;

                    if (mHomeKeyPressedCount > 5) {
                        stopAndShowResult();
                    }else {
                        if (mHomeKeyPressedCount==3){
                            ToastUtil.show("继续点击home键退出monkey");
                        }
                    }
                }
            } else if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                if (MonkeyUtil.getIsKeepSoundOff(context)) {
                    Logger.d("currVolume has changed!!!");
                    setAudioOff(context);
                }
            }
        }
    };


    /**
     * 监听usb插拔
     */
    private final BroadcastReceiver mUsbBroadcastReceiver = new BroadcastReceiver() {

        private static final String USB_CONTENT_STATE = "connected";
        public static final String USB_STATE_BOARDCAST = "android.hardware.usb.action.USB_STATE";
        private static final long TIME_DISPARITY = 8; //5s

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (TextUtils.equals(action, USB_STATE_BOARDCAST)) {
                final Bundle bundle = intent.getExtras();

                if (bundle.getBoolean(USB_CONTENT_STATE)) {
                    // usb 插入
                    long curTimeStamp = System.currentTimeMillis();
                    Logger.d("USB_CONTENT_STATE " + bundle.getBoolean(USB_CONTENT_STATE) +
                            " \n" + mStartMonkeyTimeStamp + " \n" + curTimeStamp + "\n" + (curTimeStamp - mStartMonkeyTimeStamp));

                    if (curTimeStamp - mStartMonkeyTimeStamp > TIME_DISPARITY * 1000) {
                        ToastUtil.show(context, "插入USB设备,关闭Monkey模式");
                        stopAndShowResult();
                    }
                } else {
//                    //   usb 拔出
//
//                    long curTimeStamp = System.currentTimeMillis()/1000;
//                    if ( curTimeStamp - mStartMonkeyTimeStamp >= TIME_DISPARITY ) {
//                        Toast.makeText(context, "拔出,mMonkeyStartState = "+mMonkeyStartState, Toast.LENGTH_LONG).show();
//
//                        Log.e("test", "-------------------------------> 关闭当前MonkeyTest，停止测试！！！！");
//                        Log.e("test", "-------------------------------> 关闭当前MonkeyTest，停止测试！！！！");
//                        Log.e("test", "-------------------------------> 关闭当前MonkeyTest，停止测试！！！！");
//                        MonkeyUtil.stopMonkey();
//                        MonkeyUtil.setMonkeyState(context, false);
//
////                        Intent intent2 = new Intent(StartMonkeyService.this, MainActivity.class);
////                        StartMonkeyService.this.startActivity(intent2);
//
//                        MonkeyUtil.execShell("am start -n com.eebbk.monkeytest/.activity.MainActivity");
//                    }
                }
            }
        }
    };

    /**
     * 功能： 关闭所有声音
     *
     * @author LuoTingWei
     * @date 2018/12/17
     */
    private void setAudioOff(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
    }

    private int mHomeKeyPressedCount = 0;
    private long mHomeKeyPressedTime = 0;

}
