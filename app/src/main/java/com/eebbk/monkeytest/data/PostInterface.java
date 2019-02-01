package com.eebbk.monkeytest.data;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author LuoTingWei
 *         功能 retrofit的post接口
 * @date 2018/12/14
 */
public interface PostInterface {
    @POST("bsq/custom/monkey/monitor")
    @FormUrlEncoded
    Observable<ResponseBody> postMonkeyLog(@FieldMap Map<String, String> map);
}
