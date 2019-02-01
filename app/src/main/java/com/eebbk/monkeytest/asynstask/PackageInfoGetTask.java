package com.eebbk.monkeytest.asynstask;

import android.content.Context;
import android.os.AsyncTask;

import com.eebbk.monkeytest.activity.MainActivity;
import com.eebbk.monkeytest.data.AppInfo;
import com.eebbk.monkeytest.util.InstalledAppList;
import com.eebbk.monkeytest.util.AppInfoComparator;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by admin on 2017/11/24.
 */
@Deprecated
public class PackageInfoGetTask extends AsyncTask<Void, Void, ArrayList<AppInfo>> {

    private int mResultCode;
    private Context mContext;
    private ArrayList<AppInfo> mAppInfoList;

    public PackageInfoGetTask(int resultCode, Context context) {
        mResultCode = resultCode;
        mContext = context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected ArrayList<AppInfo> doInBackground(Void... voids) {

        if ( mResultCode == MainActivity.LAUNCHER_APP_RESULTCODE ) {
            mAppInfoList = InstalledAppList.getPackages(mContext, true);
        } else {
            mAppInfoList = InstalledAppList.getPackages(mContext, false);
        }

        Collections.sort(mAppInfoList, new AppInfoComparator());

        return mAppInfoList;
    }
}
