package com.yousails.chrenai.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2017/6/19.
 */

public class NetUtil {
    /**
     * 检测网络是否可用;
     */
    public static boolean detectAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否为WIFI；
     */
    public static boolean detectWifi(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null
                        && info.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                }
            }
        }
        return false;
    }

    public static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return null;
        }
        final NetworkInfo activeInfo = connectivity.getActiveNetworkInfo();
        return activeInfo;
    }
}
