package com.yousails.chrenai.widget.x5;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.utils.TbsLog;
import com.yousails.chrenai.R;

import java.util.Properties;

/**
 * Use of 初始化腾讯x5内核
 * initX5Environment 内部会创建一个线程向后台查询当前可用内核版本号
 * ，这个函数内是异步执行所以不会阻塞 App 主线程，
 * 这个函数内是轻量级执行所以对 App 启动性能没有影响，
 * 当 App 后续创建 webview 时就可以首次加载 x5 内核了
 */

public abstract class X5WebRefreshActivity extends Activity {

    //    private BaseTitleBar titleBar;
    protected X5WebView webView;
    private TextView title;
    private TextView webDisCrip;
    private FrameLayout titleLayout;
    private String TAG = "X5WebRefreshActivity";
    /**
     * 配置
     */
    protected Properties property;
    private ProgressBar mPageLoadingProgressBar;
    private int MAX_LENGTH = 15;

    /**
     * 此类实现了下拉刷新，
     * 使用extension interface将会准确回去overScroll的时机
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_refresh_layout);
//        this.titleBar = createTitleBar();
//        property = BaseConfig.getInstance().getConfig();
        webView = (X5WebView) findViewById(R.id.web_filechooser);
        title = (TextView) findViewById(R.id.refreshText);
        webDisCrip = (TextView) findViewById(R.id.web_discription);
        titleLayout = (FrameLayout) findViewById(R.id.base_web_title);
        mPageLoadingProgressBar = (ProgressBar) findViewById(R.id.base_web_progressBar);
        mPageLoadingProgressBar.setMax(100);
//        if (titleBar != null) {
////            titleLayout.addView(titleBar.get_view());
//        }
//        webDisCrip.setText("");
        webView.setTitle(title);
        webView.getView().setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                WebView.HitTestResult hitTestResult = webView.getHitTestResult();
                switch (hitTestResult.getType()) {
                    case WebView.HitTestResult.IMAGE_TYPE://获取点击的标签是否为图片
                    case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE://获取点击的标签是否为图片
                        imageLongClick();
                        break;
                }
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                TbsLog.d(TAG, "title: " + title);
                if (webUrl() == null)
                    return;
                if (!webView.getUrl().equalsIgnoreCase(webUrl())) {
//                    if (title != null && title.length() > MAX_LENGTH)
//                        if (titleBar != null) {
//                            titleBar.getTitle_text().setText(title.subSequence(0, MAX_LENGTH) + "...");
//                        } else {
//                            if (titleBar != null) {
//                                titleBar.getTitle_text().setText(title);
//                            }
//                        }
                } else {
//                    if (titleBar != null) {
//                        titleBar.getTitle_text().setText("");
//                    }
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mPageLoadingProgressBar.setProgress(newProgress);
                if (mPageLoadingProgressBar != null && newProgress != 100) {
                    mPageLoadingProgressBar.setVisibility(View.VISIBLE);
                } else if (mPageLoadingProgressBar != null) {
                    mPageLoadingProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * @return 返回具有项目风格的TitleBar,如果某个具体的Activity需要修改TitleBar的样式，
     * 则在具体的Activity中重写此方法，返回自己想要的TitleBar
     */
//    public  BaseTitleBar createTitleBar(){
//        return null;
//    };


    public void setmPageLoadingProgressDrawable(Drawable progressDrawable){
        if (progressDrawable != null){
            mPageLoadingProgressBar.setProgressDrawable(progressDrawable);
        }
    }
    protected abstract String webUrl();

    protected abstract void imageLongClick();

    public void setJavaScriptInterface(X5SimpleJavaScriptFunction scriptInterface, String jsTag) {
        webView.addJavascriptInterface(scriptInterface, jsTag);
    }


    @Override
    protected void onResume() {
        super.onResume();
        webView.loadUrl(webUrl());
//        MobclickAgent.onResume(X5WebRefreshActivity.this);
    }


    /**
     * Title: onPause
     * Description:添加友盟统计
     *
     * @see Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
//        Logger.i(TAG, "暂停");
//        MobclickAgent.onPause(X5WebRefreshActivity.this);
    }

    /**
     * Title: onDestroy
     * Description:销毁activity，取消中止线程消息等异步执行
     *
     * @see Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Logger.i(TAG, "销毁");
    }
}
