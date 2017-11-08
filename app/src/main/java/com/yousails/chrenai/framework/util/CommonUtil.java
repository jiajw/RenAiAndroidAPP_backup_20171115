package com.yousails.chrenai.framework.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * User: jiajinwu
 * Date: 2017-11-01
 * Time: 18:56
 * 修改备注：
 * version:
 */


public class CommonUtil {

    /**
     * 获取版本号versionName
     *
     * @param context
     * @return versionName
     */
    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        String versionName = null;
        PackageInfo packInfo;
        try {
            packInfo = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = packInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取版本号versionCode
     *
     * @param context
     * @return versionCode
     */
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        int versionCode = 0;
        PackageInfo packInfo;
        try {
            packInfo = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = packInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
}
