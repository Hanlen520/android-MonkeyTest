package com.eebbk.monkeytest.util;

import com.eebbk.monkeytest.data.PostInterface;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author LuoTingWei
 *         功能 封装Retrofit方法
 * @date 2018/12/14
 */
public class RetrofitUtil {
    private static RetrofitUtil retrofitUtil;
    private final Retrofit retrofit;
    private final PostInterface postInterface;

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public PostInterface getPostInterface() {
        return postInterface;
    }

    private RetrofitUtil() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://bsq.bbkedu.com/") //设置网络请求的Url地址
                .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        postInterface = retrofit.create(PostInterface.class);
    }

    public static RetrofitUtil getInstance() {
        if (retrofitUtil == null) {
            retrofitUtil = new RetrofitUtil();
        }
        return retrofitUtil;
    }

}
