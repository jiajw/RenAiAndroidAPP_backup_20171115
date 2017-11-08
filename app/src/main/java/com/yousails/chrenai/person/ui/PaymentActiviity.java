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
import android.widget.Toast;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.person.bean.RegisterInforBean;
import com.yousails.chrenai.utils.NetUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/8/25.
 */

public class PaymentActiviity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private WebView webView;
    private ProgressBar progressBar;

    private RegisterInforBean registerInforBean;
    private String type;
    private String cId="";
    private String url;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_banner);
    }

    @Override
    protected void init() {
        registerInforBean=(RegisterInforBean)getIntent().getSerializableExtra("registerInforBean");
        type=getIntent().getStringExtra("type");
        //type: pay 付款 ，refund 退款

    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("缴费详情");

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


        String token= AppPreference.getInstance(mContext).readToken();
        if(token.contains("Bearer")){
            token=token.substring(7);
        }

       if(registerInforBean!=null){
           if("pay".equals(type)){
               url= ApiConstants.BASE_URL+"/applications/"+registerInforBean.getId()+"/payments/pay?token="+token;
           }else{
               url= ApiConstants.BASE_URL+"/applications/"+registerInforBean.getId()+"/payments/refund?token="+token;
           }

           webView.loadUrl(url);
       }

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        sharedLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_more:
                if("pay".equals(type)){
                    doPayments("pay");
                }else{
                    doPayments("refund");
                }
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
            if ("chrenai://paymentSuccess".equals(url)) {
                setResult(1);
                finish();
            }

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
     * 添加缴费记录
     */
    private void doPayments(String type){
        if (registerInforBean == null) {
            return;
        }
        if(!NetUtil.detectAvailable(mContext)){
            Toast.makeText(mContext,"请链接网络", Toast.LENGTH_SHORT).show();
            return;
        }
        String token=AppPreference.getInstance(mContext).readToken();
        String url=ApiConstants.GET_COMMENTS_API+cId+"/payments";

        Map<String,String> paramMap=new HashMap<String,String>();
        paramMap.put("id",cId);
        paramMap.put("type",type);
        paramMap.put("user_id",registerInforBean.getUser().getId());

        if("pay".equals(type)){
            paramMap.put("amount",registerInforBean.getPay_amount());
        }else{
            paramMap.put("amount",registerInforBean.getRefund_amount());
        }

        OkHttpUtils.post().url(url).addHeader("Authorization",token).params(paramMap).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {

                System.out.println(response);
            }

            @Override
            public void onResponse(String response, int id) {

                System.out.println(response);
            }
        });
    }
}
