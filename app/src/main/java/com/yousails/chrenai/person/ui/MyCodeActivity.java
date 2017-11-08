package com.yousails.chrenai.person.ui;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;

import static android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK;
import static com.yousails.chrenai.R.id.webView;


/**
 * Created by liuwen on 2017/8/8.
 */

public class MyCodeActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private WebView web;
    private ImageView imgCode;
    private String code_url;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_my_code);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText(R.string.txt_my_code);

        sharedLayout = (LinearLayout) findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.INVISIBLE);


        imgCode = (ImageView) findViewById(R.id.img_code);
        web = (WebView) findViewById(R.id.web);
    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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


        code_url = ApiConstants.code_url + AppPreference.getInstance(mContext).readToken().replace("Bearer ", "");
        Log.e("code-url", code_url);
        LogUtil.e("===code-url===" + code_url);
        /*Glide
                .with(MyCodeActivity.this)
                .load(code_url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.mipmap.ic_launcher)
                .into(imgCode);*/


        web.loadUrl(code_url);
        WebSettings settings = web.getSettings();
        web.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(LOAD_CACHE_ELSE_NETWORK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setDomStorageEnabled(true);
        web.requestFocus();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
                web.loadUrl(url);
                // 消耗掉这个事件。Android中返回True的即到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //view.loadUrl("JavaScript:window.location.assign('img://'+document.getElementsByTagName('img')[0].src)");
                super.onPageFinished(view, url);
                view.loadUrl("javascript:window.local_obj.showSource(document.getElementsByTagName('img')[1].src);");

            }
        });
    }

    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            Log.e("code-html", "______" + html + "____");
        }
    }

}
