package com.eebbk.monkeytest.asynstask;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.eebbk.bfc.common.devices.DeviceUtils;
import com.eebbk.bfc.sequence.SequenceTools;
import com.eebbk.monkeytest.data.MonkeyEventInfo;
import com.eebbk.monkeytest.util.MonkeyCommandUtils;
import com.eebbk.monkeytest.util.MonkeyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018/5/25.
 */
@Deprecated
public class UploadRunParamsAsynctask extends AsyncTask<Void, Void, Void> {

    private Context mContext;

    public UploadRunParamsAsynctask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        String startTime = MonkeyUtil.getStartMonkeyTime( );
        String stopTime = MonkeyUtil.getLocalMonkeyTime();

        if (TextUtils.equals(startTime, "2018-01-01")) {
            return null;
        }

        String packageInfo = MonkeyCommandUtils.getPkgString(MonkeyUtil.getHistoryString("pkgString", ""));
        String[] pkgList = packageInfo.replace("-p,","").split("[\\s+]");

        List<String> packageList = new ArrayList<>();
        for (String item : pkgList) {
            packageList.add(item);
        }
        // 保存monkey 字符串 用来定时跑
//        MonkeyUtil.setMonkeyStr(getApplicationContext(), monkeyStr);

        MonkeyEventInfo eventInfo= new MonkeyEventInfo(DeviceUtils.getBbkSn(),
                DeviceUtils.getModel(),
                DeviceUtils.getSystemVersion(),
                startTime,
                stopTime,
                packageList
                );

        String url = "http://bsq.bbkedu.com/bsq/custom/monkey/monitor";
        Map params = new HashMap<String, String>();
        params.put("operate", "uploadMonkeyEvent");
        params.put("info", SequenceTools.serialize(eventInfo));
//        BfcHttp.post(mContext, url, params, new IBfcHttpCallBack() {
//            @Override
//            public void onResponse(Object o) {
//                LogUtil.e("uploadMonkeyEvent-------->" + o.toString());
//            }
//        }, new IBfcErrorListener() {
//            @Override
//            public void onError(BfcHttpError bfcHttpError) {
//                LogUtil.e("uploadMonkeyEvent--------> error:" + bfcHttpError.getErrorCode());
//
//            }
//        });

//        MonkeyUtil.setStartMonkeyTime(mContext,"2018-01-01");
        return null;
    }


}
