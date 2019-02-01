package com.eebbk.monkeytest.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.eebbk.bfc.common.devices.DeviceUtils;
import com.eebbk.bfc.sequence.SequenceTools;
import com.eebbk.monkeytest.data.MonkeyErrorInfo;
import com.eebbk.monkeytest.data.MonkeyEventInfo;
import com.eebbk.monkeytest.util.AppInfoUtil;
import com.eebbk.monkeytest.util.MonkeyCommandUtils;
import com.eebbk.monkeytest.util.MonkeyUtil;
import com.eebbk.monkeytest.util.RetrofitUtil;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


/**
 * 检测结果并上传日志
 * <p>
 * Created by admin on 2018/5/16.
 */

public class TestResultPresenter {

    private ITestResultImp mITestResultImp;
    private boolean hasErrorMsgState = false;
    private String errorPackageNames = "以下App有问题：";
    private Context context;
    private String contextName = "TestResultPresenter";
    private UploadCallback uploadCallback;
    public static final String uploadMonkeyEvent = "uploadMonkeyEvent";
    public static final String uploadMonkeyError = "uploadMonkeyError";

    public TestResultPresenter(Context context) {
        this.context = context;
    }

    public TestResultPresenter(Context context, ITestResultImp mITestResultImp) {
        this.context = context;
        this.mITestResultImp = mITestResultImp;
    }


    public void searchForResultsAndUpload() {
        uploadEvent();
        detectMonkeyResultAndUpload();
        if (context instanceof Activity) {
            contextName = ((Activity) context).getComponentName().getClassName();
        }
    }

    public void setUploadCallback(UploadCallback uploadCallback) {
        this.uploadCallback = uploadCallback;
    }

    /**
     * 功能： 上传日志
     *
     * @修改 LuoTingWei
     * @date 2018/12/15
     */
    private void uploadEvent() {
        String startTime = MonkeyUtil.getStartMonkeyTime();
        String stopTime = MonkeyUtil.getLocalMonkeyTime();

        if (MonkeyUtil.getHasUploadEvent()) {
            Logger.d("HasUploadEvent, return");
            return;
        }

        String packageInfo = MonkeyCommandUtils.getPkgString(MonkeyUtil.getHistoryString("pkgString", ""));
        String[] pkgList = packageInfo.replace("-p,", "").split("[\\s+]");

        List<String> packageList = new ArrayList<>();
        Collections.addAll(packageList, pkgList);

        MonkeyEventInfo eventInfo = new MonkeyEventInfo(DeviceUtils.getBbkSn(),
                DeviceUtils.getModel(),
                DeviceUtils.getSystemVersion(),
                startTime,
                stopTime,
                packageList
        );

        Map<String, String> params = new HashMap<>();
        params.put("operate", uploadMonkeyEvent);
        params.put("info", SequenceTools.serialize(eventInfo));
        Logger.d(params);
        Observable<ResponseBody> result = RetrofitUtil.getInstance().getPostInterface().postMonkeyLog(params);
        result.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    MonkeyUtil.setHasUploadEvent(true);
                    try {
                        String msg = responseBody.string();
                        Logger.d(msg);
                        if (uploadCallback != null) {
                            uploadCallback.onFinish(uploadMonkeyEvent + msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },throwable -> Logger.d(throwable.getMessage()));
    }

    /**
     * 功能：检查crash并上传
     *
     * @修改 LuoTingWei
     * @date 2018/12/15
     */
    private void detectMonkeyResultAndUpload() {

        if (MonkeyUtil.getHasUploadError()) {
            Logger.d("HasUploadError, return");
            return;
        }

        String sdcardPath = getSDPath();
        String pkgString = MonkeyUtil.getHistoryString("pkgString", "");
        String[] appPkgList = pkgString.split("\n");

        String startMonkeyTime = MonkeyUtil.getHistoryString("lastest_exec_time", "2018-04-26");
        startMonkeyTime = startMonkeyTime.replace("-0", "-").replace(" ", "-");
        for (int idx = 0; idx < appPkgList.length; idx++) {
            if (TextUtils.isEmpty(appPkgList[idx]) || appPkgList[idx].length() <= 5) {
                continue;
            }

            String packagePath = sdcardPath + "/.crash/" + appPkgList[idx] + "/";
            String versionName = AppInfoUtil.getAppVersionName(context, appPkgList[idx]);

            File packageCrashFolder = new File(packagePath);

            if (packageCrashFolder.exists()) {
                // 读取文件夹下文件
                File[] files = packageCrashFolder.listFiles();

                if (null != files) {
                    boolean errorState = false;
                    for (int j = 0; j < files.length; j++) {
                        String item = files[j].getName();

                        if (!item.startsWith("anr") && !item.startsWith("crash")) {
                            continue;
                        }

                        String item_time = item.replace("anr--", "")
                                .replace("crash--", "")
                                .replace(".cr", "")
                                .replace(".txt", "");

                        if (item_time.compareTo(startMonkeyTime) < 0) {
                            continue;
                        }

                        try {
                            hasErrorMsgState = true;
                            String info = readFromSD(packagePath + item);

                            MonkeyErrorInfo monkeyErrorInfo = new MonkeyErrorInfo(DeviceUtils.getBbkSn(),
                                    DeviceUtils.getModel(),
                                    DeviceUtils.getSystemVersion(),
                                    versionName,
                                    appPkgList[idx],
                                    "0",
                                    info);

                            Map<String, String> params = new HashMap<>();
                            params.put("operate", uploadMonkeyError);
                            params.put("info", SequenceTools.serialize(monkeyErrorInfo));
                            RetrofitUtil retrofitUtil = RetrofitUtil.getInstance();
                            Observable<ResponseBody> result = retrofitUtil.getPostInterface().postMonkeyLog(params);
                            int finalIdx = idx;
                            int finalJ = j;
                            result.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(responseBody -> {
//                                        MonkeyUtil.setHasUploadError(true);
                                        try {
                                            String msg = responseBody.string();
                                            Logger.d(msg);
                                            if (finalIdx ==appPkgList.length-1&& finalJ ==files.length-1){
                                                if (uploadCallback != null) {
                                                    uploadCallback.onFinish(msg);
                                                }
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    },throwable -> Logger.d(throwable.getMessage()));


                            errorState = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (errorState) {
                        errorPackageNames += "###-> " + appPkgList[idx];
                    }

                } else {
                    MonkeyUtil.setHasUploadError(true);
                    Logger.d("应用包名" + appPkgList[idx] + "没发现异常，无需分析！！！");
                }
            } else {
                MonkeyUtil.setHasUploadError(true);
                Logger.t(contextName).d("应用包名" + appPkgList[idx] + "无异常，无需分析！！！");
            }
        }


        if (mITestResultImp != null) {
            new Handler(Looper.getMainLooper()).post(() -> {//主线程发送
                mITestResultImp.refreshResultView(hasErrorMsgState, errorPackageNames);
            });
        }
    }

    private String getSDPath() {
        String sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory().toString();//获取跟目录
        }
        return sdDir;
    }

    //读取SD卡中文件的方法
    //定义读取文件的方法:
    private String readFromSD(String filename) throws IOException {
        StringBuilder sb = new StringBuilder("");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            filename = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + filename;
            //打开文件输入流
            FileInputStream input = new FileInputStream(filename);
            byte[] temp = new byte[1024];

            int len = 0;
            //读取文件内容:
            while ((len = input.read(temp)) > 0) {
                sb.append(new String(temp, 0, len));
            }
            //关闭输入流
            input.close();
        }
        return sb.toString();
    }

    public interface UploadCallback {
        void onFinish(String message);
    }
}
