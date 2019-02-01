package com.eebbk.monkeytest.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：lj
 * 实现的主要功能：
 * 创建日期：17-7-7
 * 修改信息：
 */
public class MonkeyCommandUtils {

    public static String getPkgString(String packListStr) {
        String pkgString = "";
        String pkgTxt = packListStr;
        String[] pkgList = pkgTxt.split("[\\s+]");
        for(String pkgItem: pkgList) {
            pkgItem = pkgItem.trim();
            if (!pkgItem.equals("")) {
                pkgString += " -p ";
                pkgString += pkgItem;
            }
        }
        return pkgString;
    }

    public static String getTimesString(String timeSetStr) {
        if (TextUtils.isEmpty(timeSetStr)) {
            return "";
        }
        return " " +timeSetStr.trim();
    }

    public static String getDelayString(String delaySetStr) {
        if ( TextUtils.isEmpty(delaySetStr) ) {
            return "";
        }
        return " --throttle " + delaySetStr.trim();
    }

    public static String getMotionPctString(String motionPctStr) {
        if (TextUtils.isEmpty(motionPctStr)) {
            return "";
        }
        return " --pct-motion " + motionPctStr.trim();
    }

    public static String getTouchPctString(String touchPctStr) {
        if (TextUtils.isEmpty(touchPctStr)) {
            return "";
        }
        return " --pct-touch " + touchPctStr.trim();
    }

    public static String getSysPctString(String systemPctStr) {
        if (TextUtils.isEmpty(systemPctStr)) {
            return "";
        }
        return " --pct-syskeys " + systemPctStr.trim();
    }

    public static String getLaunchPctString(String launchPctStr) {
        if ( TextUtils.isEmpty(launchPctStr)) {
            return "";
        }
        return " --pct-appswitch " + launchPctStr.trim();
    }

    public static String getStopString(boolean stopSwitchState) {
        if (stopSwitchState) {
            return "";
        } else {
            return " --ignore-crashes --ignore-timeouts --ignore-security-exceptions --monitor-native-crashes --ignore-native-crashes";// --kill-process-after-error";
        }
    }

    public static String getDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-HH-mm");
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        return sdf.format(date);
    }

    public static String getInfoLogStr() {
        return "/sdcard/MonkeyTest/" + getDateStr() + "_info.txt";
    }

    public static String getErrorLogStr() {
        return "/sdcard/MonkeyTest/" + getDateStr() + "_error.txt";
    }

    public static String getLogStr() {
        return " 2>" + getErrorLogStr() + " 1>" + getInfoLogStr();
    }

    public static String getRefreshStr() {
        return "";
//        return " && am broadcast -a android.intent.action.MEDIA_MOUNTED -n com.android.providers.media/.MediaScannerReceiver -d file:///mnt/sdcard";
    }

    public static void createMonkeyDir() {
        MonkeyUtil.execShell("mkdir /sdcard/MonkeyTest");
    }

}
