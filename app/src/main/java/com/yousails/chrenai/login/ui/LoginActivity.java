package com.yousails.chrenai.login.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.app.ui.MainActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.db.UserDBService;
import com.yousails.chrenai.framework.util.GsonUtils;
import com.yousails.chrenai.login.bean.LoginResultBean;
import com.yousails.chrenai.login.bean.MetaBean;
import com.yousails.chrenai.login.bean.UserBean;
import com.yousails.chrenai.login.listener.MaxLengthWatcher;
import com.yousails.chrenai.login.listener.TextChangeListener;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.LogUtils;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.NoDoubleClickUtils;
import com.yousails.chrenai.utils.PhoneUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.CommonDialog;
import com.yousails.chrenai.view.ValidateDialog;
import com.yousails.chrenai.view.WXLoginDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/6/16.
 */

public class LoginActivity extends BaseActivity {
    public static final int MSG_RECEIVED_CODE = 10;
    private ValidateDialog validateDialog;

    private LinearLayout closedLayout;//关闭
    private LinearLayout regLayout;//注册
    private TextView titleView;//标题

    private EditText phoneEditText;//手机号
    private EditText pwdEditText;//验证码
    private Button validateBtn;//获取验证码
    private TextView tipsTextView;//错误提示

    private Button loginBtn;
    private LinearLayout wxLoginLayout;//微信登录
    private String phoneNum;
    private String password;
    private String url;
    private String captchaKey;
    private String smsCode;
    private int time = 0;
    private boolean b;
    private String from;
    private Intent loginIntent;

    private ProgressDialog dialog;
    private WXLoginDialog wxLoginDialog;
    private CommonDialog commonDialog;


    private static String mToken;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        fetchAuthResultWithBundle(savedInstanceState);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void init() {
        loginIntent = getIntent();
        if (loginIntent != null) {
            from = loginIntent.getStringExtra("from");
        }

        // 注册对象
        EventBus.getDefault().register(this);

    }

    @Override
    protected void findViews() {
        closedLayout = (LinearLayout) findViewById(R.id.layout_close);
        regLayout = (LinearLayout) findViewById(R.id.layout_register);
        titleView = (TextView) findViewById(R.id.title);

        phoneEditText = (EditText) findViewById(R.id.tv_phone);
        pwdEditText = (EditText) findViewById(R.id.tv_pwd);

        validateBtn = (Button) findViewById(R.id.iv_validate);
        tipsTextView = (TextView) findViewById(R.id.tv_error_tips);

        loginBtn = (Button) findViewById(R.id.login_btn_submit);
        wxLoginLayout = (LinearLayout) findViewById(R.id.wx_login_layout);

        dialog = new ProgressDialog(this);
        wxLoginDialog = new WXLoginDialog(this);

        phoneEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(phoneEditText, 0);

            }
        }, 500);

    }

    @Override
    protected void setListeners() {
        closedLayout.setOnClickListener(this);
        regLayout.setOnClickListener(this);
        validateBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        wxLoginLayout.setOnClickListener(this);

        phoneEditText.addTextChangedListener(new MaxLengthWatcher(11, phoneEditText, phoneChangeListener));
        pwdEditText.addTextChangedListener(new MaxLengthWatcher(6, pwdEditText, pwdChangeListener));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_close:
                if (StringUtil.isNotNull(from) && ("main".equals(from) || "detail".equals(from) || "publish".equals(from))) {
                    finish();
                } else {
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
                break;
            case R.id.layout_register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_validate:
                if (checkPhoneNum()) {
                    getImageViewCode(true);
                }

                break;
            case R.id.login_btn_submit:
                //防止连续点击
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    getToken();
                }

                break;
            case R.id.wx_login_layout:
                if (isWeixinAvilible(mContext)) {
                    boolean isauth = UMShareAPI.get(mContext).isAuthorize(LoginActivity.this, SHARE_MEDIA.WEIXIN);
                    if (!isauth) {
                        UMShareAPI.get(mContext).doOauthVerify(LoginActivity.this, SHARE_MEDIA.WEIXIN, authListener);
                    } else {
                        //   UMShareAPI.get(mContext).deleteOauth(LoginActivity.this, SHARE_MEDIA.WEIXIN, authListener);
                        getPlatformInfo();
                    }
                } else {
                    Toast.makeText(mContext, "您还没有安装微信，请先安装微信客户端", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_validate_code:
                    if (validateDialog != null) {
                        validateDialog.getImgeView().setVisibility(View.INVISIBLE);
                        validateDialog.getProgressBar().setVisibility(View.VISIBLE);
                    }

                    getImageViewCode(false);

                    break;
                case R.id.tv_submit:
                    getSmsCode();
                    break;

            }
        }
    };

    @Override
    protected void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        tipsTextView.setText("请求失败!");
                        Toast.makeText(mContext, "获取验证码失败!", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Bundle bundle = msg.getData();
                        if (bundle != null) {
                            captchaKey = bundle.getString("key");
                            url = bundle.getString("url");
                            String expired = bundle.getString("expired");
                        }
                        if (validateDialog == null) {
                            validateDialog = new ValidateDialog(mContext, onClickListener);
                        } else {
                            validateDialog.getImgeView().setVisibility(View.VISIBLE);
                            validateDialog.getProgressBar().setVisibility(View.GONE);
                        }

                        validateDialog.show();

                        final EditText codeEditText = validateDialog.getCodeEditText();

                        codeEditText.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(codeEditText, 0);

                            }
                        }, 500);

                        Glide.with(mContext)
                                .load(url)
                                .into(validateDialog.getImgeView());

                        break;
                    case 2:
                        if (validateDialog != null) {
                            validateDialog.getImgeView().setVisibility(View.VISIBLE);
                            validateDialog.getProgressBar().setVisibility(View.GONE);
                        }

                        Bundle bundle1 = msg.getData();
                        String message = bundle1.getString("msg");
                        tipsTextView.setText(message);
                        phoneEditText.setTextColor(ContextCompat.getColor(mContext, R.color.login_red_color));
                        break;
                    case 3:
                        Bundle bundle2 = msg.getData();
                        validateBtn.setEnabled(true);
                        validateBtn.setBackgroundResource(R.drawable.login_validate_btn_normal);
                        validateDialog.dismiss();
                        tipsTextView.setText(bundle2.getString("message"));
                        break;
                    case 4:
                        time++;
                        if (time >= 60) {
                            time = 0;
                            validateBtn.setText(R.string.send_again);
                            validateBtn.setEnabled(true);
                            validateBtn.setBackgroundResource(R.drawable.login_validate_btn_normal);
                        } else {
                            validateBtn.setText(getShowTime());
                            mHandler.sendEmptyMessageDelayed(4, 1000);
//                            validateBtn.setEnabled(false);
//                            validateBtn.setBackgroundResource(R.drawable.login_validate_btn);
                        }
                        break;
                    case 5:
                        if (wxLoginDialog != null) {
                            wxLoginDialog.dismiss();
                        }
                        Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                        Bundle bundle3 = msg.getData();
                        String message1 = bundle3.getString("msg");
                        tipsTextView.setText(message1);
                        pwdEditText.setTextColor(ContextCompat.getColor(mContext, R.color.login_red_color));
                        break;
                    case 6:
                        if (wxLoginDialog != null) {
                            wxLoginDialog.dismiss();
                        }
                        Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                        navToHome();
                        break;
                    case MSG_RECEIVED_CODE:
                        smsCode = (String) msg.obj;
                        break;
                }
            }
        };
    }

    @Override
    public void initData() {

    }

    TextChangeListener phoneChangeListener = new TextChangeListener() {
        @Override
        public void updateView(Editable editable) {
            int len = editable.length();
            phoneEditText.setTextColor(ContextCompat.getColor(mContext, R.color.light_black_color));
            tipsTextView.setText("");
            if (len >= 11) {
                validateBtn.setEnabled(true);
                validateBtn.setBackgroundResource(R.drawable.login_validate_btn_normal);
            } else {
                validateBtn.setEnabled(false);
                validateBtn.setBackgroundResource(R.drawable.login_validate_btn);
            }
            checkText();
        }
    };

    TextChangeListener pwdChangeListener = new TextChangeListener() {
        @Override
        public void updateView(Editable editable) {

            pwdEditText.setTextColor(ContextCompat.getColor(mContext, R.color.light_black_color));
            checkText();

        }
    };

    private boolean checkPhoneNum() {
        phoneNum = phoneEditText.getText().toString().trim();
        if (StringUtil.isEmpty(phoneNum)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!PhoneUtil.checkPhoneNumber(phoneNum)) {
            tipsTextView.setText(R.string.phone_error_tips);
            tipsTextView.setVisibility(View.VISIBLE);
            return false;
        }

        validateBtn.setBackgroundResource(R.drawable.login_validate_btn_normal);
        return true;
    }

    /**
     * 获取图片验证码
     */
    private void getImageViewCode(boolean isShow) {

        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
        }
        if (StringUtil.isEmpty(phoneNum)) {
            return;
        }
        if (isShow) {
            showDialog();
        }
        Map<String, String> params = new HashMap<>();
        params.put("phone", phoneNum);
        params.put("type", "login");

        OkHttpUtils.post().url(ApiConstants.GET_CAPTCHAS_API).params(params).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                if (commonDialog != null) {
                    commonDialog.dismiss();
                }

                if (StringUtil.isNotNull(response) && response.contains("message")) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        String message = jsonObj.optString("message");

                        Message mes = new Message();
                        mes.what = 2;
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", message);
                        mes.setData(bundle);
                        mHandler.sendMessage(mes);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, "获取验证码失败", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onResponse(String response, int id) {
                if (commonDialog != null) {
                    commonDialog.dismiss();
                }
                try {
                    JSONObject obj = new JSONObject(response);
                    String key = obj.optString("captcha_key");
                    String url = obj.optString("url");
                    String expired = obj.optString("expired_at");

                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("key", key);
                    bundle.putString("url", url);
                    bundle.putString("expired", expired);
                    message.what = 1;
                    message.setData(bundle);
                    mHandler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 获取短信验证码
     */
    private void getSmsCode() {
        if (validateDialog == null) {
            return;
        }
        String captchaCode = validateDialog.getCode();
        if (StringUtil.isEmpty(captchaCode)) {
            Toast.makeText(mContext, "请输入图片验证码！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
        }

        validateBtn.setEnabled(false);
        validateBtn.setBackgroundResource(R.drawable.login_validate_btn);


        Map<String, String> params = new HashMap<>();
        params.put("captcha_key", captchaKey);
        params.put("captcha_code", captchaCode);

        OkHttpUtils.post().url(ApiConstants.GET_VERIFICATION_CODES_API).params(params).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, String response, Exception e, int id) {
                if (StringUtil.isNotNull(response) && response.contains("message")) {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        String message = jsonObj.optString("message");

                        Message message1 = new Message();
                        message1.what = 3;
                        Bundle bundle = new Bundle();
                        bundle.putString("message", message);
                        message1.setData(bundle);
                        mHandler.sendMessage(message1);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponse(String response, int id) {
                CustomToast.createToast(mContext, "验证码已发送");
//                validateBtn.setEnabled(true);
                validateDialog.dismiss();
                mHandler.sendEmptyMessageDelayed(4, 1000);
            }
        });

    }


    /**
     * 获取Token(jwk)
     */
    private void getToken() {
        phoneNum = phoneEditText.getText().toString().trim();
        if (StringUtil.isEmpty(phoneNum)) {
            return;
        }

        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoginDialog();
        Map<String, String> params = new HashMap<>();
        params.put("phone", phoneNum);
        params.put("verification_code", password);
//        params.put("verification_code", "123456");
        OkHttpUtils.post()
                .url(ApiConstants.GET_AUTHORIZATIONS_API)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, String response, Exception e, int id) {
                        if (StringUtil.isNotNull(response) && response.contains("message")) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                String message = jsonObj.optString("message");

                                Message message1 = new Message();
                                message1.what = 5;
                                Bundle bundle = new Bundle();
                                bundle.putString("msg", message);
                                message1.setData(bundle);
                                mHandler.sendMessage(message1);

                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        if (!TextUtils.isEmpty(response)) {
                            LoginResultBean loginResultBean = GsonUtils.toBean(response, LoginResultBean.class);

                            if (loginResultBean != null) {
                                LogUtils.e("==loginResultBean==" + loginResultBean);
                                UserBean userBean = loginResultBean.getUser();

                                application.setUserBean(userBean);

                                //保存到sharepreference
                                AppPreference.getInstance(mContext).writeToken("Bearer " + loginResultBean.getToken());
                                AppPreference.getInstance(mContext).writeExpired(loginResultBean.getExpired_at());
                                AppPreference.getInstance(mContext).writeRefreshExpired(loginResultBean.getRefresh_expired_at());

                                AppPreference.getInstance(mContext).writeUserId(userBean.getId());
                                AppPreference.getInstance(mContext).writeLastUserId(userBean.getId());
                                AppPreference.getInstance(mContext).writeUserName(userBean.getName());
                                AppPreference.getInstance(mContext).writeRealName(userBean.getRealname());
                                AppPreference.getInstance(mContext).writePhone(userBean.getId(), userBean.getPhone());
                                AppPreference.getInstance(mContext).writeAvatar(userBean.getAvatar());
                                AppPreference.getInstance(mContext).writeGender(userBean.getSex());
                                AppPreference.getInstance(mContext).writeReligion(userBean.getReligion_name());
                                AppPreference.getInstance(mContext).writeLevel(userBean.getLevel());
                                AppPreference.getInstance(mContext).writeEMName(userBean.getIm_username());
                                String uid = AppPreference.getInstance(mContext).readUerId();
                                AppPreference.getInstance(mContext).writePhone(uid, userBean.getPhone());

                                if (userBean.is_certificated()) {
                                    AppPreference.getInstance(mContext).writeCertification("1");
                                }

                                if (userBean.is_vip()) {
                                    AppPreference.getInstance(mContext).writeIsVip("1");
                                }

                                int hours = userBean.getWorking_hours();
                                if (0 != hours) {
                                    AppPreference.getInstance(mContext).writeWorkHours(hours + "");
                                }


                                AppPreference.getInstance(mContext).setLogin(true);
                                AppPreference.getInstance(mContext).setLogout(false);

                                MetaBean metaBean = loginResultBean.getMeta();
                                if (metaBean != null) {
                                    AppPreference.getInstance(mContext).writeEMPwd(metaBean.getIm_password());

                                }

                                //保存到数据库
                                UserDBService userDBService = new UserDBService(mContext);
                                if (userDBService.hasUser(userBean.getId())) {
                                    userDBService.updateUser(userBean);
                                } else {
                                    userDBService.insertUser(userBean);
                                }

                                login_Em();

                            }

                        }

                    }
                });

    }


    /**
     * 检查输入框是否为空
     */
    private void checkText() {
        phoneNum = phoneEditText.getText().toString().trim();
        password = pwdEditText.getText().toString().trim();
        if (StringUtil.isNotNull(phoneNum) && StringUtil.isNotNull(password)) {
            b = true;
        } else {
            b = false;
        }
        if (b) {
            loginBtn.setEnabled(true);
            loginBtn.setBackgroundResource(R.drawable.login_btn_bg);
        } else {
            loginBtn.setEnabled(false);
            loginBtn.setBackgroundResource(R.drawable.login_btn_normal);
        }
    }

    private String getShowTime() {
        int remain = 60 - time;
        return remain + "秒";
    }


    /**
     * 第三方微信登录获取用户资料
     */
    private void getPlatformInfo() {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoginDialog();
        Map<String, String> params = new HashMap<String, String>() {
        };
        params.put("type", "weixin");
        String wxToken = AppPreference.getInstance(mContext).readWXToken();
        params.put("access_token", AppPreference.getInstance(mContext).readWXToken());
        String opeid = AppPreference.getInstance(mContext).readWXOpenId();
        params.put("open_id", AppPreference.getInstance(mContext).readWXOpenId());

        OkHttpUtils.post()
                .url(ApiConstants.LOGIN_OAUTH_API)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, String response, Exception e, int id) {
                        if (wxLoginDialog != null) {
                            wxLoginDialog.dismiss();
                        }
                        Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        if(wxLoginDialog!=null){
//                            wxLoginDialog.dismiss();
//                        }
                        if (StringUtil.isNotNull(response)) {
                            try {
                                LoginResultBean loginResultBean = new Gson().fromJson(response, LoginResultBean.class);

                                if (loginResultBean != null) {

                                    UserBean userBean = loginResultBean.getUser();
                                    application.setUserBean(userBean);

                                    //保存到sharepreference
                                    AppPreference.getInstance(mContext).writeToken("Bearer " + loginResultBean.getToken());
                                    AppPreference.getInstance(mContext).writeExpired(loginResultBean.getExpired_at());
                                    AppPreference.getInstance(mContext).writeRefreshExpired(loginResultBean.getRefresh_expired_at());

                                    AppPreference.getInstance(mContext).writeUserId(userBean.getId());
                                    AppPreference.getInstance(mContext).writeLastUserId(userBean.getId());
                                    AppPreference.getInstance(mContext).writeUserName(userBean.getName());
                                    AppPreference.getInstance(mContext).writeRealName(userBean.getRealname());
                                    AppPreference.getInstance(mContext).writePhone(userBean.getId(), userBean.getPhone());
                                    AppPreference.getInstance(mContext).writeAvatar(userBean.getAvatar());
                                    AppPreference.getInstance(mContext).writeGender(userBean.getSex());
                                    AppPreference.getInstance(mContext).writeReligion(userBean.getReligion_name());
                                    AppPreference.getInstance(mContext).writeLevel(userBean.getLevel());
                                    AppPreference.getInstance(mContext).writeEMName(userBean.getIm_username());
                                    String uid = AppPreference.getInstance(mContext).readUerId();
                                    AppPreference.getInstance(mContext).writePhone(uid, userBean.getPhone());


                                    if (userBean.is_certificated()) {
                                        AppPreference.getInstance(mContext).writeCertification("1");
                                    }

                                    if (userBean.is_vip()) {
                                        AppPreference.getInstance(mContext).writeIsVip("1");
                                    }

                                    int hours = userBean.getWorking_hours();
                                    if (0 != hours) {
                                        AppPreference.getInstance(mContext).writeWorkHours(hours + "");
                                    }


                                    AppPreference.getInstance(mContext).setLogin(true);
                                    AppPreference.getInstance(mContext).setLogout(false);

                                    MetaBean metaBean = loginResultBean.getMeta();
                                    if (metaBean != null) {
                                        AppPreference.getInstance(mContext).writeEMPwd(metaBean.getIm_password());

                                    }

                                    //保存到数据库
                                    UserDBService userDBService = new UserDBService(mContext);
                                    if (userDBService.hasUser(userBean.getId())) {
                                        userDBService.updateUser(userBean);
                                    } else {
                                        userDBService.insertUser(userBean);
                                    }

                                    login_Em();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finish(String message) {
        this.finish();
    }


    //----------------------------------------------------


    UMAuthListener authListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

//            SocializeUtils.safeShowDialog(dialog);
//            SocializeUtils.safeShowDialog(wxLoginDialog);
        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

//            SocializeUtils.safeCloseDialog(dialog);
//            SocializeUtils.safeCloseDialog(wxLoginDialog);
            // Toast.makeText(mContext, "成功了", Toast.LENGTH_LONG).show();
            AppPreference.getInstance(mContext).writeWXToken(data.get("access_token"));
            AppPreference.getInstance(mContext).writeWXRefreshToken(data.get("refresh_token"));
            AppPreference.getInstance(mContext).writeWXOpenId(data.get("openid"));

            getPlatformInfo();
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {

//            SocializeUtils.safeCloseDialog(dialog);
//            SocializeUtils.safeCloseDialog(wxLoginDialog);
//            Toast.makeText(mContext, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {

//            SocializeUtils.safeCloseDialog(dialog);
//            SocializeUtils.safeCloseDialog(wxLoginDialog);
//            Toast.makeText(mContext, "取消了", Toast.LENGTH_LONG).show();
        }
    };


    /**
     * 对于的低端手机可能会有如下问题，从开发者app调到qq或者微信的授权界面，后台把开发者app杀死了，这样，授权成功没有回调了
     */
    private void fetchAuthResultWithBundle(Bundle savedInstanceState) {
        UMShareAPI.get(this).fetchAuthResultWithBundle(this, savedInstanceState, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {

//                SocializeUtils.safeShowDialog(dialog);
//                SocializeUtils.safeShowDialog(wxLoginDialog);
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                Toast.makeText(getApplicationContext(), "onRestoreInstanceState Authorize succeed", Toast.LENGTH_SHORT).show();

//                SocializeUtils.safeCloseDialog(dialog);
//                SocializeUtils.safeCloseDialog(wxLoginDialog);
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText(getApplicationContext(), "onRestoreInstanceState Authorize onError", Toast.LENGTH_SHORT).show();

//                SocializeUtils.safeCloseDialog(dialog);
//                SocializeUtils.safeCloseDialog(wxLoginDialog);
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText(getApplicationContext(), "onRestoreInstanceState Authorize onCancel", Toast.LENGTH_SHORT).show();

//                SocializeUtils.safeCloseDialog(dialog);
//                SocializeUtils.safeCloseDialog(wxLoginDialog);
            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        UMShareAPI.get(this).onSaveInstanceState(outState);
    }


    /**
     * 登录环信
     */
    private void login_Em() {
        String emName = AppPreference.getInstance(mContext).readEMName();
        String emPwd = AppPreference.getInstance(mContext).readEMPwd();
        if (StringUtil.isEmpty(emName) || StringUtil.isEmpty(emPwd)) {
            return;
        }

        final long start = System.currentTimeMillis();
        // call login method
        Log.d(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(emName, emPwd, new EMCallBack() {

            @Override
            public void onSuccess() {
//                Log.d(TAG, "login: onSuccess");
                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                mHandler.sendEmptyMessage(6);


            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);
                mHandler.sendEmptyMessage(6);

            }
        });
    }


    /**
     * 判断 用户是否安装微信客户端
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 强制显示软键盘
     */
    private void showKeyboard() {
        phoneEditText.setFocusable(true);
        phoneEditText.setFocusableInTouchMode(true);
        phoneEditText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(phoneEditText, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 强制隐藏软键盘
     */
    private void hiddenKeyboard() {
        phoneEditText.setText("");
        phoneEditText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(phoneEditText.getWindowToken(), 0);
    }


    /**
     * 登录Dialog
     */
    private void showLoginDialog() {
        if (wxLoginDialog == null) {
            wxLoginDialog = new WXLoginDialog(this);
        }
        wxLoginDialog.show();
    }


    /**
     * 获取图片验证码Dialog
     */
    private void showDialog() {
        if (commonDialog == null) {
            commonDialog = new CommonDialog(this);
        }
        commonDialog.show();
    }

    /**
     * 跳转到主界面
     */
    private void navToHome() {
        if (loginIntent != null) {
            from = loginIntent.getStringExtra("from");
            if ("detail".equals(from)) {
                //关掉登录页面
                EventBus.getDefault().post("initwebview");
                finish();
            } else if ("main".equals(from)) {
                //切换到我的页面
//            EventBus.getDefault().post("mypage");
                finish();
            } else if ("publish".equals(from)) {
                //切换到我的页面
                EventBus.getDefault().post("publish");
                finish();
            } else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                //关掉登录页面及注册页面
                EventBus.getDefault().post("finish");
            }
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            //关掉登录页面及注册页面
            EventBus.getDefault().post("finish");
        }
    }
}
