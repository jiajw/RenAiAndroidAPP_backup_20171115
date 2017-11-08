package com.yousails.chrenai.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.yousails.chrenai.config.ApiConstants;

/**
 * Log工具，类似android.util.Log。
 * tag自动产生，格式: customTagPrefix:className.methodName(L:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(L:lineNumber)。
 * Class:LogUtils
 * User: Administrator
 * Date: 2017-10-19
 * Time: 10:20
 * 修改人：jiajinwu
 * 修改时间：2017/10/19 10:20
 * 修改备注：
 * version:
 */


public class LogUtils {

    public static String customTagPrefix = "";
    public static boolean LOG_DEBUG = ApiConstants.IS_TEST;
    public static boolean allowD = LOG_DEBUG;
    public static boolean allowE = LOG_DEBUG;
    public static boolean allowI = LOG_DEBUG;
    public static boolean allowV = LOG_DEBUG;
    public static boolean allowW = LOG_DEBUG;
    public static CustomLogger customLogger;

    private LogUtils() {
    }


    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    @SuppressLint("DefaultLocale")
    public static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    public static void d(String content) {
        if (!allowD) return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.d(tag, content);
        } else {
            Log.d(tag, content);
        }
    }


    public static void e(String content) {
        if (!allowE) return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.e(tag, content);
        } else {
            Log.e(tag, content);
        }
    }


    public static void i(String content) {
        if (!allowI) return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.i(tag, content);
        } else {
            Log.i(tag, content);
        }
    }


    public static void v(String content) {
        if (!allowV) return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.v(tag, content);
        } else {
            Log.v(tag, content);
        }
    }


    public static void w(String content) {
        if (!allowW) return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.w(tag, content);
        } else {
            Log.w(tag, content);
        }
    }

    /**
     * 输出长度很长的log
     *
     * @param content
     */
    public static void longE(String content) {
        if (!allowE) return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        int p = 2000;
        long length = content.length();
        if (length < p || length == p)
            Log.e(tag, content);
        else {
            while (content.length() > p) {
                String logContent = content.substring(0, p);
                content = content.replace(logContent, "");
                Log.e(tag, logContent);
            }
            Log.e(tag, content);
        }

    }

    public interface CustomLogger {
        void d(String tag, String content);

        void d(String tag, String content, Throwable tr);

        void e(String tag, String content);

        void e(String tag, String content, Throwable tr);

        void i(String tag, String content);

        void i(String tag, String content, Throwable tr);

        void v(String tag, String content);

        void v(String tag, String content, Throwable tr);

        void w(String tag, String content);

        void w(String tag, String content, Throwable tr);

        void w(String tag, Throwable tr);

        void wtf(String tag, String content);

        void wtf(String tag, String content, Throwable tr);

        void wtf(String tag, Throwable tr);
    }

}
