package com.eebbk.monkeytest.receiver;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;

import com.eebbk.monkeytest.util.MonkeyUtil;
import com.orhanobut.logger.Logger;

/**
 * Created by admin on 2018/4/26.
 */

public class SettingsContentObserver extends ContentObserver {
    Context context;

    public SettingsContentObserver(Context c, Handler handler) {
        super(handler);
        context = c;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        if (!MonkeyUtil.getIsKeepSoundOff(context)) {
            return;
        }

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Logger.d("currVolume: 音量发生变化" + currentVolume);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
    }
}
