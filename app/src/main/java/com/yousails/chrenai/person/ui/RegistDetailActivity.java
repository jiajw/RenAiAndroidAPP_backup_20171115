package com.yousails.chrenai.person.ui;

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

/**
 * Created by Administrator on 2017/8/24.
 */

public class RegistDetailActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private WebView webView;
    private ProgressBar progressBar;
    private String cId="";
    private String url;
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_banner);
    }

    @Override
    protected void init() {
        cId=getIntent().getStringExtra("cid");

    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("报名者详情");

        sharedLayout = (LinearLayout) findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.pbar_loading);
//        progressBar.setIndeterminate(true);

        webView=(WebView)findViewById(R.id.webview);
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
//        webSetting.setSupportZoom(true);
//        webSetting.setBuiltInZoomControls(true);
        webView.setWebViewClient(client);
        webView.setWebChromeClient(chromeClient);


        String token=AppPreference.getInstance(mContext).readToken();
        if(token.contains("Bearer")){
            token=token.substring(7);
        }

        url=ApiConstants.BASE_URL+"/applications/"+cId+"?token="+token;
        webView.loadUrl(url);
    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.title_back:
                finish();
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
