package com.eebbk.monkeytest.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.eebbk.monkeytest.R;
import com.eebbk.monkeytest.util.MonkeyUtil;

/**
 * 作者：lj
 * 实现的主要功能：
 * 创建日期：17-7-8
 * 修改信息：
 */
public class MonkeyParamsConfigActivity extends BaseActivity {

    private EditText mTimesEt;
    private EditText mDelayEt;
    private EditText mMotionPctEt;
    private EditText mTouchPctEt;
    private EditText mSystemPctEt;
    private EditText mLaunchPctEt;
    private Switch mStopSwitch;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_config;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initView() {
        mTimesEt = (EditText) findViewById(R.id.times_et);
        mDelayEt = (EditText) findViewById(R.id.delay_et);
        mMotionPctEt = (EditText) findViewById(R.id.motion_pct_et);
        mTouchPctEt = (EditText) findViewById(R.id.touch_pct_et);
        mSystemPctEt = (EditText) findViewById(R.id.system_pct_et);
        mLaunchPctEt = (EditText) findViewById(R.id.launch_pct_et);
        mStopSwitch = (Switch) findViewById(R.id.stop_switch);

        setTitle("修改运行时配置");
        setShowBackButton(true);

        TextView iv_title_sure = (TextView) findViewById(R.id.iv_title_sure);
        iv_title_sure.setVisibility(View.VISIBLE);
        iv_title_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveHistory();
                finish();
            }
        });

        setShowBackButton(true);

        mDelayEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Integer.parseInt(mDelayEt.getText().toString()) < 100) {
                    showAlertDialog(getString(R.string.text_hint), "延迟间隔小于100ms，可能导致无法终止monkey"
                            , (dialogInterface, i) -> mDelayEt.setText(100 + ""), null);
                }
            }
        });
    }

    private void initData() {
        loadHistory();
    }


    private void loadHistory() {
        String timeString = MonkeyUtil.getHistoryString("timeString", "3000000");
        mTimesEt.setText(timeString);
        String delayString = MonkeyUtil.getHistoryString("delayString", "300");
        mDelayEt.setText(delayString);
        String motionString = MonkeyUtil.getHistoryString("motionString", "15");
        mMotionPctEt.setText(motionString);
        String touchString = MonkeyUtil.getHistoryString("touchString", "15");
        mTouchPctEt.setText(touchString);
        String systemString = MonkeyUtil.getHistoryString("systemString", "15");
        mSystemPctEt.setText(systemString);
        String launchString = MonkeyUtil.getHistoryString("launchString", "15");
        mLaunchPctEt.setText(launchString);
        mStopSwitch.setChecked(MonkeyUtil.getHistoryStop());
    }

    private void saveHistory() {
        MonkeyUtil.setHistoryString("timeString", mTimesEt.getText().toString());
        MonkeyUtil.setHistoryString("delayString", mDelayEt.getText().toString());
        MonkeyUtil.setHistoryString("motionString", mMotionPctEt.getText().toString());
        MonkeyUtil.setHistoryString("touchString", mTouchPctEt.getText().toString());
        MonkeyUtil.setHistoryString("systemString", mSystemPctEt.getText().toString());
        MonkeyUtil.setHistoryString("launchString", mLaunchPctEt.getText().toString());
        MonkeyUtil.setHistoryStop(mStopSwitch.isChecked());
    }

}
