package com.yousails.chrenai.person.ui;

import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.widget.x5.X5WebView;

/**
 * Created by Administrator on 2017/8/10.
 */

public class FootMarkActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private TextView titleView;
    private WebView webView;
    private X5WebView footmarkView;
    private ProgressBar progressBar;
    private String title;
    private String url;
    private String user_id;
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_banner);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void findViews() {
        user_id = getIntent().getStringExtra("user_id");
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        if(TextUtils.isEmpty(user_id)) {
            titleView.setText("我的足迹");
        }else{
            titleView.setText("TA的足迹");
        }
        progressBar = (ProgressBar) findViewById(R.id.pbar_loading);
//        progressBar.setIndeterminate(true);

        footmarkView=(X5WebView)findViewById(R.id.web_footmark);
        footmarkView.setVisibility(View.VISIBLE);


        webView=(WebView)findViewById(R.id.webview);
//        WebSettings webSetting = webView.getSettings();
//        webSetting.setJavaScriptEnabled(true);
//        webSetting.setSupportZoom(true);
//        webSetting.setBuiltInZoomControls(true);
//        webView.setWebViewClient(client);
//        webView.setWebChromeClient(chromeClient);


        String mToken= AppPreference.getInstance(mContext).readToken();


        if(TextUtils.isEmpty(user_id)&&StringUtil.isNotNull(mToken)&&mToken.contains("Bearer")){
            mToken=mToken.substring(7);
            url= ApiConstants.GET_FOOTPRINT_API+mToken;

        }else{
             url = ApiConstants.BASE_URL+"/users/"+user_id+"/footprints";

        }
//        webView.loadUrl(url);

        footmarkView.loadUrl(url);

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.title_back:
                FootMarkActivity.this.finish();
                break;
        }
    }

    @Override
    protected void handleMessage() {

    }

    @Override
    public void initData() {

    }

    private WebViewClient client = new WebViewClient() {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            webView.loadData("页面请求失败,请检查网络是否异常!",
                    "text/html; charset=UTF-8", null);

        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
        }
    };


    private WebChromeClient chromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    };
}
