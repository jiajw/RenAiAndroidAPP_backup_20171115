package com.yousails.chrenai.config;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.multidex.MultiDex;

import com.fragment.base.AppProxy;
import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseUI;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.yousails.chrenai.app.service.LocationService;
import com.yousails.chrenai.app.ui.ActivityManager;
import com.yousails.chrenai.db.DBHelper;
import com.yousails.chrenai.home.bean.CityBean;
import com.yousails.chrenai.im.DemoHelper;
import com.yousails.chrenai.utils.UiUtil;
import com.yousails.chrenai.utils.https.TrustAllCerts;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wordpress.android.editor.app.EditorAppProxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/6/15.
 */

public class ModelApplication extends Application {
    public static Context mContext;

    /**
     * Application单例
     */
    private static ModelApplication sInstance;
    /**
     * TAG
     */
    public final String TAG = "BaseApplication";

    /**
     *
     */
    private static ActivityManager activityManager = null;

    public LocationService locationService;

    public static List<CityBean> localCityList = new ArrayList<>();

    /**
     * @return 单例
     */
    public static synchronized ModelApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        //因为引用的包过多，实现多包问题
        MultiDex.install(this);
        super.onCreate();
        sInstance = this;
        mContext = this;
        initOkHttp();
        initUiParams();
        AppProxy.getInstance().init(this);
        EditorAppProxy.getInstance().init(this);
        initCity();
        initLocation();
        initEaseUi();
        UMShareAPI.get(this);

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        /*
        initSP();
        initDirs();
        initDB();
        initActivity();

        Logger.i(TAG, "创建");
      */
    }

    /***
     * 初始化定位sdk
     */
    private void initLocation() {
        locationService = new LocationService(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
    }


    /**
     * 初始化偏好单例
     */
    protected void initSP() {
        AppPreference.getInstance(mContext);
    }

    /**
     * 初始化数据库单例
     */
    protected void initDB() {
        DBHelper.getInstance(mContext);
    }

    /**
     * 初始化自定义Activity管理器
     */
    public ActivityManager getActivityManager() {
        if (activityManager == null) {
            activityManager = ActivityManager.getScreenManager();
        }
        return activityManager;
    }


    private void initOkHttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG", ApiConstants.IS_TEST))
                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    protected void initUiParams() {
        UiUtil.initUiParams(this);
    }


    //各个平台的配置，建议放在全局Application或者程序入口
    {
        PlatformConfig.setWeixin("wx187e2ddaf42085c3", "c79dc90303719b0431e39260ec0c0c96");

        PlatformConfig.setSinaWeibo("588754509", "b903fd02a0c48a61a524068357d6134e", "http://sns.whalecloud.com");

        PlatformConfig.setQQZone("101424762", "28ef3b95b87f6f2ec860dc30b2ce8810");

    }


    /**
     * 初始化城市数据
     */
    private void initCity() {
        // TODO Auto-generated method stub
        String[] cityArray = {"a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "w", "x", "y", "z"};
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(mContext.getAssets().open("cities.json")));
            String next = "";
            while (null != (next = br.readLine())) {
                sb.append(next);
            }

            JSONObject jsonObject = new JSONObject(sb.toString().trim());
            Type type = new TypeToken<CityBean>() {
            }.getType();
            localCityList.clear();
            for (int i = 0; i < cityArray.length; i++) {
                JSONArray jsonArray = jsonObject.optJSONArray(cityArray[i]);
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject json = jsonArray.optJSONObject(j);
                    CityBean cityBean = new Gson().fromJson(json.toString(), type);
                    localCityList.add(cityBean);
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void initEaseUi() {
//        EaseUI.getInstance().init(mContext,null);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
//        EMClient.getInstance().setDebugMode(true);
        DemoHelper.getInstance().init(this);
    }
}
