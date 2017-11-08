package com.yousails.chrenai.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * Created by WuXiaolong
 * on 2016/3/31.
 */
@SuppressWarnings("unused")
public class SharedPreferencesUtil {

    /**
     * 透传收到push消息后，发送一个notification id的key
     */
    public static final String PUSH_NOTIFICATION_ID_KEY = "push_notification_id_key";
    /**
     * 上传通讯录的key
     */
    public static final String UPLOAD_CONTACT_KEY = "upload_contact_key";

    /**
     * 主界面是否显示用户引导图
     */
    public static final String SHOW_MAIN_GUIDE_KEY = "show_main_guide_key";

    /**
     * 设备ID唯一标识
     */
    public static final String DEVICE_ID_KEY = "device_id_key";

    /**
     * 用户是否登录过，为了兼容以前的老的deviceid方式
     */
    public static final String USER_HAS_LOGINED = "user_has_logined";

    /**
     * 点击摇tab的时候显示的开机大图
     */
    public static final String SHOW_MAIN_YAO_TAB_INFO = "show_main_yao_tab_info";


    /**
     * 当天是否已经检测过通知权限，保证一天只执行一次
     */
    public static final String CHECK_NOTIFACATION_PERMISSION_PERDAY = "check_notifacation_permission_perday";

    /**
     * 手机是否支持打开通知权限的界面
     */
    public static final String SUPPORT_CHECK_NOTIFACATION_PERMISSION = "support_check_notifacation_permission";

    public static String getString(Context context, String key,
                                   final String defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getString(key, defaultValue);
    }

    public static void setString(Context context, final String key,
                                 final String value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putString(key, value).apply();
    }

    public static void removeString(Context context, final String key) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().remove(key).commit();
    }

    public static void clear(Context context){

        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().clear();
    }

    public static boolean getBoolean(Context context, final String key,
                                     final boolean defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getBoolean(key, defaultValue);
    }

    public static boolean hasKey(Context context, final String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(
                key);
    }

    public static void setBoolean(Context context, final String key,
                                  final boolean value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putBoolean(key, value).apply();
    }

    public static void setInt(Context context, final String key,
                              final int value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putInt(key, value).apply();
    }

    public static int getInt(Context context, final String key,
                             final int defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getInt(key, defaultValue);
    }

    public static void setFloat(Context context, final String key,
                                final float value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putFloat(key, value).apply();
    }

    public static float getFloat(Context context, final String key,
                                 final float defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getFloat(key, defaultValue);
    }

    public static void setLong(Context context, final String key,
                               final long value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putLong(key, value).apply();
    }

    public static long getLong(Context context, final String key,
                               final long defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getLong(key, defaultValue);
    }

    /**
     * 根据key和预期的value类型获取value的值
     *
     * @param key
     * @param clazz
     * @return
     */
    public static <T> T getValue(Context context, String key, Class<T> clazz) {
        if (context == null) {
            throw new RuntimeException("请先调用带有context，name参数的构造！");
        }
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return getValue(key, clazz, sp);
    }

    /**
     * 针对复杂类型存储<对象>
     *
     * @param key
     * @param object
     */
    public static void setObject(Context context, String key, Object object) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        //创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //创建字节对象输出流
        ObjectOutputStream out = null;
        try {
            //然后通过将字对象进行64转码，写入key值为key的sp中
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, objectVal);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObject(Context context, String key, Class<T> clazz) {
        T t = null;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            //一样通过读取字节流，创建字节流输入流，写入对象并作强制转换
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                t = (T) ois.readObject();

                bais.close();
                ois.close();
            } catch (Exception e) {
                e.printStackTrace();
                t = null;
            }
        }
        return t;
    }

    /**
     * 对于外部不可见的过渡方法
     *
     * @param key
     * @param clazz
     * @param sp
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> T getValue(String key, Class<T> clazz, SharedPreferences sp) {
        T t;
        try {

            t = clazz.newInstance();

            if (t instanceof Integer) {
                return (T) Integer.valueOf(sp.getInt(key, 0));
            } else if (t instanceof String) {
                return (T) sp.getString(key, "");
            } else if (t instanceof Boolean) {
                return (T) Boolean.valueOf(sp.getBoolean(key, false));
            } else if (t instanceof Long) {
                return (T) Long.valueOf(sp.getLong(key, 0L));
            } else if (t instanceof Float) {
                return (T) Float.valueOf(sp.getFloat(key, 0L));
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
            LogUtil.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LogUtil.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
        }
        LogUtil.e("system", "无法找到" + key + "对应的值");
        return null;
    }

    /**
     * 是否显示首页引导图
     */
    public static boolean isShowMainGuide(Context context, String tvmid) {
        try {
            if (context == null || TextUtils.isEmpty(tvmid)) {
                return false;
            }
            int showFlag = getInt(context, SHOW_MAIN_GUIDE_KEY + "_" + tvmid, 0);
            if (showFlag == 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 设置显示首页引导图
     *
     * @param flag 0 显示，1 不显示
     */
    public static void setShowMainGuideFlag(Context context, int flag, String tvmid) {
        try {
            if (flag != 0 && flag != 1 || context == null || TextUtils.isEmpty(tvmid)) {
                return;
            }

            setInt(context, SHOW_MAIN_GUIDE_KEY + "_" + tvmid, flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置设备ID
     */
    public static void setDeviceId(Context context, String deviceId) {
        try {
            setString(context, DEVICE_ID_KEY, deviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备ID
     */
    public static String getDeviceId(Context context) {
        try {
            return getString(context, DEVICE_ID_KEY, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置用户是否登录过，只要登录过就永远是true，不再修改，即时注销也不需要重置为false
     */
    public static void setUserHasLogined(Context context, boolean userHasLogined) {
        try {
            setBoolean(context, USER_HAS_LOGINED, userHasLogined);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户是否登录过，只要登录过就永远是true，不再修改，即时注销也不需要重置为false
     */
    public static boolean getUserHasLogined(Context context) {
        try {
            return getBoolean(context, USER_HAS_LOGINED, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置用户是否登录过，只要登录过就永远是true，不再修改，即时注销也不需要重置为false
     */
    public static void setShowMainYaoTabInfo(Context context, String pushMsg, String tvmid) {
        try {
            setString(context, SHOW_MAIN_YAO_TAB_INFO + "_" + tvmid, pushMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户是否登录过，只要登录过就永远是true，不再修改，即时注销也不需要重置为false
     */
    public static String getShowMainYaoTabInfo(Context context, String tvmid) {
        try {
            return getString(context, SHOW_MAIN_YAO_TAB_INFO + "_" + tvmid, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 清除数据
     */
    public static void removeShowMainYaoTabInfo(Context context, String tvmid) {
        try {
            removeString(context, SHOW_MAIN_YAO_TAB_INFO + "_" + tvmid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置是否检测过通知的权限
     */
//    public static void setCheckNotifacationPerDay(Context context) {
//        try {
////            setString(context, CHECK_NOTIFACATION_PERMISSION_PERDAY, DateUtil.DateToString(new Date(), DateStyle.YYYY_MM_DD));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 当天是否检测过通知的权限
     */
    public static boolean hasCheckNotifacationPerDay(Context context) {
        try {
            return TextUtils.isEmpty(getString(context, CHECK_NOTIFACATION_PERMISSION_PERDAY, null)) ? false : true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置手机是否有打开通知的权限界面
     */
    public static void setSupportCheckNotifacation(Context context, boolean isSupport) {
        try {
            setBoolean(context, SUPPORT_CHECK_NOTIFACATION_PERMISSION, isSupport);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 手机是否有打开通知的权限界面
     */
    public static boolean isSupportCheckNotifacation(Context context) {
        try {
            return getBoolean(context, SUPPORT_CHECK_NOTIFACATION_PERMISSION, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
