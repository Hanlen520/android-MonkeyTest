package com.eebbk.monkeytest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eebbk.monkeytest.activity.SettingActivity;
import com.eebbk.monkeytest.util.LogUtil;

/**
 * 作者：lj
 * 实现的主要功能：
 * 创建日期：17-7-8
 * 修改信息：
 */
public class startToLauncherApp extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("------------------------------->Start None Launcher Activity");
//        ComponentName cn = new ComponentName("com.android.settings",
//                "com.android.settings.DevelopmentSettings");
//
//        Intent intent2 = new Intent(Intent.ACTION_MAIN);
//        intent2.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent2.setComponent(cn);
//        context.getApplicationContext().startActivity(intent2);

        Intent intent2 = new Intent(context, SettingActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
    }
}
