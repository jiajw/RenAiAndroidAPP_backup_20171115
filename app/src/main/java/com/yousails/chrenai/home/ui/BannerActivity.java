package com.yousails.chrenai.home.ui;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/7/4.
 */

public class BannerActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;
    private WebView webView;
    private ProgressBar progressBar;
    private UMShareListener mShareListener;
    private ShareAction mShareAction;
    private String url;
    private String title;
    private String image;


    @Override
    protected void setContentView() {

        setContentView(R.layout.activity_banner);
    }

    @Override
    protected void init() {

        Intent intent=getIntent();
        if(intent!=null){
           url= intent.getStringExtra("url");
           title= intent.getStringExtra("title");
           image= intent.getStringExtra("image");
        }

    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("详情");

        sharedLayout = (LinearLayout) findViewById(R.id.btn_more);
        TextView sharedView = (TextView) findViewById(R.id.txt_more);
        sharedView.setText("分享");

        progressBar = (ProgressBar) findViewById(R.id.pbar_loading);
//        progressBar.setIndeterminate(true);

        webView=(WebView)findViewById(R.id.webview);
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
//        webSetting.setSupportZoom(true);
//        webSetting.setBuiltInZoomControls(true);
        webView.setWebViewClient(client);
        webView.setWebChromeClient(chromeClient);
        webView.loadUrl(url);

        initShareBoard();
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
                BannerActivity.this.finish();
                break;
            case R.id.btn_more:
                ShareBoardConfig config = new ShareBoardConfig();
                config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
                config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR); // 圆角背景
                config.setIndicatorVisibility(false);//隐藏指示器
                config.setTitleVisibility(false); // 隐藏title
                config.setCancelButtonText("关闭");// 设置取消按钮文本内容
//                config.setCancelButtonVisibility(false); // 隐藏取消按钮
                mShareAction.open(config);
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


    /**
     * 初始化友盟分享面板
     */
    private void initShareBoard() {
        mShareListener = new CustomShareListener(BannerActivity.this);
        /*增加自定义按钮的分享面板*/
        mShareAction = new ShareAction(mContext).setDisplayList(
                SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.QQ, SHARE_MEDIA.SINA, SHARE_MEDIA.QZONE)
                .addButton("umeng_sharebutton_copyurl", "umeng_sharebutton_copyurl", "umeng_socialize_copyurl", "umeng_socialize_copyurl")
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        if (snsPlatform.mShowWord.equals("umeng_sharebutton_copyurl")) {
                            // 得到剪贴板管理器
                            ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                            cmb.setText(url);
                            Toast.makeText(BannerActivity.this, "已复制链接到粘贴板", Toast.LENGTH_LONG).show();

                        } else if (share_media == SHARE_MEDIA.SMS) {
                            UMWeb web = new UMWeb(url);
                            web.setTitle(title);
                            web.setDescription(title);
                            new ShareAction(BannerActivity.this).withMedia(web)
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener)
                                    .share();
                        } else {
                            UMWeb web = new UMWeb(url);
                            web.setTitle(title);
                            web.setDescription(title);
                            web.setThumb(new UMImage(BannerActivity.this, image));
                            new ShareAction(BannerActivity.this).withMedia(web)
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener)
                                    .share();
                        }
                    }
                });
    }


    private static class CustomShareListener implements UMShareListener {

        private WeakReference<BannerActivity> mActivity;

        private CustomShareListener(BannerActivity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {

            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                        && platform != SHARE_MEDIA.EMAIL
                        && platform != SHARE_MEDIA.FLICKR
                        && platform != SHARE_MEDIA.FOURSQUARE
                        && platform != SHARE_MEDIA.TUMBLR
                        && platform != SHARE_MEDIA.POCKET
                        && platform != SHARE_MEDIA.PINTEREST

                        && platform != SHARE_MEDIA.INSTAGRAM
                        && platform != SHARE_MEDIA.GOOGLEPLUS
                        && platform != SHARE_MEDIA.YNOTE
                        && platform != SHARE_MEDIA.EVERNOTE) {
                    Toast.makeText(mActivity.get(), platform + " 分享成功", Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                    && platform != SHARE_MEDIA.EMAIL
                    && platform != SHARE_MEDIA.FLICKR
                    && platform != SHARE_MEDIA.FOURSQUARE
                    && platform != SHARE_MEDIA.TUMBLR
                    && platform != SHARE_MEDIA.POCKET
                    && platform != SHARE_MEDIA.PINTEREST

                    && platform != SHARE_MEDIA.INSTAGRAM
                    && platform != SHARE_MEDIA.GOOGLEPLUS
                    && platform != SHARE_MEDIA.YNOTE
                    && platform != SHARE_MEDIA.EVERNOTE) {
                Toast.makeText(mActivity.get(), " 分享失败，稍后重试。", Toast.LENGTH_SHORT).show();

                if (t != null) {
                    Log.d("throw", "throw:" + t.getMessage());
                }
            }

        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {

//            Toast.makeText(mActivity.get(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    }
}
