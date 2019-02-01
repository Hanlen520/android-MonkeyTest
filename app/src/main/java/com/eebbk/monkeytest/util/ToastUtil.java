package com.eebbk.monkeytest.util;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.eebbk.monkeytest.MonkeyTestApplication;

/**
 * @author LuoTingWei
 *         功能 toast工具类
 * @date 2018/12/12
 */
public class ToastUtil {
    private static Toast toast;
    private static final String TAG = "ToastUtil";

    public static void show(CharSequence message) {
        show(MonkeyTestApplication.getInstance(), message);
    }

    public static void show(Context context, CharSequence message) {
        Log.i(TAG, "|-----ToastUtil show" + message);
        if (MonkeyTestApplication.getInstance() != null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Log.e(TAG, "toast.show failed with message " + message);
        }
    }

    /**
     * 功能： 返回一个Snackbar
     *
     * @author LuoTingWei
     * @date 2018/12/14
     */
    public static Snackbar snackBarMake(Activity activity, CharSequence text, CharSequence btnText, View.OnClickListener onClickListener) {
        if (btnText != null && onClickListener != null) {
            return Snackbar.make(activity.getWindow().getDecorView(), text, Snackbar.LENGTH_LONG).setAction(btnText, onClickListener);
        } else {
            return Snackbar.make(activity.getWindow().getDecorView(), text, Snackbar.LENGTH_SHORT);
        }
    }
}
