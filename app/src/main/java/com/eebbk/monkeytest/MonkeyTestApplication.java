package com.eebbk.monkeytest;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * Created by admin on 2018/4/26.
 */

public class MonkeyTestApplication extends Application {

    private static MonkeyTestApplication instance;//= new MonkeyTestApplication();
    private boolean mStartState;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        mStartState = false;

        Logger.addLogAdapter(new AndroidLogAdapter(PrettyFormatStrategy.newBuilder().tag("MonkeyTest").build()) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    //这里提供了一个供外部访问本class的静态方法，可以直接访问
    public static MonkeyTestApplication getInstance() {
        if (null == instance) {
            instance = new MonkeyTestApplication();
            instance.setStartState(false);
        }
        return instance;
    }

    @Deprecated //因为进程可能会被杀掉重启，所以在这里获取意义不大
    public boolean isStartState() {
        return mStartState;
    }

    @Deprecated
    public void setStartState(boolean startState) {
        Logger.d("Set run state :" + startState);
        mStartState = startState;
    }
}
