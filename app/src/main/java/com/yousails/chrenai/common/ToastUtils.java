package com.yousails.chrenai.common;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


/**
 * Author:WangKunHui
 * Date: 2017/7/12 14:14
 * Desc: Toast工具类
 * E-mail:life_artist@163.com
 */
public class ToastUtils {

    private static String TAG = "ToastUtils";

    public static void showShort(Context context, String content) {
        show(context, content, Toast.LENGTH_SHORT);
    }

    public static void showLong(Context context, String content) {
        show(context, content, Toast.LENGTH_LONG);
    }

    private static void show(Context context, String content, int length) {
        if (context == null || TextUtils.isEmpty(content)) {
            return;
        }
        try {
            Toast.makeText(context, content, length).show();
        } catch (Exception e) {
            LogUtil.e(TAG, "ToastUtils Exception" + e.getMessage());
        }
    }
}