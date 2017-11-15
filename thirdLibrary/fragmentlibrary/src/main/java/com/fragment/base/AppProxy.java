package com.fragment.base;

import android.app.Application;
import android.util.Log;

/**
 * Created by sym on 17/7/15.
 */

public class AppProxy {

    private final String TAG = "AppProxy";
    private static AppProxy mInstance;
    private Application mApplication;

    private AppProxy() {

    }

    public static AppProxy getInstance() {
        if (null == mInstance) {
            mInstance = new AppProxy();
        }
        return mInstance;
    }

    public void init(Application application) {
        this.mApplication = application;
    }

    public Application getApplication() {
        if (null == mApplication) {
            Log.e(TAG, "application is null");
        }
        return mApplication;
    }

}
