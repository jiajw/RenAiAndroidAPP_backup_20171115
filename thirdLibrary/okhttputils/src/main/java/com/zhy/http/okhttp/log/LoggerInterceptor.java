package com.zhy.http.okhttp.log;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by zhy on 16/3/1.
 */
public class LoggerInterceptor implements Interceptor {
    public static final String TAG = "OkHttpUtils";
    private String tag;
    private boolean showResponse;

    public LoggerInterceptor(String tag, boolean showResponse) {
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        this.showResponse = showResponse;
        this.tag = tag;
    }

    public LoggerInterceptor(String tag) {
        this(tag, false);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        logForRequest(request);
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    private Response logForResponse(Response response) {
        try {
            //===>response log
            Log.e(tag, "========response'log=======");
            StringBuffer strb = new StringBuffer(
                    "=================response log=================\n");

            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
//            Log.e(tag, "url : " + clone.request().url());
            strb.append("url : ");
            strb.append(clone.request().url() + "\n \n");

//            Log.e(tag, "code : " + clone.code());
            strb.append("code : ");
            strb.append(clone.code() + "\n \n");

//            Log.e(tag, "protocol : " + clone.protocol());
            strb.append("protocol : ");
            strb.append(clone.protocol() + "\n \n");

            if (!TextUtils.isEmpty(clone.message())) {
//                Log.e(tag, "message : " + clone.message());
                strb.append("message : ");
                strb.append(clone.message() + "\n \n");
            }


            ResponseBody body = clone.body();
            if (body != null) {
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
//                        Log.e(tag, "responseBody's contentType : " + mediaType.toString());
                    strb.append("responseBody's contentType : ");
                    strb.append(mediaType.toString() + "\n \n");
                    if (isText(mediaType)) {
                        String resp = body.string();
//                            Log.e(tag, "responseBody's content : " + resp);
                        strb.append("responseBody's content : ");
                        strb.append(resp + "\n \n");
                        body = ResponseBody.create(mediaType, resp);
                        return response.newBuilder().body(body).build();
                    } else {
//                            Log.e(tag, "responseBody's content : " + " maybe [file part] , too large too print , ignored!");
                        strb.append("responseBody's content : " + " maybe [file part] , too large too print , ignored!" + "\n \n");
                    }
                }
            }
            strb.append("=================response'log End=================\n");
            if (showResponse) {

            }
            Log.e(tag, strb.toString());

            Log.e(tag, "========response'log=======end");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private void logForRequest(Request request) {
        try {
            String url = request.url().toString();
            Headers headers = request.headers();

//            Log.e(tag, "========request'log=======");
            StringBuffer strb = new StringBuffer(
                    "=================Request log=================\n");
            strb.append("URL:");
            strb.append(url + "\n \n");
            strb.append("method :");
            strb.append(request.method() + "\n \n");

            if (headers != null && headers.size() > 0) {
                strb.append("headers“：");
                strb.append(headers.toString() + "\n \n");
            }
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null) {
                    strb.append("requestBody's contentType : ");
                    strb.append(mediaType.toString() + "\n \n");
//                    Log.e(tag, "requestBody's contentType : " + mediaType.toString());
                    if (isText(mediaType)) {
//                        Log.e(tag, "requestBody's content : " + bodyToString(request));
                        strb.append("requestBody's content : ");
                        strb.append(bodyToString(request) + "\n \n");
                    } else {
                        Log.e(tag, "requestBody's content : " + " maybe [file part] , too large too print , ignored!");
                        strb.append("requestBody's content : ");
                        strb.append(" maybe [file part] , too large too print , ignored!" + "\n \n");
                    }
                }
            }

            strb.append("=================request'log End=================\n");

            if (showResponse) {

                Log.e(tag, strb.toString());
            }
//            Log.e(tag, "========request'log=======end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }
}
