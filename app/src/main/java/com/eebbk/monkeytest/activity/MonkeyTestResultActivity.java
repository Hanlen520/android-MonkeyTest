package com.eebbk.monkeytest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.eebbk.monkeytest.R;
import com.eebbk.monkeytest.presenter.ITestResultImp;
import com.eebbk.monkeytest.presenter.TestResultPresenter;
import com.eebbk.monkeytest.util.MonkeyUtil;


/**
 * Created by admin on 2018/4/26.
 */

public class MonkeyTestResultActivity extends BaseActivity implements ITestResultImp {

    private TestResultPresenter mTestResultPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_result;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MonkeyUtil.setHasUploadEvent(false);
        MonkeyUtil.setHasUploadError(false);
        initData();
        initView();
    }

    private void initView() {
        setTitle("MonkeyTest测试结果");
        setShowBackButton(true);
    }

    private void initData() {
        int pid = getIntent().getIntExtra("pid_key", 0);
        if (pid != 0) {
            android.os.Process.killProcess(pid);
        }
        mTestResultPresenter = new TestResultPresenter(this, this);
        mTestResultPresenter.setUploadCallback(message -> snackbarShow("已上传本次运行日志"));
        mTestResultPresenter.searchForResultsAndUpload();
    }

    @Override
    public void refreshResultView(boolean hasError, String errorInfo) {
        findViewById(R.id.search_app_info_bar).setVisibility(View.GONE);
        String startTime="\n"+MonkeyUtil.getStartMonkeyTime();

        if (hasError) {
            TextView resultTXT = (TextView) findViewById(R.id.show_result_txt);
            resultTXT.setText(getResources().getText(R.string.app_detect_result_error)+startTime);
            TextView resultErrorInfo = (TextView) findViewById(R.id.show_result_info);
            resultErrorInfo.setText(errorInfo);
        } else {
            TextView resultTXT = (TextView) findViewById(R.id.show_result_txt);
            resultTXT.setText(getResources().getText(R.string.app_detect_result_succeed)+startTime);
        }
    }

    @Override
    protected void onResume() {
        if (MonkeyUtil.getMonkeyState()) {
            MonkeyUtil.setMonkeyState(false);
            MonkeyUtil.stopMonkey();
            MonkeyUtil.execShell("am start -n com.eebbk.monkeytest/.activity.MonkeyTestResultActivity");
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        startActivity(new Intent(this, MainActivity.class));
        super.onDestroy();
    }


}
