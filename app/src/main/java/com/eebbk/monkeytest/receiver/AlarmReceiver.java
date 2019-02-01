package com.eebbk.monkeytest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.eebbk.monkeytest.activity.MonkeyActivity;
import com.eebbk.monkeytest.util.MonkeyUtil;
import com.eebbk.monkeytest.util.ToastUtil;
import com.orhanobut.logger.Logger;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Logger.d("action get : ---->" + action);
        if (action.equals("com.eebbk.monkeytest.alarm")) {
            if (!MonkeyUtil.getIsAlarmOn(context)) {
                MonkeyUtil.cancelAlarm(context);
            }

            String monkeyStr = MonkeyUtil.getMonkeyStr();
            Logger.d(monkeyStr);
            if (!TextUtils.isEmpty(monkeyStr)) {
                ToastUtil.show("定时任务启动");
                MonkeyUtil.execShell("am start -n com.eebbk.monkeytest/.activity." + MonkeyActivity.class.getSimpleName());

//                Intent intentStartRun = new Intent(context, MonkeyActivity.class);
//                intentStartRun.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intentStartRun);

//                MonkeyUtil.execShell(monkeyStr + "&");
//                MonkeyUtil.setMonkeyState(context,false);
//                context.startService(new Intent(context, StartMonkeyService.class));//定时启动
            }
        } else if (action.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
            if (!MonkeyUtil.getMonkeyState() || !MonkeyUtil.getIsKeepWifiOn(context)) {
                Logger.d("startState:" + MonkeyUtil.getMonkeyState() + " wifi on:" + MonkeyUtil.getIsKeepWifiOn(context));
                return;
            }

            setWifiEnable(context, true);

            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            if (state == WifiManager.WIFI_STATE_DISABLED) {
                MonkeyUtil.openWifi();
            }
        } else {
            Logger.d("AlarmReceiver action get :" + action);
        }
    }

    public void setWifiEnable(Context context, boolean state) {
        //首先，用Context通过getSystemService获取wifimanager
        WifiManager mWifiManager = (WifiManager)
                context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //调用WifiManager的setWifiEnabled方法设置wifi的打开或者关闭，只需把下面的state改为布尔值即可（true:打开 false:关闭）

        int wifiState1 = mWifiManager.getWifiState();
        Logger.d("wifi状态---------》" + wifiState1);

        mWifiManager.setWifiEnabled(state);
    }
}
