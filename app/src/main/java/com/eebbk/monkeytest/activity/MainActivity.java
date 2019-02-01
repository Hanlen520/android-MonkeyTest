package com.eebbk.monkeytest.activity;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eebbk.monkeytest.R;
import com.eebbk.monkeytest.services.StartMonkeyService;
import com.eebbk.monkeytest.util.MonkeyUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private static final String AUTO_START = "com.eebbk.monkeytest.AUTO_START";
    private static final String AUTO_STOP = "com.eebbk.monkeytest.AUTO_STOP";

    public static final int LAUNCHER_APP_RESULTCODE = 1211;
    public static final int NONELAUNCH_APP_RESULTCODE = 1212;

    private TextView tvPackage1;
    private TextView tvNoneLancherPackage2;
    private TextView tvRunConfig;

    //    private ArrayList<AppInfo> mAppInfoList;
//    private BfcVersionManager mBfcVersionManager;
    private MediaScannerConnection mediaScannerConnection;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//        MonkeyUtil.execShell("monkey -p com.eebbk.vision -v 10000");

        initView();
        initData();

        if (!MonkeyUtil.getHasUploadEvent()) {
//            TestResultPresenter testResultPresenter = new TestResultPresenter(getApplicationContext());
//            testResultPresenter.setUploadCallback(message -> snackbarShow("已上传上次运行日志"));
//            testResultPresenter.searchForResultsAndUpload();
            startActivity(new Intent(this,MonkeyTestResultActivity.class));
        }

        dealIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        dealIntent(intent);
    }

    private void dealIntent(Intent intent) {//供命令启动monkey用
        if (intent != null) {
            if (TextUtils.isEmpty(intent.getAction())) {
                return;
            }
            switch (intent.getAction()) {
                case AUTO_START:
                    String pkgs = intent.getStringExtra("pkgs");
                    Logger.d(TAG, "dealIntent: pkgs " + pkgs);
                    if (TextUtils.isEmpty(pkgs)) {
                        return;
                    }

                    String confs = intent.getStringExtra("confs");
                    Logger.d(TAG, "dealIntent: confs " + confs);
                    if (!TextUtils.isEmpty(confs)) {
                        String[] confItems = confs.split("#");
                        MonkeyUtil.setHistoryString("timeString", confItems[0]);
                        MonkeyUtil.setHistoryString("delayString", confItems[1]);
                        MonkeyUtil.setHistoryString("motionString", confItems[2]);
                        MonkeyUtil.setHistoryString("touchString", confItems[3]);
                        MonkeyUtil.setHistoryString("systemString", confItems[4]);
                        MonkeyUtil.setHistoryString("launchString", confItems[5]);
                    }

                    Logger.d(TAG, "dealIntent: " + intent.getAction()+" "+pkgs);

                    String pkgString = pkgs.replace("#", "\n");
                    tvPackage1.setText(pkgString);
                    MonkeyUtil.setHistoryString("pkgString", tvPackage1.getText().toString());

                    MonkeyUtil.setMonkeyState(true);
                    MonkeyUtil.setStartMonkeyTime("");
                    Intent monkeyServiceIntent = new Intent(MainActivity.this, StartMonkeyService.class);
                    startService(monkeyServiceIntent);
                    break;
                case AUTO_STOP:
                    MonkeyUtil.setMonkeyState(false);
                    MonkeyUtil.stopMonkey();

                    Intent testResultActivity = new Intent(MainActivity.this, MonkeyTestResultActivity.class);
                    startActivity(testResultActivity);
                    MonkeyUtil.execShell("am start -n com.eebbk.monkeytest/.activity.MonkeyTestResultActivity");
                    break;
            }
        }
    }

    private void initView() {
        setTitle("Monkey 自动化测试");

        Button selectBtn1 = (Button) findViewById(R.id.package_select_btn1);
        selectBtn1.setOnClickListener(v -> {
            gotoPackageActivity(tvPackage1.getText().toString(), LAUNCHER_APP_RESULTCODE);
        });
        findViewById(R.id.none_launch_package_select_btn1).setOnClickListener(v -> {
            gotoPackageActivity(tvNoneLancherPackage2.getText().toString(), NONELAUNCH_APP_RESULTCODE);
        });

        TextView tvExtraFun = (TextView) findViewById(R.id.iv_title_extra_fun);
        tvExtraFun.setVisibility(View.VISIBLE);
        tvExtraFun.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        tvRunConfig = (TextView) findViewById(R.id.show_run_config);
        findViewById(R.id.btn_config_modify).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MonkeyParamsConfigActivity.class);
            startActivity(intent);
        });


        tvPackage1 = (TextView) findViewById(R.id.tv_package_1);
        String pkgString = MonkeyUtil.getHistoryString("pkgString", "");
        tvPackage1.setText(pkgString);
        tvNoneLancherPackage2 = (TextView) findViewById(R.id.tv_none_launch_package_2);

        Button exeMonkeyBtn = (Button) findViewById(R.id.exe_monkey_btn);
        exeMonkeyBtn.setOnClickListener(v -> {
            // 判断包名  次数 都有输入
            if (tvPackage1.getText().toString().trim().equals("")) {
                snackbarShow("请选择需要测试的应用");
                return;
            }

            Intent intent = new Intent(MainActivity.this, StartMonkeyService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                //TODO 未测试8.0系统，待完善
                startForegroundService(intent);
            } else {
                startService(intent);
            }

        });

        Button clearMonkeyBtn = (Button) findViewById(R.id.clear_monkey_record_btn);
        clearMonkeyBtn.setOnClickListener(v -> {
            MonkeyUtil.execShell("rm sdcard/MonkeyTest/*");
            MonkeyUtil.execShell("rm sdcard/.crash/*");
            MonkeyUtil.setHasUploadEvent(false);
            MonkeyUtil.setHasUploadError(false);
            snackbarShow("已删除sdcard/MonkeyTest/ & sdcard/.crash/");
        });

        Button refreshMediaBtn = (Button) findViewById(R.id.refresh_media_btn);
        refreshMediaBtn.setOnClickListener(v ->
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (mediaScannerConnection != null && mediaScannerConnection.isConnected()) {
                    snackbarShow("正在刷新中");
                    return;
                }
                mediaScannerConnection = new MediaScannerConnection(context, new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {
                        snackbarShow("开始刷新");
                        mediaScannerConnection.scanFile(Environment.getExternalStorageDirectory().getAbsolutePath(), null);
                        Logger.d(Environment.getExternalStorageDirectory().getAbsolutePath());
                    }

                    @Override
                    public void onScanCompleted(String s, Uri uri) {
                        mediaScannerConnection.disconnect();
                        runOnUiThread(() -> snackbarShow("媒体库刷新成功"));
                    }
                });
                mediaScannerConnection.connect();
            } else {
                MonkeyUtil.execShell("am broadcast -a android.intent.action.MEDIA_MOUNTED -n com.android.providers.media/.MediaScannerReceiver -d file://" + Environment.getExternalStorageDirectory());
                toastShow("已发送刷新广播");
            }
        });
    }

    private void initData() {
        MonkeyUtil.setMonkeyState(false);
        if (MonkeyUtil.getIsFirst()){
            MonkeyUtil.setHasUploadEvent(true);
            MonkeyUtil.setHasUploadError(true);
            MonkeyUtil.setIsFirst(false);
        }
    }

    private void gotoPackageActivity(String packageListStr, int resultCode) {
        String[] pkgList = packageListStr.split("[\\s+\n]");
        ArrayList<String> pkgNotSpace = new ArrayList<>();
        for (String pkgItem : pkgList) {
            pkgItem = pkgItem.trim();
            if (!pkgItem.equals("")) {
                pkgNotSpace.add(pkgItem);
            }
        }


        Intent intent = new Intent(this, PackageSelectActivity.class);
        intent.putStringArrayListExtra("pkgList", pkgNotSpace);
        intent.putExtra("resultCode", resultCode);

//        Bundle bundle = new Bundle();
//        bundle.putInt("type",resultCode);
//        intent.putExtras(bundle);

        startActivityForResult(intent, resultCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LAUNCHER_APP_RESULTCODE) {
            ArrayList<String> pkgList = data.getStringArrayListExtra("pkgList");
            tvPackage1.setText("");
            String pkgString = "";
            for (String pkg : pkgList) {
                pkgString += pkg + "\n";
            }
            tvPackage1.setText(pkgString);

            MonkeyUtil.setHistoryString("pkgString", tvPackage1.getText().toString());
        } else if (resultCode == NONELAUNCH_APP_RESULTCODE) {
            snackbarShow("该功能暂时还未开通，请耐心等待");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setMonkeyConfigShow();

        if (MonkeyUtil.getMonkeyState()) {
            MonkeyUtil.setMonkeyState(false);
            MonkeyUtil.stopMonkey();
            MonkeyUtil.execShell("am start -n com.eebbk.monkeytest/.activity." + MainActivity.class.getSimpleName());
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaScannerConnection != null && mediaScannerConnection.isConnected()) {
            mediaScannerConnection.disconnect();
        }
        super.onDestroy();
    }

    private void setMonkeyConfigShow() {

        String info = "测试次数：[" + MonkeyUtil.getHistoryString("timeString", "3000000") + "]"
                + ", 单次延时：[" + MonkeyUtil.getHistoryString("delayString", "300") + "]"
                + ", 滑动事件比例：[" + MonkeyUtil.getHistoryString("motionString", "15") + "]"
                + ", 触摸事件比例：[" + MonkeyUtil.getHistoryString("touchString", "15") + "]"
                + ", 系统事件比例：[" + MonkeyUtil.getHistoryString("systemString", "15") + "]"
                + ", Activity比例：[" + MonkeyUtil.getHistoryString("launchString", "15") + "]";

        tvRunConfig.setText(info);
    }

    public void doExit(View view) {
        MonkeyUtil.setMonkeyState(false);
        MonkeyUtil.stopMonkey();
        System.exit(0);
    }
}
