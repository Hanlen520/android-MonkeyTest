package com.eebbk.monkeytest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.eebbk.monkeytest.R;
import com.eebbk.monkeytest.services.StartMonkeyService;
import com.eebbk.monkeytest.util.MonkeyUtil;

/**
 * @author LuoTingWei
 *         功能 定时跑提示界面
 * @date 2018/12/15
 */
public class MonkeyActivity extends AppCompatActivity {

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MonkeyUtil.wakeUpAndUnlock(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_monkey);
        MonkeyUtil.setMonkeyState(false);

        handler = new Handler();
        handler.postDelayed(() -> {
            startService(new Intent(MonkeyActivity.this, StartMonkeyService.class));//定时启动
            finish();
        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onCancel(View view) {
        handler.removeCallbacksAndMessages(0);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(0);
        super.onDestroy();
    }
}
