package com.eebbk.monkeytest.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.eebbk.monkeytest.data.AppInfo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * 作者：lj
 * 实现的主要功能：
 * 创建日期：17-4-6
 * 修改信息：
 *
 * @author Lijun
 */
public class InstalledAppList {


    public static ArrayList<AppInfo> getPackages(Context context, boolean hasLaunchEntrance) {

        ArrayList<AppInfo> appList = new ArrayList<>();
        // 获取已经安装的所有应用, PackageInfo　系统类，包含应用信息
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);

            boolean isSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

            if (isSystemApp && detectFilterPackageName(packageInfo.packageName)) {
                continue;
            }
            if ("com.eebbk.monkeytest".equals(packageInfo.packageName)) {
                continue;
            }

            AppInfo appInfo = new AppInfo();
            appInfo.setAppName(
                    packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());//获取应用名称
            appInfo.setPackageName(packageInfo.packageName); //获取应用包名，可用于卸载和启动应用
            appInfo.setVersionName(packageInfo.versionName);//获取应用版本名
            appInfo.setVersionCode(packageInfo.versionCode);//获取应用版本号
//            try {
//                appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(context.getPackageManager()));//获取应用图标
//            } catch (Exception ignored) {
//            }
            appInfo.setSystemApp(isSystemApp);
            String launcherClass = getLauncherActivity(context, packageInfo.packageName);
            if (null != launcherClass) {
                appInfo.setLaunchClass(launcherClass);

                if (hasLaunchEntrance) {
                    appInfo.setAppPYName(PinyinHelper.convertToPinyinString(appInfo.getAppName(), "", PinyinFormat.WITHOUT_TONE));
                    appList.add(appInfo);
                }
            } else {
                Logger.d("Without Lancher Activity App!!! -->" + packageInfo.packageName);
                String nonelauncher = getNoneLauncherActivity(context, packageInfo.packageName);

                if (null != nonelauncher && !hasLaunchEntrance) {
                    appInfo.setAppPYName(PinyinHelper.convertToPinyinString(appInfo.getAppName(), "", PinyinFormat.WITHOUT_TONE));
                    appList.add(appInfo);
                }
            }
        }

        System.out.println("应用类别　：应用总数为:" + appList.size());
//        AppInfoComparator mc = new AppInfoComparator();
//        Collections.sort(appList, mc);

//        for ( AppInfo appInfo : appList ) {
//            System.out.println("应用类别　：+++++++++++:"+appInfo.getAppName() +", "+ appInfo.getPackageName()+", 是否系统App:"+appInfo.isSystemApp());
//        }

        return appList;
    }

    /**
     * 检测当前包名是否需要过滤掉
     *
     * @param packageName 包名
     * @return 是否过滤包名
     */
    private static boolean detectFilterPackageName(String packageName) {

        if (packageName.startsWith("com.android") || packageName.startsWith("android") || packageName.startsWith("com.mediatek")) {
            Logger.d("System Protected App!!! -->" + packageName);
            if (packageName.equals("com.android.calculator2")) {
                //过滤掉
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * 通过包名查看是否有桌面启动Activity
     */
    public static String getLauncherActivity(Context mainContext, String packageName) {
        // TODO 把应用杀掉然后再启动，保证进入的是第一个页面
        PackageInfo pi = null;
        try {
            pi = mainContext.getApplicationContext().getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);
        PackageManager pManager = mainContext.getApplicationContext().getPackageManager();
        List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent,
                0);

        if (apps.size() <= 0) {
            return null;
        }

        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String startAppName = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;
            Logger.d("启动的activity是: " + startAppName + ":" + className);
            return className;
        } else {
            return null;
        }
    }


    /**
     * 通过包名查看是否有桌面启动Activity
     */
    public static String getNoneLauncherActivity(Context mainContext, String packageName) {
        // TODO 把应用杀掉然后再启动，保证进入的是第一个页面
        Intent resolveIntent = new Intent();
        resolveIntent.setPackage(packageName);
        PackageManager pManager = mainContext.getApplicationContext().getPackageManager();
        List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent,
                0);

        if (apps.size() <= 0) {
            return null;
        }

        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String startAppName = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;

            Logger.d("启动的activity是: " + startAppName + ":" + className);

            return className;
        } else {
            return null;
        }
    }
}
