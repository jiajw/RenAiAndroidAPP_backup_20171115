package com.yousails.chrenai.net;

import android.net.Uri;
import android.text.TextUtils;

import com.yousails.chrenai.bean.HttpBaseBean;
import com.yousails.chrenai.common.GsonUtils;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.net.listener.ModelRequestListener;
import com.yousails.chrenai.thread.ThreadHelper;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Author:WangKunHui
 * Date: 2017/7/13 12:06
 * Desc:
 * E-mail:life_artist@163.com
 */
public class OkHttpModelRequestUtil {

    private static String TAG = "OkHttpModelRequestUtil";

    public static <T extends HttpBaseBean> void doModelPost(String url, Map<String, String> params, Map<String, String> bodies, final Class<T> t, final ModelRequestListener<T> listener) {

        String requestPath = appendParams(url, params);

        FormBody.Builder builder = new FormBody.Builder();

        Set<String> keys = params.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.add(key, bodies.get(key));
        }

        Request request = new Request.Builder()
                .url(requestPath)
                .post(builder.build())
                .build();

        request(request, t, listener);
    }

    public static <T extends HttpBaseBean> void doModelFileUpload(String url, String path, final Class<T> t, final ModelRequestListener<T> listener) {

        File uploadFile = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile);

        Request request = new Request.Builder().url(url).post(requestBody).build();

        request(request, t, listener);
    }

    public static <T extends HttpBaseBean> void doModelFileUpload(String url, String path, Map<String, String> params, final Class<T> t, final ModelRequestListener<T> listener) {

        String requestPath = appendParams(url, params);

        File uploadFile = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile);
        Request request = new Request.Builder().url(requestPath).post(requestBody).build();

        request(request, t, listener);
    }

    /**
     * 直接返回model对象的get请求
     *
     * @param url
     * @param params
     * @param t
     * @param listener
     * @param <T>
     */
    public static <T extends HttpBaseBean> void doModelGET(String url, Map<String, String> params, final Class<T> t, ModelRequestListener<T> listener) {

        String requestPath = appendParams(url, params);

        Request request = new Request.Builder()
                .url(requestPath)
                .build();

        request(request, t, listener);

    }

    private static <T extends HttpBaseBean> void request(Request request, final Class<T> t, final ModelRequestListener<T> listener) {

        OkHttpUtils.getInstance().getOkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sync2AsyFailure("错误类型：" + e.getMessage(), listener);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response == null) {
                    sync2AsyFailure("服务器返回数据为空", listener);
                } else {
                    String data = getData(response, listener);

                    LogUtil.i(TAG, "onResponse  :  " + data);
                    if (TextUtils.isEmpty(data)) {
                        sync2AsyFailure("Response数据为空", listener);
                        return;
                    }

                    try {
                        T result = GsonUtils.fromJson(data, t);
                        if (result == null) {
                            sync2AsyFailure("Response数据解析失败", listener);
                        } else {
                            sync2AsySuccess(result, listener);
                        }
                    } catch (Exception e) {
                        sync2AsyFailure("Response数据解析失败", listener);
                    }
                }
            }
        });
    }

    private static String getData(Response response, final ModelRequestListener listener) {
        String data = null;
        try {
            data = response.body().string();
        } catch (final IOException e) {
            e.printStackTrace();
            sync2AsyFailure("从Response获取数据失败", listener);
            data = "";
        }
        return data;
    }

    /**
     * 请求失败同步转异步处理
     */
    private static void sync2AsyFailure(final String errorMessage, final ModelRequestListener listener) {
        if (listener == null || TextUtils.isEmpty(errorMessage)) {
            return;
        }
        ThreadHelper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onFailure(errorMessage);
                }
            }
        });
    }

    private static <T extends HttpBaseBean> void sync2AsySuccess(final T result, final ModelRequestListener listener) {
        if (listener == null || result == null) {
            return;
        }

        ThreadHelper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onSuccess(result);
                }
            }
        });
    }

    private static String appendParams(String url, Map<String, String> params) {

        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }
}
