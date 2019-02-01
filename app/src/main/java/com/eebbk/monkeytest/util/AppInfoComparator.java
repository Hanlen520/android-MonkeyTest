package com.eebbk.monkeytest.util;

import com.eebbk.monkeytest.data.AppInfo;

import java.util.Comparator;

/**
 * 作者：lj
 * 实现的主要功能：
 * 创建日期：17-4-6
 * 修改信息：
 */
public class AppInfoComparator implements Comparator<AppInfo> {

    @Override
    public int compare(AppInfo appInfo1, AppInfo appInfo2) {
        return appInfo1.getAppPYName().compareTo(appInfo2.getAppPYName());
    }
}
