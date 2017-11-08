package com.yousails.chrenai.net;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.yousails.chrenai.bean.HttpBaseBean;
import com.yousails.chrenai.common.GsonUtils;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.config.ModelApplication;
import com.yousails.chrenai.net.listener.LoadingRequestListener;
import com.yousails.chrenai.net.listener.ModelRequestListener;
import com.yousails.chrenai.net.listener.StringRequestListener;
import com.yousails.chrenai.thread.ThreadHelper;
import com.zhy.http.okhttp.OkHttpUtils;

import org.wordpress.android.editor.app.EditorAppProxy;
import org.wordpress.android.editor.utils.PicUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Utf8;

import static android.R.attr.data;

/**
 * Author:WangKunHui
 * Date: 2017/7/19 14:37
 * Desc:
 * E-mail:life_artist@163.com
 */
public class HttpRequestUtil<T> {

    private String TAG = "HttpRequestUtil";

    public static MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    private String url;

    private Map<String, String> header;

    private Map<String, String> params;

    private Map<String, String> body;

    private String jsonBody;

    private HttpRequestUtil(String url) {
        this.url = url;
    }

    public static HttpRequestUtil getInstance(String url) {
        return new HttpRequestUtil(url);
    }

    public HttpRequestUtil setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }

    public HttpRequestUtil addHeader(String key, String value) {
        if (header == null) {
            header = new HashMap<>();
        }
        header.put(key, value);
        return this;
    }

    public HttpRequestUtil setDefaultHeader() {
        if (header == null) {
            header = new HashMap<>();
        }
//        header.put("Authorization",
//                "BearereyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsImlzcyI6Imh0dHA6XC9cL2NocmVuYWkueW91c2FpbHMtcHJvamVjdC5jb20iLCJpYXQiOjE0OTgwMTM1NzMsImV4cCI6MTUwMzE5NzU3MywibmJmIjoxNDk4MDEzNTczLCJqdGkiOiJkZmIyNDEzOTNhNjEyZmI1YTgwOWZjZjY5NWNmN2Y3NSJ9.JRVOB_KgJNQtQIE4kQ8WNx6B4rn3MHbuGyieJ1p3mDw");

        String mToken= AppPreference.getInstance(ModelApplication.mContext).readToken();
        header.put("Authorization",mToken);

        return this;
    }

    public HttpRequestUtil setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public HttpRequestUtil addParam(String key, String value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(key, value);
        return this;
    }

    public HttpRequestUtil setRequestBody(String body) {
        this.jsonBody = body;
        return this;
    }

    public HttpRequestUtil setRequestBody(Map<String, String> body) {
        this.body = body;
        return this;
    }

    public HttpRequestUtil addRequestBody(String key, String value) {
        if (this.body == null) {
            this.body = new HashMap<>();
        }
        this.body.put(key, value);
        return this;
    }

    public void get(StringRequestListener listener) {

    }

    public <T extends HttpBaseBean> void getModel(Class<T> t, ModelRequestListener<T> listener) {
        request(getRequest(), t, listener);
    }

    @NonNull
    private Request getRequest() {
        url = appendParams(url, params);

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        Headers header = getHeader();
        if (header != null) {
            builder.headers(header);
        }
        return builder.build();
    }

    public <T extends HttpBaseBean> void post(Class<T> t, ModelRequestListener<T> listener) {
        Request request = postRequest();
        request(request, t, listener);
    }

    public void post(StringRequestListener listener) {
        Request request = postRequest();
        request(request, listener);
    }

    /**
     * 支持图片压缩上传
     */
    public <T extends HttpBaseBean> void upload(Path path, Class<T> t,
                                                final ModelRequestListener<T> listener) {

        if (TextUtils.isEmpty(path.toString())) {
            listener.onFailure("图片路径为空");
            return;
        }

        File file = new File(path.toString());
        if (file == null || !file.exists()) {
            listener.onFailure("图片不存在");
            return;
        }
        File temp = null;
        int maxSize = path.getMaxSize();
        if (maxSize > 0) {
            Bitmap bitmap = PicUtils.getInstance().getBitmap(path.toString());
            File cacheDir = EditorAppProxy.getInstance().getApplication().getCacheDir();
            String name = file.getName();
            temp = new File(cacheDir, name);
            PicUtils.getInstance().compressAndGenImage(bitmap, temp.getAbsolutePath(), maxSize);
        } else {
            temp = file;
        }

        Log.i(TAG, temp.getAbsolutePath());

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"),
                temp);

        RequestBody uploadBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("asset", "upload.jpg", requestBody)
                .build();

        Request.Builder builder = new Request.Builder();

        url = appendParams(url, params);
        builder.url(url);
        Headers header = getHeader();
        if (header != null) {
            builder.headers(header);
        }

        final File finalTemp = temp;
        RequestBody request = ProgressHelper.withProgress(uploadBody, new ProgressUIListener() {

            @Override
            public void onUIProgressStart(long totalBytes) {
                super.onUIProgressStart(totalBytes);
            }

            @Override
            public void onUIProgressChanged(long numBytes, long totalBytes, float percent,
                                            float speed) {
                if (listener instanceof LoadingRequestListener) {
                    ((LoadingRequestListener) listener).loading(numBytes, totalBytes, percent,
                            speed);
                }
            }

            @Override
            public void onUIProgressFinish() {
                super.onUIProgressFinish();
                if (finalTemp.exists()) {
                    finalTemp.delete();
                }
            }
        });

        builder.post(request);
        request(builder.build(), t, listener);
    }

    public <T extends HttpBaseBean> void upload(String path, Class<T> t,
                                                final ModelRequestListener<T> listener) {

        if (TextUtils.isEmpty(path)) {
            listener.onFailure("图片路径为空");
            return;
        }

        File file = new File(path);
        if (file == null || !file.exists()) {
            listener.onFailure("图片不存在");
            return;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"),
                file);

        RequestBody uploadBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("asset", "upload.jpg", requestBody)
                .build();

        Request.Builder builder = new Request.Builder();

        url = appendParams(url, params);
        builder.url(url);
        Headers header = getHeader();
        if (header != null) {
            builder.headers(header);
        }

        builder.post(uploadBody);
        request(builder.build(), t, listener);
    }

    private Request postRequest() {

        url = appendParams(url, params);
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        Headers header = getHeader();
        if (header != null) {
            builder.headers(header);
        }

        if (!TextUtils.isEmpty(jsonBody)) {
            builder.post(RequestBody.create(JSON_TYPE, jsonBody));
        }

        if (body != null && body.size() > 0) {
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            Set<String> keys = params.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                bodyBuilder.add(key, body.get(key));
            }

            builder.post(bodyBuilder.build());
        }
        return builder.build();
    }

    private Headers getHeader() {
        if (header != null && header.size() > 0) {
            return Headers.of(header);
        }
        return null;
    }

    private void request(Request request, final StringRequestListener listener) {

        OkHttpUtils.getInstance().getOkHttpClient().newCall(request).enqueue(
                new okhttp3.Callback() {

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

                            //String ret = new String(data.getBytes(), "unicode");
                            LogUtil.i(TAG, "onResponse  :  " + data);
                            if (TextUtils.isEmpty(data)) {
                                sync2AsyFailure("Response数据为空", listener);
                                return;
                            }

                            sync2AsySuccess(data, listener);

                        }
                    }
                });
    }

    private <T extends HttpBaseBean> void request(Request request, final Class<T> t,
                                                  final ModelRequestListener<T> listener) {

        OkHttpUtils.getInstance().getOkHttpClient().newCall(request).enqueue(
                new okhttp3.Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        sync2AsyFailure("错误类型：" + e.getMessage(), listener);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response == null) {
                            sync2AsyFailure("服务器返回数据为空", listener);
                        } else {
                            if(response.code()>300){
                                //sync2AsyFailure("{\"code:\""+response.code()+",\"message\":"+response.message()+"}", listener);
                                //Log.e("response.error",response.body().string());
                                sync2AsyFailure(response.body().string(), listener);
                                return;
                            }
                            String data = getData(response, listener);

                            //String ret = new String(data.getBytes(), "unicode");
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

    private String getData(Response response, final ModelRequestListener listener) {
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

    private String getData(Response response, final StringRequestListener listener) {
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
    private void sync2AsyFailure(final String errorMessage, final ModelRequestListener listener) {
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

    /**
     * 请求失败同步转异步处理
     */
    private void sync2AsyFailure(final String errorMessage, final StringRequestListener listener) {
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

    private <T extends HttpBaseBean> void sync2AsySuccess(final T result,
                                                          final ModelRequestListener listener) {
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

    private <T extends HttpBaseBean> void sync2AsySuccess(final String result,
                                                          final StringRequestListener listener) {
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

    private String appendParams(String url, Map<String, String> params) {

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
