package com.eebbk.monkeytest.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.eebbk.monkeytest.activity.MonkeyTestResultActivity;
import com.eebbk.monkeytest.util.MonkeyUtil;
import com.orhanobut.logger.Logger;

/**
 * @author LuoTingWei
 *         功能 守护进程，用于主动结束monkey后打开结果界面
 * @date 2018/12/17
 */
public class MonkeyControlService extends Service {

    public static String action = "com.eebbk.monkeytest.ControlService";
    public static String status_key = "status";//monkey已启动
    public static String pid_key = "pid_key";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int pid;
        if (intent != null) {
            pid = intent.getIntExtra(pid_key, 0);
            boolean status = intent.getBooleanExtra(status_key, false);
            Logger.d("MonkeyControlService onStartCommand " + pid + " " + status);
            if (pid != 0 && status) {
                showResult();
            }
        }

        if (!MonkeyUtil.getMonkeyState()) {
            return START_NOT_STICKY;
        } else {
            return START_STICKY;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void showResult() {
        MonkeyUtil.setMonkeyState(false);
        MonkeyUtil.execShell("am start -n com.eebbk.monkeytest/.activity." + MonkeyTestResultActivity.class.getSimpleName());

    }
}
