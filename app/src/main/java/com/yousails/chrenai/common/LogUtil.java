package com.yousails.chrenai.common;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by : wang
 * Time : 16/4/20
 * Desc :
 * Copyright©2016 by wang. All rights reserved.
 */
public class LogUtil {

    // 是否需要打印bug，可以在application的onCreate函数里面初始化
    public static boolean isDebug = true;

    private static final String TAG = "LogUtil";

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug) {
            Log.i(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String tag, String msg, Exception e) {
        if (isDebug) {
            if (TextUtils.isEmpty(tag)) {
                Log.e(TAG, msg, e);
            } else {
                Log.e(TAG, tag + "===========" + msg, e);
            }
        }
    }

    public static void v(String msg) {
        if (isDebug) {
            Log.v(TAG, msg);
        }
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(TAG, tag + "===========" + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(TAG, tag + "===========" + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(TAG, tag + "===========" + msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(TAG, tag + "===========" + msg);
        }
    }
}
