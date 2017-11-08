package com.yousails.chrenai.login.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yousails.chrenai.BuildConfig;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.app.ui.MainActivity;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.framework.util.CommonUtil;
import com.yousails.chrenai.framework.util.CustomDialog;
import com.yousails.chrenai.framework.util.CustomDialogHelper;
import com.yousails.chrenai.framework.util.FileUtils;
import com.yousails.chrenai.framework.util.GsonUtils;
import com.yousails.chrenai.module.bean.VersionBean;
import com.yousails.chrenai.utils.PermissionUtils;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.utils.TimeUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 启动页
 * Created by Administrator on 2017/7/17.
 */

public class SplashActivity extends BaseActivity {
    private LinearLayout splashLayout;

    private ProgressBar progressBar;
    private TextView tvLeft;
    private TextView tvRight;
    private VersionBean versionBean;

    /* 下载保存路径 */
    private String mSavePath;
    /* 下载的请求地址 */
    private String downloadURL;
    /* 下载文件名 */
    private String downloadFileName;
    private int contentLen;//声明要下载的文件总长
    private CustomDialog customDialog;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void init() {
    }

    @Override
    protected void findViews() {
        splashLayout = (LinearLayout) findViewById(R.id.splash_layout);

        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        splashLayout.startAnimation(animation);

    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0: {
                        reLogin();
                    }
                    break;
                    case 1:
                        navToHome();
                        break;

                    case 2:
                        //有版本更新
                        VersionBean version = (VersionBean) msg.obj;
                        //更新提示
                        showForcedUpdateDialog(version);
                        break;
                    case 3:
                        //没有版本更新
                        checkToken();
                        break;

                }
            }
        };
    }

    @Override
    public void initData() {
        checkUpdate();
    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    /**
     * 跳转到主界面
     */
    private void navToHome() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }


    /**
     * 跳转到登录界面
     */
    private void navToLogin() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }

    /**
     * 是否已有用户登录
     */
    private boolean isUserLogin() {
        return AppPreference.getInstance(mContext).readLogin();
    }


    /***
     * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
     */
    //拍照、定位、网络、读短信
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_LOGS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.SET_DEBUG_APP,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);

        }


    }


    /**
     * 判断token是否过期
     */
    private void checkToken() {

        //当前时间
        long currentTime = new Date().getTime();
        //过期时间
        long mExpired = TimeUtil.getSecondsFromDate(AppPreference.getInstance(mContext).readExpired());//过期时间 24小时
        //可刷新时间
        long mRefreshToken = TimeUtil.getSecondsFromDate(AppPreference.getInstance(mContext).readRefreshExpired());//可刷新时间 7天
        if (isUserLogin()) {
            //如果用户已登陆
            if (currentTime < mExpired) {
                //未过期，正常跳转到主页
                navToHome();
            } else if (currentTime > mExpired && currentTime < mRefreshToken) {
                //执行刷新操作
                refreshToken();
            } else if (currentTime > mRefreshToken) {
                //退出登陆，跳转到登陆页面
                reLogin();
            }
        } else {
            navToHome();
        }


    }

    //重新登陆
    private void reLogin() {

        clearUserInfor();
        navToLogin();
    }

    //检查更新
    private void checkUpdate() {
        Map<String, String> params = new HashMap<>();
        params.put("type", "android");

        OkHttpUtils.get()
                .url(ApiConstants.CHECK_VERSION_URL)
                .params(params)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                Message message = new Message();
                message.what = 3;
                mHandler.sendMessage(message);
            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {

                    versionBean = GsonUtils.toBean(response, VersionBean.class);
                    LogUtil.e("===versionBean==" + versionBean);

                    int versionCode = versionBean.getVersion();
                    if (versionCode > CommonUtil.getVersionCode(SplashActivity.this)) {
                        Message message = new Message();
                        message.what = 2;
                        message.obj = versionBean;
                        mHandler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = 3;
                        mHandler.sendMessage(message);
                    }
                } else {
                    Message message = new Message();
                    message.what = 3;
                    mHandler.sendMessage(message);
                }

            }
        });
    }


    /**
     * 清空用户登录信息
     */
    private void clearUserInfor() {
        //保存到sharepreference
        String userId = AppPreference.getInstance(mContext).readUerId();
        AppPreference.getInstance(mContext).writePhone(userId, "");
        AppPreference.getInstance(mContext).writeUserId("");
        AppPreference.getInstance(mContext).writeLastUserId("");
        AppPreference.getInstance(mContext).writeUserName("");
        AppPreference.getInstance(mContext).writeRealName("");
        AppPreference.getInstance(mContext).writeAvatar("");
        AppPreference.getInstance(mContext).writeGender("male");
        AppPreference.getInstance(mContext).writeReligion("");
        AppPreference.getInstance(mContext).writeIsVip("");
        AppPreference.getInstance(mContext).writeCertification("");
        AppPreference.getInstance(mContext).writeWorkHours("");
        AppPreference.getInstance(mContext).writeLevel("");
        AppPreference.getInstance(mContext).writeEMName("");
        AppPreference.getInstance(mContext).writeEMPwd("");

        AppPreference.getInstance(mContext).setLogin(false);
        AppPreference.getInstance(mContext).setLogout(true);

        AppPreference.getInstance(mContext).writeToken("");
        AppPreference.getInstance(mContext).writeExpired("");
        AppPreference.getInstance(mContext).writeRefreshExpired("");

//        AppPreference.getInstance(mContext).writeWXToken("");
//        AppPreference.getInstance(mContext).writeWXOpenId("");
//        AppPreference.getInstance(mContext).writeWXRefreshToken("");
    }

    /**
     * 刷新 重新获取token
     */
    private void refreshToken() {

        /*if (!NetUtil.detectAvailable(mContext)) {
            //  Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }*/

        String mToken = AppPreference.getInstance(mContext).readToken();
        RequestBody requestBody = new FormBody.Builder().add("Authorization", mToken).build();
        String url = ApiConstants.REFRESH_AUTHORIZATIONS_API;
        OkHttpUtils.put().url(ApiConstants.REFRESH_AUTHORIZATIONS_API).addHeader("Authorization", mToken).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                //刷新失败，重新登录
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    LogUtil.e("===response==" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        //保存到sharepreference
                        AppPreference.getInstance(mContext).writeToken("Bearer " + jsonObject.optString("token"));
                        AppPreference.getInstance(mContext).writeExpired(jsonObject.optString("expired_at"));
                        AppPreference.getInstance(mContext).writeRefreshExpired(jsonObject.optString("refresh_expired_at"));

                        mHandler.sendEmptyMessage(1);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }


    /**
     * 强制更新提示框
     */
    public void showForcedUpdateDialog(final VersionBean versionBean) {

        String description = null;
//        String description = versionBean.getDescription();
        if (TextUtils.isEmpty(description)) {
            description = getString(R.string.soft_update_tip);
        }
        CustomDialogHelper.showUpdateTipDialog(SplashActivity.this, "提示", description, "取消", "确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        checkToken();

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (Build.VERSION.SDK_INT >= 23) {
                            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            PermissionUtils.requestMultiPermissions(SplashActivity.this, perms, mPermissionGrant);
                        } else {
                            if (!TextUtils.isEmpty(versionBean.getUrl())) {

                                //开始下载
                                startDownloadApk();
                            }
                        }
                    }
                });


    }

    private void showDownloadingDialog() {
        View contentView = LayoutInflater.from(SplashActivity.this).inflate(R.layout.update_progress_layout, null);
        progressBar = (ProgressBar) contentView.findViewById(R.id.download_progress);
        tvLeft = (TextView) contentView.findViewById(R.id.tv_progress_tag1);
        tvRight = (TextView) contentView.findViewById(R.id.tv_progress_tag2);
        customDialog = CustomDialogHelper.showUpdateProgressDialog(SplashActivity.this, contentView);
        if (!customDialog.isShowing()) {
            customDialog.show();
        }

    }

    /**
     * 安装包下载
     */
    public void startDownloadApk() {

        downloadURL = versionBean.getUrl();
        LogUtil.e("==downloadURL==" + downloadURL);
        //启用AsyncTask，传入需要执行的内容（地址）
        int lastSlashIndex = downloadURL.lastIndexOf("/");
        downloadFileName = downloadURL.substring(lastSlashIndex + 1);
        new DownLoadTask().execute(downloadURL);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_MULTI_PERMISSION:
                    startDownloadApk();
                    break;

                default:
                    break;
            }
        }
    };

    class DownLoadTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDownloadingDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (customDialog != null) {
                customDialog.dismiss();
            }

            installApk();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            tvLeft.setText(values[0] + 1 + "%");
            tvRight.setText(values[0] + 1 + "/100");
        }

        @Override
        protected String doInBackground(String... params) {

            // 建立下载的apk文件
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                mSavePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
            } else {
                mSavePath = Environment.getDownloadCacheDirectory().getPath();
            }

            File file = FileUtils.createNewFile(mSavePath + "/" + downloadFileName);

            URL url;
            try {
                url = new URL(downloadURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();

                //计算文件长度
                contentLen = conn.getContentLength();
                InputStream in = conn.getInputStream();

                byte[] buffer = new byte[4096];
                int len;
                FileOutputStream out = new FileOutputStream(file);
                int temp = 0;

                while ((len = in.read(buffer)) > 0) {
                    // 当前进度
//                    int schedule = (int) ((total * 100) / contentLen);
                    int schedule = (int) ((file.length() * 100) / contentLen);
                    LogUtil.e("==schedule=" + schedule);
                    out.write(buffer, 0, len);
                    out.flush();

                    publishProgress(schedule);
                }

                out.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void installApk() {

        File apkFile = new File(mSavePath, downloadFileName);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(SplashActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
        MobclickAgent.onKillProcess(this);
        android.os.Process.killProcess(android.os.Process.myPid());

    }


}


