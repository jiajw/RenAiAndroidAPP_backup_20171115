package com.fragment.base;

import android.content.Context;

import test.longfor.com.fragmentlibrary.R;


/**
 * Created by sym on 17/5/28.
 */

public class BaseActivity extends SFragmentActivity {
    @Override
    public int getFragmentContainerId() {
        return R.id.frame;
    }

    @Override
    public Context getApplicationContext() {

        return AppProxy.getInstance().getApplication();
    }
}
