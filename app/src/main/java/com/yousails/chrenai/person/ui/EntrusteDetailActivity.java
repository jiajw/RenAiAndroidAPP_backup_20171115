package com.yousails.chrenai.person.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
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

import com.umeng.socialize.UMShareAPI;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.person.bean.RegisterInforBean;
import com.yousails.chrenai.view.CancelEntrustDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/9/5.
 */

public class EntrusteDetailActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private LinearLayout bottomLayout;
    private LinearLayout enrollLayout;
    private LinearLayout entrustLayout;
    private LinearLayout editorLayout;
    private LinearLayout rescindLayout;
    private TextView titleView;
    private WebView webView;
    private ProgressBar progressBar;
    private CancelEntrustDialog cancelEntrustDialog;

    private RegisterInforBean registerInforBean;
    private String cId="";
    private String type;
    private String url;


    @Override
    protected void setContentView() {

        setContentView(R.layout.activity_entrusted_detail);
        // 注册对象
        EventBus.getDefault().register(this);
    }

    @Override
    protected void init() {
        // type:  entrust/editor/cancel
        type=getIntent().getStringExtra("type");
        registerInforBean=(RegisterInforBean)getIntent().getSerializableExtra("registerInfor");
        if(registerInforBean!=null){
            cId=registerInforBean.getId();

        }
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
//        titleView=(TextView)findViewById(R.id.title);
//        titleView.setText("委托详情");
        findViewById(R.id.title_divider_line).setVisibility(View.INVISIBLE);

        sharedLayout = (LinearLayout) findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.GONE);

        bottomLayout=(LinearLayout)findViewById(R.id.bottom_layout);
        enrollLayout=(LinearLayout) findViewById(R.id.enroll_layout);
        entrustLayout=(LinearLayout) findViewById(R.id.entrust_layout);
        editorLayout=(LinearLayout) findViewById(R.id.editor_layout);
        rescindLayout=(LinearLayout) findViewById(R.id.rescind_entrustment_layout);

        if("entrusted".equals(type)){
            enrollLayout.setVisibility(View.GONE);
            entrustLayout.setVisibility(View.VISIBLE);

        }else{
            enrollLayout.setVisibility(View.VISIBLE);
            entrustLayout.setVisibility(View.GONE);
        }

        progressBar = (ProgressBar) findViewById(R.id.pbar_loading);
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

        //委托详情页
        url= ApiConstants.BASE_URL+"/entrusted/applications/"+cId+"?token="+token;
        webView.loadUrl(url);
    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        enrollLayout.setOnClickListener(this);
        editorLayout.setOnClickListener(this);
        rescindLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.enroll_layout:
                startActivity("entrust");
                break;
            case R.id.editor_layout:
                startActivity("editor");
                break;
            case R.id.rescind_entrustment_layout:
                showDialog();
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
//                        setResult(1);
//                        finish();
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
                bottomLayout.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    };



    /**
     * 取消委托确认对话框
     */
    private void showDialog(){
        if(cancelEntrustDialog==null){
            cancelEntrustDialog=new CancelEntrustDialog(mContext,onClickListener);
        }
        cancelEntrustDialog.show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cancel_btn:
                    cancelEntrustDialog.dismiss();
                    rescindEntrustment();
                    break;
            }
        }
    };


    /**
     * 取消委托
     */
    private void rescindEntrustment(){
        String token = AppPreference.getInstance(mContext).readToken();
        String url=ApiConstants.GET_COMMENTS_API+registerInforBean.getActivity_id()+"/entrusted/applications/"+cId;
        OkHttpUtils.delete().url(url).addHeader("Authorization",token).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                System.out.print(response);
                Toast.makeText(mContext,"取消委托失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.print(response);
                Toast.makeText(mContext,"取消委托成功",Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post("updateData");
                finish();
            }
        });
    }

    /**
     * 跳转
     */
    private void startActivity(String type) {
        Intent intent=new Intent(mContext,EntrusteSettingActivity.class);
        intent.putExtra("type",type);
        intent.putExtra("registerInfor", registerInforBean);

//            startActivity(intent);
        startActivityForResult(intent, 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1 == resultCode) {
            String token= AppPreference.getInstance(mContext).readToken();
            if(token.contains("Bearer")){
                token=token.substring(7);
            }

            //委托详情页
            url= ApiConstants.BASE_URL+"/entrusted/applications/"+cId+"?token="+token;
            webView.loadUrl(url);

            if("not_entrusted".equals(type)){
                //刷新列表
                EventBus.getDefault().post("updateData");
//                finish();
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEntrusted(String message) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
