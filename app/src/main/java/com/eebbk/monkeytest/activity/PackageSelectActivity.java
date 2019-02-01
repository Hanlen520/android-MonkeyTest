package com.eebbk.monkeytest.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.eebbk.monkeytest.R;
import com.eebbk.monkeytest.data.AppInfo;
import com.eebbk.monkeytest.util.AppInfoComparator;
import com.eebbk.monkeytest.util.InstalledAppList;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PackageSelectActivity extends BaseActivity {

    public ArrayList<String> mPkgInUseList = new ArrayList<>();
    public static List<AppInfo> mAppInfoList = new ArrayList<>();
    private List<AppInfo> appInfoSearchList = new ArrayList<>();
    //    private ArrayList<AppInfo> mNonelanchAppInfoList;
    private Map<String, Drawable> mMapAppInfo = new HashMap<>();
    private PackagesAdapter mAppListAdapter;
    private TextView mIv_title_sure;
    private TextView tvSelectNone;
    private ListView mPackagesLv;
    private FloatingActionButton fbtnToSearch;
    private Button btnSearch;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout llSearch;
    private EditText edtSearch;

    private static int lastCode = 0;
    private int mResultCode = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_package_select;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        LogUtil.e("--------------------------> onDestroy");
//    }

    private void initView() {

        setTitle("添加应用");
        setShowBackButton(true);
        tvSelectNone = (TextView) findViewById(R.id.tv_select_none);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_ps);
        mPackagesLv = (ListView) findViewById(R.id.package_list_view);
        mIv_title_sure = (TextView) findViewById(R.id.iv_title_sure);
        llSearch = findViewById(R.id.ll_search_layout);
        btnSearch = findViewById(R.id.btn_search);
        fbtnToSearch = findViewById(R.id.floating_btn_search);
        edtSearch = findViewById(R.id.edt_search);

        renewPackageList(mResultCode);//开始加载

        if (mResultCode == MainActivity.LAUNCHER_APP_RESULTCODE) {
            findViewById(R.id.app_info_prompt).setVisibility(View.VISIBLE);
        }
        mAppListAdapter = new PackagesAdapter(mAppInfoList);
        mPackagesLv.setAdapter(mAppListAdapter);

        mIv_title_sure.setVisibility(View.VISIBLE);
        switchSearchView();

        mIv_title_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("pkgList", mPkgInUseList);
                setResult(mResultCode, intent);
                finish();
            }
        });

        refreshLayout.setDistanceToTriggerSync(getResources().getDimensionPixelOffset(R.dimen.DIMEN_HEIGHT_150_PX));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAppInfoList.clear();
                renewPackageList(mResultCode);
            }
        });

        setPackageStateChanged();
        setEnsureTextEnable(false);

        edtSearch.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                onBtnSearchClick(null);
                return true;
            }
            return false;
        });
    }

    private void initData() {
        mResultCode = getIntent().getIntExtra("resultCode", MainActivity.LAUNCHER_APP_RESULTCODE);
        mPkgInUseList = getIntent().getStringArrayListExtra("pkgList");
        if (lastCode != mResultCode) {
            mAppInfoList.clear();
        }
        lastCode = mResultCode;
    }

    void switchSearchView() {
        if (llSearch.getVisibility() == View.VISIBLE) {
            llSearch.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        } else {
            llSearch.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 功能： rxJava加载列表数据并显示
     *
     * @author LuoTingWei
     * @date 2018/12/13
     */
    private void renewPackageList(int type) {
        showProgressDialog();
        progressDialog.setMessage("正在加载APP列表");
        Observable.create((ObservableOnSubscribe<Integer>) observableEmitter -> {
            if (mAppInfoList.size() == 0) {
                if (type == MainActivity.LAUNCHER_APP_RESULTCODE) {
                    mAppInfoList = InstalledAppList.getPackages(context, true);
                } else {
                    mAppInfoList = InstalledAppList.getPackages(context, false);
                }
                Collections.sort(mAppInfoList, new AppInfoComparator());
            }
            observableEmitter.onNext(1);

            final List<PackageInfo> packages = getApplicationContext().getPackageManager().getInstalledPackages(0);

            for (int idx = 0; idx < packages.size(); idx++) {
                mMapAppInfo.put(packages.get(idx).packageName, packages.get(idx).applicationInfo.loadIcon(getApplicationContext().getPackageManager()));
            }

            observableEmitter.onNext(2);
            observableEmitter.onComplete();
        }).subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(which -> {
            if (which == 1) {
                progressDialog.setMessage("正在加载图标");
            } else if (which == 2) {
                dismissDialog();
                refreshLayout.setRefreshing(false);
                renewInstalledPackageAppInfo();
            }

        }, throwable -> Logger.e(throwable.getMessage()));
    }


    public void renewInstalledPackageAppInfo() {
        mAppListAdapter.setData(mAppInfoList);
        setPackageStateChanged();
    }

    /**
     * 设置确认键是否可以点击
     *
     * @param enable
     */
    private void setEnsureTextEnable(boolean enable) {

        if (!enable) {
            mIv_title_sure.setTextColor(ContextCompat.getColor(this, R.color.disable_pkg_color));
            mIv_title_sure.setClickable(false);
        } else {
            mIv_title_sure.setTextColor(ContextCompat.getColor(this, R.color.date_ui_white));
            mIv_title_sure.setClickable(true);
        }

    }

    private void setPackageStateChanged() {
        setTitle("添加应用(" + mPkgInUseList.size() + "/" + mAppInfoList.size() + ")");
        if (mPkgInUseList != null && mPkgInUseList.size() > 0) {
            tvSelectNone.setVisibility(View.VISIBLE);
        } else {
            tvSelectNone.setVisibility(View.GONE);
        }
    }

    private boolean isPackageInUse(String packageName) {
        for (String pkg : mPkgInUseList) {
            if (pkg.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(-1);
    }

    /**
     * 功能：tvSelectNone onClick
     *
     * @author LuoTingWei
     * @date 2018/12/12
     */
    public void tvSelectNone(View view) {
        if (mPkgInUseList != null) {
            mPkgInUseList.clear();
            if (mAppListAdapter != null) {
                mAppListAdapter.notifyDataSetChanged();
            }
            setEnsureTextEnable(false);
        }
        setPackageStateChanged();
    }

    //搜索键
    public void onBtnSearchClick(View view) {
        switchSearchView();
        String keyword = edtSearch.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            appInfoSearchList = mAppInfoList.stream()
                    .filter(appInfo -> appInfo.getAppName().contains(keyword) || appInfo.getPackageName().contains(keyword))
                    .sorted(new AppInfoComparator())
                    .collect(Collectors.toList());
            mAppListAdapter.setData(appInfoSearchList);
            if (appInfoSearchList.size() == 0) {
                snackbarShow("未找到");
            }
        } else {
            snackbarShow("仅支持Android7.0+");
        }

    }

    public void onFloatBtnToSearch(View view) {
        switchSearchView();
    }

    public void onIvBtnClearText(View view) {
        edtSearch.setText("");
        mAppListAdapter.setData(mAppInfoList);
    }

    private class PackagesAdapter extends BaseAdapter {


        private List<AppInfo> mAppInfoList = new ArrayList<>();
        private LayoutInflater layoutInflater = LayoutInflater.from(PackageSelectActivity.this);

        public PackagesAdapter(List<AppInfo> mAppInfoList) {
            if (mAppInfoList != null) {
                this.mAppInfoList = mAppInfoList;
            }
        }

        public void setData(List<AppInfo> dataList) {
            if (dataList != null) {
                this.mAppInfoList = dataList;
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return mAppInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if (null == convertView) {
                view = layoutInflater.inflate(R.layout.package_list_item, null, false);
            } else {
                view = convertView;
            }

            AppInfo appInfo = mAppInfoList.get(position);

            final CheckBox mCheckBox = (CheckBox) view.findViewById(R.id.package_item_cb);
            String appName = appInfo.getAppName();
            final String pkgName = appInfo.getPackageName();

            TextView appNameTv = (TextView) view.findViewById(R.id.list_item_app_name);
            appNameTv.setText(appName);
            TextView packageNameTv = (TextView) view.findViewById(R.id.list_item_package_name);
            packageNameTv.setText(pkgName);

            ImageView appIcon = (ImageView) view.findViewById(R.id.list_item_icon);
//            appIcon.setBackground(appInfo.getAppIcon());
            appIcon.setBackground(mMapAppInfo.get(appInfo.getPackageName()));

            TextView appVersionName = (TextView) view.findViewById(R.id.list_item_versionName);
            String versionInfo = getResources().getString(R.string.app_version_name) + ":" + appInfo.getVersionName() + ", " +
                    getResources().getString(R.string.app_version_code) + appInfo.getVersionCode();
            appVersionName.setText(versionInfo);

            mCheckBox.setOnCheckedChangeListener(null);
            mCheckBox.setChecked(isPackageInUse(pkgName));

            LinearLayout wrapperLayout = (LinearLayout) view.findViewById(R.id.package_item_wrapper_layout);
            wrapperLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckBox.setChecked(!mCheckBox.isChecked());
                }
            });

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mPkgInUseList.add(pkgName);
//                        tvSelectNone.setVisibility(View.VISIBLE);
                    } else {
                        for (String pkgItem : mPkgInUseList) {
                            if (pkgItem.equals(pkgName)) {
                                mPkgInUseList.remove(pkgItem);
                                break;
                            }
                        }
                    }
                    setPackageStateChanged();
                    setEnsureTextEnable(true);
                }
            });

            return view;
        }
    }
}
