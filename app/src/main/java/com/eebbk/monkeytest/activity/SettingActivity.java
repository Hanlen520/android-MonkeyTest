package com.eebbk.monkeytest.activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.eebbk.common.utils.datatimewheel.DateChooseWheelViewDialog;
import com.eebbk.common.utils.datatimewheel.util.ModeConst;
import com.eebbk.monkeytest.R;
import com.eebbk.monkeytest.util.MonkeyUtil;

import java.util.Date;

public class SettingActivity extends BaseActivity {

    private DateChooseWheelViewDialog mDateChooseDialog;

    private TextView mDateTv;

    private int mHour = 0;
    private int mMinute = 0;
    private Switch alarmSwitch;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("设置");

        mDateTv = (TextView) findViewById(R.id.date_tv);
        mDateTv.setOnClickListener(v -> showDateChooseDialog());

        mHour = MonkeyUtil.getHour();
        mMinute = MonkeyUtil.getMinute();
        String dateStr = String.format("%02d:%02d", mHour, mMinute);
        mDateTv.setText(dateStr);


        Switch keepSwitch = (Switch) findViewById(R.id.keey_wifi_switch);
        keepSwitch.setChecked(MonkeyUtil.getIsKeepWifiOn(context));
        keepSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MonkeyUtil.setIsKeepWifiOn(isChecked);
        });

        alarmSwitch = (Switch) findViewById(R.id.alarm_switch);
        alarmSwitch.setChecked(MonkeyUtil.getIsAlarmOn(context));
        alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MonkeyUtil.setIsAlarmOn(isChecked);
            setAlarm(isChecked);
        });

        Switch soundSwitch = (Switch) findViewById(R.id.sound_switch);
        soundSwitch.setChecked(MonkeyUtil.getIsKeepSoundOff(context));
        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MonkeyUtil.setIsKeepSoundOff(isChecked);
            if (isChecked) {
                getDoNotDisturb();
            }
        });

        TextView lastExecTime = (TextView) (findViewById(R.id.exec_last_time_txt));
        lastExecTime.setText(MonkeyUtil.getStartMonkeyTime());
        setShowBackButton(true);

        initTimeDialog();

    }

    private void initTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setPositiveButton("确定", (dialog, which) -> {
            String dateStr = String.format("%02d:%02d", mHour, mMinute);
            mDateTv.setText(dateStr);
            MonkeyUtil.setHour(mHour);
            MonkeyUtil.setMinute(mMinute);
            if (alarmSwitch.isChecked()) {
                setAlarm(true);
                snackbarShow("定时设置成功");
            }
        });
        AlertDialog dialog = builder.create();

        mDateChooseDialog = new DateChooseWheelViewDialog(this, new DateChooseWheelViewDialog.DateChooseInterface() {
            @Override
            public void getDateTime(Date date, View view) {
                Log.d("MonkeyTest", "getDateTime: " + date);
                mHour = date.getHours();
                mMinute = date.getMinutes();
            }

            @Override
            public void getSetContenView(View view) {

            }
        }, ModeConst.HOUR_MINUTE, dialog, builder);


    }

    private void setAlarm(boolean isAlarm) {
        if (isAlarm) {
            MonkeyUtil.cancelAlarm(getApplicationContext());
            MonkeyUtil.setAlarm(getApplicationContext(), MonkeyUtil.getHour(), MonkeyUtil.getMinute());
        } else {
            MonkeyUtil.cancelAlarm(getApplicationContext());
        }
    }


    private void showDateChooseDialog() {
        mDateChooseDialog.showDateChooseDialog();
    }

    //获取Do not disturb权限,才可进行音量操作
    private void getDoNotDisturb() {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }
    }
}
