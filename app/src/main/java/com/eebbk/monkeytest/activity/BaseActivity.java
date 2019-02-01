package com.eebbk.monkeytest.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.eebbk.monkeytest.R;
import com.eebbk.monkeytest.util.StatusBarUtil;
import com.eebbk.monkeytest.util.ToastUtil;
import com.orhanobut.logger.Logger;

/**
 * @author LuoTingWei
 *         功能 带dialog的BaseActivity
 * @date 2018/12/12
 */
public abstract class BaseActivity extends Activity {
    protected TextView tvTitle;
    protected ImageView ivLeftBtn;

    protected Context context = this;
    protected ProgressDialog progressDialog;
    protected AlertDialog alertDialog;
    private Snackbar snackbar;

    protected abstract int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
            StatusBarUtil.switchTransSystemUI(this);
            tvTitle = (TextView) findViewById(R.id.tv_title_name);
            ivLeftBtn = (ImageView) findViewById(R.id.iv_title_back);

            ivLeftBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    /**
     * 功能： 显示返回按钮
     *
     * @author LuoTingWei
     * @date 2018/12/12
     */
    protected void setShowBackButton(boolean isShowBackButton) {
        if (isShowBackButton) {
            ivLeftBtn.setVisibility(View.VISIBLE);
        } else {
            ivLeftBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void setTitle(@StringRes int titleId) {
        if (tvTitle != null) {
            tvTitle.setText(titleId);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }


    @Override
    protected void onDestroy() {
        dismissDialog();
        dismissSnackbar();
        progressDialog = null;
        alertDialog = null;
        snackbar = null;
        super.onDestroy();
    }

    protected void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    private void dismissSnackbar() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    protected void showProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {

        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.show();
        }
    }

    protected class ProgressDialog extends Dialog {
        private TextView tvMessage;

        ProgressDialog(@NonNull Context context) {
            super(context, android.R.style.Theme_Dialog);
            View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog_layout, null);
            setContentView(view);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            tvMessage = (TextView) view.findViewById(R.id.tv_progress_dialog_message);
            setCanceledOnTouchOutside(false);
        }

        void setMessage(CharSequence message) {
            if (tvMessage != null) {
                tvMessage.setText(message);
            }
        }
    }

    /**
     * 功能： 显示对话框
     *
     * @author LuoTingWei
     * @date 2018/12/12
     */
    protected void showAlertDialog(String title, String message, DialogInterface.OnClickListener onPositiveButtonListener, DialogInterface.OnClickListener onNegativeButtonListener) {
        dismissDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        if (onPositiveButtonListener != null) {
            builder.setPositiveButton(android.R.string.ok, onPositiveButtonListener);
        }
        if (onNegativeButtonListener != null) {
            builder.setNegativeButton(android.R.string.cancel, onNegativeButtonListener);
        }
        alertDialog = builder.create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        alertDialog.show();
    }

    public void toastShow(Object object) {
        if (object != null) {
            ToastUtil.show(object.toString());
        } else {
            Logger.d("toastShow null!");
        }
    }

    public void snackbarShow(Object message) {
        if (message != null) {
            snackbarShow(message.toString(), null, null);
        } else {
            Logger.d("snackbarShow null!");
        }
    }

    public void snackbarShow(CharSequence message, CharSequence btnText, View.OnClickListener onClickListener) {
        dismissSnackbar();
        Logger.d(message.toString());
        if (TextUtils.isEmpty(btnText) && onClickListener != null) {
            btnText = getString(android.R.string.ok);
        }
        snackbar = ToastUtil.snackBarMake(this, message, btnText, onClickListener);
        final View view = snackbar.getView();
        view.getLayoutParams().width = WindowManager.LayoutParams.MATCH_PARENT;
        view.setAlpha(0.9f);
        final TextView tvSnackbarText = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        if (tvSnackbarText != null) {
            tvSnackbarText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.DIMEN_TEXT_23_PX));
            tvSnackbarText.setGravity(Gravity.CENTER);
        }
        snackbar.show();
    }

}
