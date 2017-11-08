package com.yousails.chrenai.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.yousails.chrenai.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 针对6.0以上系统动态获取权限工具类
 * Class:PermissionUtils
 * User: Administrator
 * Date: 2017-10-18
 * Time: 16:28
 * 修改人：jiajinwu
 * 修改时间：2017/10/18 16:28
 * 修改备注：
 * version:
 */


public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getSimpleName();

    public static final int CODE_RECORD_AUDIO = 0;
    public static final int CODE_GET_ACCOUNTS = 1;
    public static final int CODE_READ_PHONE_STATE = 2;
    public static final int CODE_CALL_PHONE = 3;
    public static final int CODE_CAMERA = 4;
    public static final int CODE_ACCESS_FINE_LOCATION = 5;
    public static final int CODE_ACCESS_COARSE_LOCATION = 6;
    public static final int CODE_READ_EXTERNAL_STORAGE = 7;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 8;
    public static final int CODE_MULTI_PERMISSION = 100;

    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;


    private static final String[] requestPermissions = {
            PERMISSION_RECORD_AUDIO,
            PERMISSION_GET_ACCOUNTS,
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_CALL_PHONE,
            PERMISSION_CAMERA,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_ACCESS_COARSE_LOCATION,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE
    };


    //权限授权接口
    public interface PermissionGrant {
        void onPermissionGranted(int requestCode);
    }

    /**
     * 获取权限
     *
     * @param activity        需要获取权限的上下文对像
     * @param requestCode     获取权限code值
     * @param permissionGrant 权限授予回调
     */
    public static void requestPermission(final Activity activity,
                                         final int requestCode,
                                         PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }

        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            LogUtils.e("requestPermission illegal requestCode:" + requestCode);
            return;
        }

        //权限
        final String requestPermission = requestPermissions[requestCode];
        LogUtils.e("requestPermission :" + requestPermission);

        int checkSelPermission;

        try {
            //检查是否有此权限
            checkSelPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
        } catch (Exception e) {
            //如果用户关闭了你申请的权限，ActivityCompat.checkSelfPermission()会导致程序崩溃，catch异常
            LogUtils.e("RuntimeException:" + e.getMessage() + "please open this permission");
            return;
        }

        //需要的权限不存在，动态获取
        if (checkSelPermission != PackageManager.PERMISSION_GRANTED) {
            LogUtils.e("checkSelPermission=" + checkSelPermission + "===权限不存在");
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                LogUtils.e("shouldShowRequestPermissionRationale:" + requestPermission);
                shouldShowRationale(activity, requestCode, requestPermission);
            } else {
                LogUtils.e("requestPermission:" + requestPermission);
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            }

        } else {
            LogUtils.e("checkSelPermission=" + checkSelPermission + "===权限存在" + "opened:" + requestPermissions[requestCode]);
            permissionGrant.onPermissionGranted(requestCode);
        }


    }

    /**
     * 一次申请多个权限
     *
     * @param activity
     * @param grant
     */
    public static void requestMultiPermissions(final Activity activity, PermissionGrant grant) {
        final List<String> permissionsList = getNoGrantedPermission(activity, false);
        final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, true);
        //TODO checkSelfPermission
        if (permissionsList == null || shouldRationalePermissionsList == null) {
            return;
        }
        Log.d(TAG, "requestMultiPermissions permissionsList:" + permissionsList.size() + ",shouldRationalePermissionsList:" + shouldRationalePermissionsList.size());

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), CODE_MULTI_PERMISSION);
            Log.d(TAG, "showMessageOKCancel requestPermissions");
        } else if (shouldRationalePermissionsList.size() > 0) {
            showMessageOKCancel(activity, "should open those permission", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity, shouldRationalePermissionsList.toArray(new String[shouldRationalePermissionsList.size()]),
                            CODE_MULTI_PERMISSION);
                    Log.d(TAG, "showMessageOKCancel requestPermissions");
                }
            });
        } else {
            grant.onPermissionGranted(CODE_MULTI_PERMISSION);
        }
    }

    public static void requestMultiPermissions(final Activity activity, String[] requestPermissions, PermissionGrant grant) {
        final List<String> permissionsList = getNoGrantedPermission(activity, requestPermissions, false);
        final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, requestPermissions, true);
        if (permissionsList == null || shouldRationalePermissionsList == null) {
            return;
        }

        LogUtils.e("requestMultiPermissions permissionsList:" + permissionsList.size() + ",shouldRationalePermissionsList:" + shouldRationalePermissionsList.size());

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), CODE_MULTI_PERMISSION);
        } else if (shouldRationalePermissionsList.size() > 0) {
            showMessageOKCancel(activity, activity.getString(R.string.permission_hint), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity, shouldRationalePermissionsList.toArray(new String[shouldRationalePermissionsList.size()]),
                            CODE_MULTI_PERMISSION);
                }
            });
        } else {
            grant.onPermissionGranted(CODE_MULTI_PERMISSION);
        }
    }

    private static void showMessageOKCancel(final Activity context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", null)
                .create()
                .show();

    }

    /**
     * isShouldRationale
     * isShouldRationale=true:eturn no granted and shouldShowRequestPermissionRationale permissions
     * isShouldRationale=false:eturn no granted and !shouldShowRequestPermissionRationale
     *
     * @param activity
     * @param isShouldRationale
     * @return
     */

    private static List<String> getNoGrantedPermission(Activity activity, boolean isShouldRationale) {

        ArrayList<String> permissions = new ArrayList<>();
        for (int i = 0; i < requestPermissions.length; i++) {
            String requestPermission = requestPermissions[i];
            //TODO checkSelfPermission
            int checkSelfPermission = -1;
            try {
                checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
            } catch (RuntimeException e) {
                Toast.makeText(activity, "please open those permission", Toast.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "RuntimeException:" + e.getMessage());
                return null;
            }

            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "getNoGrantedPermission ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED:" + requestPermission);

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                    Log.d(TAG, "shouldShowRequestPermissionRationale if");
                    if (isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                } else {
                    if (!isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                    Log.d(TAG, "shouldShowRequestPermissionRationale else");
                }

            }
        }

        return permissions;
    }

    private static List<String> getNoGrantedPermission(Activity activity, String[] requestPermissions, boolean isShouldRationale) {

        ArrayList<String> permissions = new ArrayList<>();
        for (int i = 0; i < requestPermissions.length; i++) {
            String requestPermission = requestPermissions[i];
            int checkSelfPermission;
            try {
                checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
            } catch (RuntimeException e) {
                LogUtils.e("RuntimeException:" + e.getMessage() + "please open those permission");
                return null;
            }

            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                LogUtils.e("checkSelPermission=" + checkSelfPermission + "===权限不存在");
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                    LogUtils.e("shouldShowRequestPermissionRationale if");
                    if (isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                } else {
                    if (!isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                    LogUtils.e("shouldShowRequestPermissionRationale else");
                }

            }
        }

        return permissions;
    }

    private static void shouldShowRationale(final Activity activity, final int requestCode,
                                            final String requestPermission) {
        //TODO
        String[] permissionsHint = activity.getResources().getStringArray(R.array.permissions);
        showMessageOKCancel(activity, permissionsHint[requestCode], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
                LogUtils.e("shouldShowRationale showMessageOKCancel requestPermissions:" + requestPermission);
            }
        });

    }

    /**
     * @param activity
     * @param requestCode  Need consistent with requestPermission
     * @param permissions
     * @param grantResults
     */
    public static void requestPermissionsResult(final Activity activity, final int requestCode, String[] permissions,
                                                int[] grantResults, PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }

        LogUtils.e("requestPermissionsResult requestCode:" + requestCode);

        if (requestCode == CODE_MULTI_PERMISSION) {
            requestMultiResult(activity, permissions, grantResults, permissionGrant);
            return;
        }

        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            LogUtils.e("requestPermissionsResult illegal requestCode:" + requestCode);
            Toast.makeText(activity, "illegal requestCode:" + requestCode, Toast.LENGTH_SHORT).show();
            return;
        }

        LogUtils.e("requestPermissionsResult requestCode:" + requestCode + "\npermissions:" + permissions.toString() + "\ngrantResults:" + grantResults.toString() + "\nlength" + grantResults.length);
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //TODO success, do something, can use callback
            LogUtils.e("onRequestPermissionsResult PERMISSION_GRANTED");
            permissionGrant.onPermissionGranted(requestCode);

        } else {
            //TODO hint user this permission function
            LogUtils.e("onRequestPermissionsResult PERMISSION NOT GRANTED");
            String[] permissionsHint = activity.getResources().getStringArray(R.array.permissions);
            openSettingActivity(activity, activity.getResources().getString(R.string.permission_hint));
        }

    }

    private static void requestMultiResult(Activity activity, String[] permissions, int[] grantResults, PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }

        LogUtils.e("onRequestPermissionsResult permissions length:" + permissions.length);

        Map<String, Integer> perms = new HashMap<>();

        ArrayList<String> notGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            LogUtils.e("permissions: [i]:" + i + ", permissions[i]" + permissions[i] + ",grantResults[i]:" + grantResults[i]);
            perms.put(permissions[i], grantResults[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                notGranted.add(permissions[i]);
            }
        }

        if (notGranted.size() == 0) {
//            Toast.makeText(activity, "all permission success" + notGranted, Toast.LENGTH_SHORT)
//                    .show();
            permissionGrant.onPermissionGranted(CODE_MULTI_PERMISSION);
        } else {
            openSettingActivity(activity, activity.getResources().getString(R.string.permission_hint));
        }

    }

    private static void openSettingActivity(final Activity activity, String message) {

        showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                LogUtils.e("getPackageName(): " + activity.getPackageName());
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
    }

}
