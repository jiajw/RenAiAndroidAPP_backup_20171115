package com.yousails.chrenai.person.ui;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.person.bean.RegisterInforBean;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/8/24.
 */

public class AddRegistDetailActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private WebView webView;

    private LinearLayout bottomLayout;
    private LinearLayout passedLayout;
    private LinearLayout rejectedLayout;
    private LinearLayout dividerLine;
    private ProgressBar progressBar;

    private RegisterInforBean registerInforBean;
    private String cId="";
    private String status;
    private String url;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_additional);
    }

    @Override
    protected void init() {
        registerInforBean=(RegisterInforBean)getIntent().getSerializableExtra("registerInfor");
        if(registerInforBean!=null){
            cId=registerInforBean.getId();
            status=registerInforBean.getStatus();
        }

    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("追加报名申请");

        sharedLayout = (LinearLayout) findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.pbar_loading);
//        progressBar.setIndeterminate(true);

        bottomLayout=(LinearLayout)findViewById(R.id.bottom_layout);
        passedLayout=(LinearLayout)findViewById(R.id.passed_layout);
        rejectedLayout=(LinearLayout)findViewById(R.id.rejected_layout);
        dividerLine=(LinearLayout)findViewById(R.id.divider_line);

        if(StringUtil.isNotNull(status)){
            if("rejected".equals(status)){
                bottomLayout.setVisibility(View.VISIBLE);
                passedLayout.setVisibility(View.VISIBLE);
                rejectedLayout.setVisibility(View.GONE);
                dividerLine.setVisibility(View.GONE);
            }else if("applied".equals(status)){
                bottomLayout.setVisibility(View.VISIBLE);
                passedLayout.setVisibility(View.VISIBLE);
                rejectedLayout.setVisibility(View.VISIBLE);
                dividerLine.setVisibility(View.VISIBLE);
            }
        }

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
        url=ApiConstants.BASE_URL+"/additional/applications/"+cId+"?token="+token;
        webView.loadUrl(url);
    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        passedLayout.setOnClickListener(this);
        rejectedLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.passed_layout:
                allowEnroll("passed");
                break;
            case R.id.rejected_layout:
                allowEnroll("rejected");
                break;
        }
    }

    @Override
    protected void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        setResult(1);
                        finish();
                        break;
                }
            }
        };
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


    /**
     * 同意/删除报名
     */
    //passed 审核通过，rejected 拒绝
    private void allowEnroll(final String status) {
        if (registerInforBean == null) {
            return;
        }
        String pid = registerInforBean.getActivity_id();
        String id = registerInforBean.getId();

        String url = ApiConstants.GET_COMMENTS_API + pid + "/applications/" + id;
        String token = AppPreference.getInstance(mContext).readToken();

        RequestBody requestBody = new FormBody.Builder()
                .add("status", status)
                .add("pid", pid)
                .add("id", id)
                .build();


        OkHttpUtils.patch().url(url).addHeader("Authorization", token).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                if("passed".equals(status)){
                    Toast.makeText(mContext,"允许报名失败！",Toast.LENGTH_SHORT).show();
                }else if("rejected".equals(status)){
                    Toast.makeText(mContext,"拒绝报名失败！",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onResponse(String response, int id) {
                mHandler.sendEmptyMessage(1);
            }
        });
    }
}
