package com.yousails.chrenai.thread;

import android.os.Handler;
import android.os.Looper;

/**
 * Author:WangKunHui
 * Date: 2017/7/13 11:28
 * Desc:
 * E-mail:life_artist@163.com
 */
public class ThreadUtils {

    private static final Handler uiHandler = new Handler(Looper.getMainLooper());

    public static int getThreadCount() {
        return Thread.getAllStackTraces().size();
    }

    public static long getCurThreadId() {
        return Thread.currentThread().getId();
    }

    public static String getCurThreadName() {
        return Thread.currentThread().getName();
    }

    public static void runOnUiThread(Runnable action) {
        if (action == null)
            return;

        if (ThreadUtils.getCurThreadId() == Looper.getMainLooper().getThread().getId()) {
            action.run();
        } else {
            uiHandler.post(action);
        }
    }
}
