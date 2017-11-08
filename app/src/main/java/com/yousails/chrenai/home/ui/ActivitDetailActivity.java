package com.yousails.chrenai.home.ui;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
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
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.bean.EnrollBean;
import com.yousails.chrenai.login.bean.UserBean;
import com.yousails.chrenai.login.ui.AuthActivity;
import com.yousails.chrenai.login.ui.LoginActivity;
import com.yousails.chrenai.person.ui.PersonActivity;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.CancelAddDialog;
import com.yousails.chrenai.view.CheckCertifDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 活动详情页
 * Created by Administrator on 2017/7/7.
 */

public class ActivitDetailActivity extends BaseActivity {
    private UMShareListener mShareListener;
    private ShareAction mShareAction;
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private LinearLayout bottomLayout;
    private LinearLayout consultLayout;
    private LinearLayout enrollLayout;
    private TextView enrollView;
    private TextView titleView;
    private WebView webView;
    private ProgressBar progressBar;
    private String aId = "";
    private String title = "";
    private String coverImg="";
    private String uId;
    private String url;
    private ActivitiesBean activitiesBean;
    boolean isLogin;
    private UserBean userBean = null;
    private CheckCertifDialog checkCertifDialog;
    private CancelAddDialog cancelAddDialog;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_activities_detail);
        // 注册对象
        EventBus.getDefault().register(this);
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        if (intent != null) {
            url = intent.getStringExtra("url");
            activitiesBean = (ActivitiesBean) intent.getSerializableExtra("bean");
            if (activitiesBean != null) {
                userBean = activitiesBean.getUser();
                aId = activitiesBean.getId();
                title = activitiesBean.getTitle();
                coverImg=activitiesBean.getCover_image();
                uId=activitiesBean.getUser_id();
            }
        }
    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText("活动详情");

        sharedLayout = (LinearLayout) findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.INVISIBLE);
        TextView sharedView = (TextView) findViewById(R.id.txt_more);
        sharedView.setText("分享");

        bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        consultLayout = (LinearLayout) findViewById(R.id.consult_layout);
        enrollLayout = (LinearLayout) findViewById(R.id.enroll_layout);
        enrollView = (TextView) findViewById(R.id.enroll_tview);

        progressBar = (ProgressBar) findViewById(R.id.pbar_loading);
//        progressBar.setIndeterminate(true);

        webView = (WebView) findViewById(R.id.webview);
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
//        webSetting.setSupportZoom(true);
//        webSetting.setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebViewClient(client);
        webView.setWebChromeClient(chromeClient);
        webView.loadUrl(url);

        initShareBoard();

        webView.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);


    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        sharedLayout.setOnClickListener(this);

        consultLayout.setOnClickListener(this);
        enrollLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.title_back:
                ActivitDetailActivity.this.finish();
                break;
            case R.id.btn_more:
                ShareBoardConfig config = new ShareBoardConfig();
                config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
                config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR); // 圆角背景
                config.setIndicatorVisibility(false);//隐藏指示器
                config.setTitleVisibility(false); // 隐藏title
                config.setCancelButtonText("关闭");// 设置取消按钮文本内容
//                  config.setCancelButtonVisibility(false); // 隐藏取消按钮
                mShareAction.open(config);

                break;
            case R.id.consult_layout:
                //先判断是否已登录
                isLogin = AppPreference.getInstance(mContext).readLogin();
                if (isLogin) {
                    Intent intent2 = new Intent(mContext, ChatActivity.class);
                    if (userBean != null) {
                        intent2.putExtra("userId", userBean.getIm_username());
                        intent2.putExtra("nickname", userBean.getName());
                    } else {
                        intent2.putExtra("userId", "");
                    }
                    intent2.putExtra("chatType", 1);
                    startActivity(intent2);
                } else {
//                    CustomToast.createToast(mContext, "请先登录");
                    navToLogin();
                }

                break;
            case R.id.enroll_layout:
                if (isLogin) {
                    if (activitiesBean != null) {
                        String enrollName = enrollView.getText().toString();
                        if ("我要报名".equals(enrollName)) {
                            Intent intent = new Intent(mContext, EnrollActivity.class);
                            intent.putExtra("add", "0");
                            intent.putExtra("activitiesBean", activitiesBean);
                            startActivityForResult(intent, 0);
                        } else if ("追加报名".equals(enrollName)) {
                            Intent intent = new Intent(mContext, EnrollActivity.class);
                            intent.putExtra("add", "1");
                            intent.putExtra("activitiesBean", activitiesBean);
                            startActivityForResult(intent, 0);
                        } else if ("取消报名".equals(enrollName)) {
                            boolean isStart = activitiesBean.is_started();
                            boolean isFinished = activitiesBean.is_finished();

                            if (!isFinished) {
                                if (isStart) {
                                    CustomToast.createToast(mContext, "活动已开始，不可取消报名");
                                } else {
                                    Intent intent = new Intent(mContext, DeleteEnrollActivity.class);
                                    intent.putExtra("id", activitiesBean.getId());
                                    startActivityForResult(intent, 1);

                                }
                            }

                        } else if ("取消追加".equals(enrollName)) {
                            showCancleDialog();
                        }
                    }
                } else {
                    navToLogin();
                }
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
                    case 0:
                        progressBar.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                        bottomLayout.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        progressBar.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                        bottomLayout.setVisibility(View.VISIBLE);

                        //判断显示的文字
                        if (activitiesBean != null) {
                            EnrollBean enrollBean = activitiesBean.getUserApplication();
                            if (enrollBean != null) {
                                String status = enrollBean.getStatus();
                                boolean isAdd = enrollBean.is_additional();
                                if ("passed".equals(status)) {
                                    enrollView.setText("取消报名");
                                } else if ("applied".equals(status)) {
                                    if (isAdd) {
                                        enrollView.setText("取消追加");
                                    } else {
                                        enrollView.setText("取消报名");
                                    }
                                } else if ("rejected ".equals(status) || "deleted".equals(status)) {
                                    //我要报名 置灰色
                                    enrollView.setText("我要报名");
                                    enrollLayout.setBackgroundColor(getResources().getColor(R.color.enroll_gray_color));
                                    enrollLayout.setEnabled(false);
                                }

                            } else {
                                int limit = activitiesBean.getLimit();

                                if (0 == limit) {
                                    enrollView.setText("我要报名");
                                } else {
                                    int total = activitiesBean.getApplication_total();
                                    if (total < limit) {
                                        enrollView.setText("我要报名");
                                    } else {
                                        enrollView.setText("追加报名");
                                    }
                                }
                            }

                        }
                        break;
                    case 2:
                        CustomToast.createToast(mContext, "取消报名成功");
                        refreshWebview();
                        break;
                }
            }
        };

    }

    @Override
    public void initData() {
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initView(){
        if (activitiesBean != null) {

            isLogin = AppPreference.getInstance(mContext).readLogin();

            if (!isLogin) {
                bottomLayout.setVisibility(View.VISIBLE);
                return;
            }

            //获取用户实名认证信息,后期通过推送方式判断
            getAuth();

            String curentId = activitiesBean.getUser().getId();
            String userId = AppPreference.getInstance(mContext).readUerId();

            boolean isFinished = activitiesBean.is_finished();

            if(TextUtils.isEmpty(curentId) || TextUtils.isEmpty(userId)){
                return;
            }

            if (isFinished || curentId.equals(userId)) {
                bottomLayout.setVisibility(View.GONE);
                return;
            }

            //获取用户报名信息
            getUserApplication();

        }
    }




    private WebViewClient client = new WebViewClient() {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            webView.loadData("页面请求失败,请检查网络是否异常!",
                    "text/html; charset=UTF-8", null);

        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if ("chrenai://goToMap".equals(url)) {
                Intent intent = new Intent(ActivitDetailActivity.this, AddressActivity.class);
                intent.putExtra("coordinate", activitiesBean.getCoordinate());
                intent.putExtra("address", activitiesBean.getAddress());
                intent.putExtra("id", aId);
                startActivity(intent);
            } else if ("chrenai://goToUser".equals(url)) {
                isLogin = AppPreference.getInstance(mContext).readLogin();
                if (isLogin) {
                    Intent intent = new Intent(mContext, PersonActivity.class);
                    intent.putExtra("user", userBean);
                    startActivity(intent);
                } else {
                    navToLogin();
                }

            } else if ("chrenai://goToPostComment".equals(url)) {
                checkCertif("show");

            } else if ("chrenai://goToComments".equals(url)) {
                checkCertif("hide");
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
                webView.setVisibility(View.VISIBLE);
                sharedLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                webView.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }
    };


    /**
     * 是否实名认证
     */
    private void checkCertif(String type) {
        boolean isLogin = AppPreference.getInstance(mContext).readLogin();
        String certification = AppPreference.getInstance(mContext).readCertification();
        String userId = AppPreference.getInstance(mContext).readUerId();
        String status = AppPreference.getInstance(mContext).readStatus(userId);
        if (isLogin) {
            if (StringUtil.isNotNull(certification) && "1".equals(certification)) {
                if ("show".equals(type)) {
                    Intent intent = new Intent(ActivitDetailActivity.this, CommentsAcitivity.class);
                    intent.putExtra("id", aId);
                    intent.putExtra("flag", "show");
                    intent.putExtra("userId", uId);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ActivitDetailActivity.this, CommentsAcitivity.class);
                    intent.putExtra("id", aId);
                    intent.putExtra("flag", "hide");
                    intent.putExtra("userId", uId);
                    startActivity(intent);
                }
            } else {
                if (StringUtil.isNotNull(status)) {
                    if ("applied".equals(status)) {

                        CustomToast.createToast(mContext, "审核中");

                    } else if ("rejected".equals(status)) {
                        showDialog();
                    } else {
                        if ("show".equals(type)) {
                            Intent intent = new Intent(ActivitDetailActivity.this, CommentsAcitivity.class);
                            intent.putExtra("id", aId);
                            intent.putExtra("flag", "show");
                            intent.putExtra("userId", uId);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ActivitDetailActivity.this, CommentsAcitivity.class);
                            intent.putExtra("id", aId);
                            intent.putExtra("flag", "hide");
                            intent.putExtra("userId", uId);
                            startActivity(intent);
                        }
                    }
                } else {
                    showDialog();
                }

            }
        } else {
            navToLogin();
        }

    }


    private void showDialog() {
        if (checkCertifDialog == null) {
            checkCertifDialog = new CheckCertifDialog(mContext,"detail", onClickListener);
        }
        checkCertifDialog.show();
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_submit:
                    checkCertifDialog.dismiss();

                    Intent intent = new Intent(mContext, AuthActivity.class);
                    startActivity(intent);
//                    finish();
                    break;

            }
        }
    };


    private void showCancleDialog() {
        if (cancelAddDialog == null) {
            cancelAddDialog = new CancelAddDialog(mContext, onCancleClickListener);
        }
        cancelAddDialog.show();
    }

    View.OnClickListener onCancleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cancel_btn:
                    cancelAddDialog.dismiss();
                    deleteEnroll();
                    break;

            }
        }
    };


    /**
     * 初始化友盟分享面板
     */
    private void initShareBoard() {
        mShareListener = new CustomShareListener(this);
        /*增加自定义按钮的分享面板*/
        mShareAction = new ShareAction(ActivitDetailActivity.this).setDisplayList(
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
                            Toast.makeText(ActivitDetailActivity.this, "已复制链接到粘贴板", Toast.LENGTH_LONG).show();
                            shared("clipboard");
                        } else if (share_media == SHARE_MEDIA.SMS) {
                            UMWeb web = new UMWeb(url);
                            web.setTitle(title);
                            web.setDescription(title);
                            new ShareAction(ActivitDetailActivity.this).withMedia(web)
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener)
                                    .share();
                            shared("sms");
                        } else {
                            UMWeb web = new UMWeb(url);
                            web.setTitle(title);
                            web.setDescription(title);
                            web.setThumb(new UMImage(ActivitDetailActivity.this, coverImg));
                            new ShareAction(ActivitDetailActivity.this).withMedia(web)
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener)
                                    .share();

                            String platform = "";
                            if (share_media == SHARE_MEDIA.WEIXIN_CIRCLE || share_media == SHARE_MEDIA.WEIXIN) {
                                platform = "weixin";
                            } else if (share_media == SHARE_MEDIA.QQ || share_media == SHARE_MEDIA.QZONE) {
                                platform = "qq";
                            } else if (share_media == SHARE_MEDIA.SINA) {
                                platform = "weibo";
                            }
                            shared(platform);
                        }

                    }
                });
    }


    private static class CustomShareListener implements UMShareListener {

        private WeakReference<ActivitDetailActivity> mActivity;

        private CustomShareListener(ActivitDetailActivity activity) {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/

        if (1 == resultCode) {
            if (data != null) {
                // passed 审核通过，rejected 审核拒绝，applied 审核中。
                String status = data.getStringExtra("status");
//                boolean isAdd = data.getBooleanExtra("isAdd", false);

                 refreshWebview();

            }
        } else if (2 == resultCode) {
            refreshWebview();

        } else {
            UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        }

    }

    /**
     * 取消报名后刷新
     */
    private void refreshWebview() {

        if (url != null) {
            webView.loadUrl(url);
        }
        getUserApplication();
    }

    /**
     * 屏幕横竖屏切换时避免出现window leak的问题
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mShareAction.close();
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/


    /**
     * 用户报名信息
     */
    private void getUserApplication() {

        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isEmpty(aId)) return;
        String url = ApiConstants.GET_COMMENTS_API + aId + "?include=userApplication";

        String mToken = AppPreference.getInstance(mContext).readToken();
        OkHttpUtils.get().url(url).addHeader("Authorization", mToken).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Type type = new TypeToken<ActivitiesBean>() {
                        }.getType();
                        activitiesBean = new Gson().fromJson(jsonObject.toString(), type);

                        mHandler.sendEmptyMessage(1);

                    } catch (Exception e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(0);
                    }

                }

            }
        });
    }

    /**
     * 跳转到登录界面
     */
    private void navToLogin() {
        Intent loginIntent = new Intent(mContext, LoginActivity.class);
        loginIntent.putExtra("from", "detail");
        startActivity(loginIntent);
    }


    /**
     * 分享
     */
    private void shared(String platform) {
        if (StringUtil.isEmpty(aId)) {
            return;
        }
        String mToken = AppPreference.getInstance(mContext).readToken();

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("id", aId);
        paramMap.put("platform", platform);

        OkHttpUtils.post().url(ApiConstants.POST_SHARED_API + aId).addHeader("Authorization", mToken).params(paramMap).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                System.out.print(response);
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.print(response);
            }
        });

    }


    /**
     * 获取实名认证信息
     */
    private void getAuth() {
        boolean isLogin = AppPreference.getInstance(mContext).readLogin();
        if (!isLogin) {
            return;
        }

        if (!NetUtil.detectAvailable(mContext)) {
            //   Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        String mToken = AppPreference.getInstance(mContext).readToken();

        OkHttpUtils.get().url(ApiConstants.SUBMIT_CERTIFICATION_API).addHeader("Authorization", mToken).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONObject reJson = jsonObject.optJSONObject("religion");
                        if (reJson != null) {
                            AppPreference.getInstance(mContext).writeReligion(reJson.optString("name"));
                        }

                        //applied: 已申请, passed: 已通过, rejected: 已拒绝
                        String status = jsonObject.optString("status");
                        if ("passed".equals(status)) {
                            AppPreference.getInstance(mContext).writeCertification("1");
                        } else {
                            AppPreference.getInstance(mContext).writeCertification("");
                        }

                        String userId = AppPreference.getInstance(mContext).readUerId();
                        AppPreference.getInstance(mContext).writeStatus(userId, status);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }


    /**
     * 取消报名
     */
    private void deleteEnroll() {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtil.isEmpty(aId)) return;

        String url = ApiConstants.GET_COMMENTS_API + aId + "/user/application";
        String mToken = AppPreference.getInstance(mContext).readToken();
        RequestBody requestBody = new FormBody.Builder().add("id", aId).build();
        OkHttpUtils.delete().url(url).addHeader("Authorization", mToken).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                CustomToast.createToast(mContext, "取消报名失败");
            }

            @Override
            public void onResponse(String response, int id) {
                mHandler.sendEmptyMessage(2);
            }
        });
    }


    /**
     * 发布评论或是删除评论后刷新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateView(String param) {
        if("refreshwebview".equals(param)){
            if (url != null) {
                webView.loadUrl(url);
            }
        }

    }

    /**
     * 登录成功后刷新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateWebView(String param) {
        if("initwebview".equals(param)){
            initView();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}