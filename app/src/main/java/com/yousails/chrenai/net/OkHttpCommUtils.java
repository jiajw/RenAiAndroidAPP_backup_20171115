package com.yousails.chrenai.net;


import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.config.ModelApplication;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/6/20.
 */

public class OkHttpCommUtils {
    /*String token;
    public void OkHttpCommUtils(){
        token=AppPreference.getInstance(ModelApplication.mContext).readToken();
    }*/

    public static void doPost(String url, Map<String, String> params, Callback callback) {
        String token=AppPreference.getInstance(ModelApplication.mContext).readToken();
        if(StringUtil.isNotNull(token)){
            OkHttpUtils.post()
                    .url(url)
                    .addHeader("Authorization",token)
                    .params(params)
                    .build()
                    .execute(callback);
        }else{
            OkHttpUtils.post()
                    .url(url)
                    .params(params)
                    .build()
                    .execute(callback);
        }

    }

    public static void doGET(String url, Map<String, String> params, Callback callback) {
        String token=AppPreference.getInstance(ModelApplication.mContext).readToken();
        if(StringUtil.isNotNull(token)){
            OkHttpUtils.get()
                    .url(url)
                    .addHeader("Authorization",token)
                    .params(params)
                    .build()
                    .execute(callback);
        }else{
            OkHttpUtils.get()
                    .url(url)
                    .params(params)
                    .build()
                    .execute(callback);
        }

    }


    public static void doPut(String url, RequestBody requestBody ,Callback callback) {
        String token=AppPreference.getInstance(ModelApplication.mContext).readToken();
        OkHttpUtils.put()
                .url(url)
                .addHeader("Authorization",token)
                .requestBody(requestBody)
                .build()
                .execute(callback);
    }

    public static void delete(String url, RequestBody requestBody ,Callback callback) {
        String token=AppPreference.getInstance(ModelApplication.mContext).readToken();
        OkHttpUtils.delete()
                .url(url)
                .addHeader("Authorization",token)
                .requestBody(requestBody)
                .build()
                .execute(callback);
    }


}
