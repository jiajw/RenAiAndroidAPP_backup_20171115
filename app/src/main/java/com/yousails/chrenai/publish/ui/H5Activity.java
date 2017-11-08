package com.yousails.chrenai.publish.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.smtt.sdk.WebView;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;

/**
 * Author:WangKunHui
 * Date: 2017/7/31 11:35
 * Desc:
 * E-mail:life_artist@163.com
 */
public class H5Activity extends BaseActivity {

    private static String H5_URL = "URL";
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;


    private TextView title;
    private WebView webView;

    private String url;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_h5);
    }

    @Override
    protected void init() {
        this.url = getIntent().getStringExtra(H5_URL);
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);

        title = (TextView) findViewById(R.id.title) ;
        title.setText("活动详情");
        webView = (WebView) findViewById(R.id.webView);
    }

    @Override
    protected void initViews() {
        Log.e("activity_preview_url",url);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.loadUrl(url);
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void handleMessage() {

    }

    @Override
    public void initData() {

    }

    public static void launch(Context context, String url) {
        Intent intent = new Intent(context, H5Activity.class);
        intent.putExtra(H5_URL, url);
        context.startActivity(intent);
    }
}
