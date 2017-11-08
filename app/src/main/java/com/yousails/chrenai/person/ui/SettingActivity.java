package com.yousails.chrenai.person.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.SocializeUtils;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.app.ui.MainActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.config.ModelApplication;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.GlideCatchUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.ClearCacheDialog;
import com.yousails.chrenai.view.ClearProgressDialog;
import com.yousails.chrenai.view.LogoutDialog;
import com.yousails.chrenai.view.WXLoginDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/8/10.
 */

public class SettingActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private RelativeLayout accountInforLayout;
    private RelativeLayout aboutUsLayout;
    private RelativeLayout feedbackLayout;
    private RelativeLayout clearCacheLayout;
    private Button submitBtn;
    private TextView accountView;
    private TextView titleView;
    private ClearCacheDialog clearCacheDialog;
    private LogoutDialog logoutDialog;
    private ClearProgressDialog progressDialog;
    private boolean isLogin;


    @Override
    protected void setContentView() {

      setContentView(R.layout.activity_setting);
    }

    @Override
    protected void init() {
        // 注册对象
        EventBus.getDefault().register(this);
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("设置");

        sharedLayout=(LinearLayout)findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.INVISIBLE);

        accountInforLayout=(RelativeLayout)findViewById(R.id.account_infor_layout);
        accountView=(TextView)findViewById(R.id.account_tview);
        aboutUsLayout=(RelativeLayout)findViewById(R.id.about_us_layout);
        feedbackLayout=(RelativeLayout)findViewById(R.id.feedback_layout);
        clearCacheLayout=(RelativeLayout)findViewById(R.id.clear_cache_layout);

        submitBtn=(Button)findViewById(R.id.exit_btn_submit);


        isLogin=AppPreference.getInstance(mContext).readLogin();
        if(isLogin){
            submitBtn.setText("退出");
            initPhoneView();
        }else{
            submitBtn.setText("登录");
        }

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        accountInforLayout.setOnClickListener(this);
        aboutUsLayout.setOnClickListener(this);
        feedbackLayout.setOnClickListener(this);
        clearCacheLayout.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.account_infor_layout:
                if(isLogin){
                    Intent intent=new Intent(mContext,UpatePhoneActivity.class);
                    startActivity(intent);
                }else{
                    CustomToast.createToast(mContext,"请先登录");
                }
                break;
            case R.id.about_us_layout:
                Intent aboutIntent=new Intent(mContext,AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.feedback_layout:
                Intent feedbackIntent=new Intent(mContext,FeedbackActivity.class);
                startActivity(feedbackIntent);
                break;
            case R.id.clear_cache_layout:
                clearCache();
                break;
            case R.id.exit_btn_submit:
                logoutDialog();
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
                        clearUserInfor();
                        //跳转到首页
                        ModelApplication application = (ModelApplication) getApplication();
                        application.getActivityManager().popAllActivity();
                  //    application.getActivityManager().popAllActivityExceptOne(SettingActivity.class);

                        CustomToast.createToast(mContext,"退出成功");
                        Intent intent=new Intent(mContext,MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                }
            }
        };
    }

    @Override
    public void initData() {

    }

    /**
     * 退出确认对话框
     */
    private void logoutDialog(){
        if(logoutDialog==null){
            logoutDialog=new LogoutDialog(mContext, onClickListener);
        }
        logoutDialog.show();
    }


    /**
     * 清除缓存
     */
    private void clearCache(){
        if (clearCacheDialog == null) {
            clearCacheDialog = new ClearCacheDialog(mContext, onClickListener);
        }
        clearCacheDialog.show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_submit:
                    clearCacheDialog.dismiss();
                    clearLoading();
                    if(GlideCatchUtil.getInstance().clearCacheMemory()){
                        CustomToast.createToast(mContext,"清理成功");
                    }
                    progressDialog.dismiss();

                    break;
                case R.id.quit_btn:
                    logoutDialog.dismiss();
                    logoutApp();
                    break;

            }
        }
    };

    private void clearLoading(){

        if(progressDialog==null){
            progressDialog= new ClearProgressDialog(this);
        }
        progressDialog.show();
    }

    /**
     * 退出登录
     */
    private void logoutApp(){
        logoutEM();
        deleted();
        logout();

    }


    /**
     * 更改手机号
     */
    private void initPhoneView(){
        String phoneNum=AppPreference.getInstance(mContext).readPhone(AppPreference.getInstance(mContext).readUerId());
        if(StringUtil.isNotNull(phoneNum)){
            String start=phoneNum.substring(0,3);
            String end=phoneNum.substring(7);
            accountView.setText(start+"****"+end);
            accountView.setTextColor(getResources().getColor(R.color.text_gray_color));
        }else{
            accountView.setText("手机未绑定");
            accountView.setTextColor(getResources().getColor(R.color.login_red_color));
        }
    }

    /**
     * 清空用户登录信息
     */
    private void clearUserInfor(){
        //保存到sharepreference
        String userId=AppPreference.getInstance(mContext).readUerId();
        AppPreference.getInstance(mContext).writePhone(userId,"");

        AppPreference.getInstance(mContext).writeUserId("");
        AppPreference.getInstance(mContext).writeLastUserId("");
        AppPreference.getInstance(mContext).writeUserName("");
        AppPreference.getInstance(mContext).writeRealName("");
        AppPreference.getInstance(mContext).writeAvatar("");
        AppPreference.getInstance(mContext).writeGender("male");
        AppPreference.getInstance(mContext).writeReligion("");
        AppPreference.getInstance(mContext).writeIsVip("");
        AppPreference.getInstance(mContext).writeCertification("");
        AppPreference.getInstance(mContext).writeWorkHours("");
        AppPreference.getInstance(mContext).writeLevel("");
        AppPreference.getInstance(mContext).writeEMName("");
        AppPreference.getInstance(mContext).writeEMPwd("");

        AppPreference.getInstance(mContext).setLogin(false);
        AppPreference.getInstance(mContext).setLogout(true);

        AppPreference.getInstance(mContext).writeToken("");
        AppPreference.getInstance(mContext).writeExpired("");
        AppPreference.getInstance(mContext).writeRefreshExpired("");

        AppPreference.getInstance(mContext).writeWXToken("");
        AppPreference.getInstance(mContext).writeWXOpenId("");
        AppPreference.getInstance(mContext).writeWXRefreshToken("");
    }


    /**
     * 退出环信
     */
    private void logoutEM(){
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e("ss","退出成功");
            }

            @Override
            public void onError(int i, String s) {
                Log.e("ss","退出失败"+s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * 删除授权
     *
     */
    private void deleted(){
        UMShareAPI.get(mContext).deleteOauth(SettingActivity.this, SHARE_MEDIA.WEIXIN, authListener);
    }

    UMAuthListener authListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

//             Toast.makeText(mContext, "成功了", Toast.LENGTH_LONG).show();
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
//            Toast.makeText(mContext, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {

//            Toast.makeText(mContext, "取消了", Toast.LENGTH_LONG).show();
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatePhone(String message) {
        if ("updatePhone".equals(message)) {
            initPhoneView();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销
        EventBus.getDefault().unregister(this);
    }


    /**
     * 退出登录
     */
    private void logout(){

//        if(!NetUtil.detectAvailable(mContext)){
//            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
//            return;
//        }

        String mToken= AppPreference.getInstance(mContext).readToken();
        OkHttpUtils.delete().url(ApiConstants.REFRESH_AUTHORIZATIONS_API).addHeader("Authorization", mToken).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
//                CustomToast.createToast(mContext,"退出失败");
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onResponse(String response, int id) {
//                CustomToast.createToast(mContext,"退出成功");
                mHandler.sendEmptyMessage(1);
            }
        });
    }
}
