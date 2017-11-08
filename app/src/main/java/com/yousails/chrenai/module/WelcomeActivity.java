package com.yousails.chrenai.module;

import android.view.View;

import com.yousails.chrenai.R;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.framework.base.FrameworkBaseActivity;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.utils.TimeUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.Date;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 重构启动页
 * User: jiajinwu
 * Date: 2017-11-01
 * Time: 09:55
 * 修改备注：
 * version:
 */


public class WelcomeActivity extends FrameworkBaseActivity {


    @Override
    public void initContentView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        //1.获取token
        //2.checkUpdate

    }

    @Override
    public void processClick(View view) {

    }


}
