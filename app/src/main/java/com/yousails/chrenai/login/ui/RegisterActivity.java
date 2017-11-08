package com.yousails.chrenai.login.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.login.listener.MaxLengthWatcher;
import com.yousails.chrenai.login.listener.TextChangeListener;
import com.yousails.chrenai.login.receiver.SMSBroadcastReceiver;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.PhoneUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.RegisterErrorDialog;
import com.yousails.chrenai.view.ValidateDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/6/20.
 */

public class RegisterActivity extends BaseActivity {
    private SMSBroadcastReceiver mSMSBroadcastReceiver;
    private ValidateDialog validateDialog;
    private RegisterErrorDialog registerErrorDialog;
    private LinearLayout backLayout;//返回
    private ImageView backView;
    private LinearLayout regLayout;//注册
    private TextView titleView;//标题

    private EditText phoneEditText;//手机号
    private EditText pwdEditText;//验证码
    private Button validateBtn;//获取验证码
    private TextView tipsTextView;//错误提示

    private Button nextBtn;
    private LinearLayout wxLoginLayout;//微信登录

    private String phoneNum;
    private String password;
    private String url;
    private String captchaKey;
    private int time = 0;
    private boolean b;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void init() {
        // 注册对象
        EventBus.getDefault().register(this);
    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.layout_close);
        backView = (ImageView) findViewById(R.id.iv_close);
        regLayout = (LinearLayout) findViewById(R.id.layout_register);
        titleView = (TextView) findViewById(R.id.title);

        phoneEditText = (EditText) findViewById(R.id.tv_phone);
        pwdEditText = (EditText) findViewById(R.id.tv_pwd);
        validateBtn = (Button) findViewById(R.id.iv_validate);
        tipsTextView = (TextView) findViewById(R.id.tv_error_tips);

        nextBtn = (Button) findViewById(R.id.login_btn_submit);
        wxLoginLayout = (LinearLayout) findViewById(R.id.wx_login_layout);

        titleView.setText(R.string.phone_register);
        nextBtn.setText(R.string.next_text);
//        backView.setBackgroundResource(R.drawable.ic_back);
        backView.setImageResource(R.drawable.ic_main_return);
        regLayout.setVisibility(View.GONE);
        wxLoginLayout.setVisibility(View.GONE);

        phoneEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(phoneEditText, 0);

            }
        }, 500);

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        validateBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        phoneEditText.addTextChangedListener(new MaxLengthWatcher(11, phoneEditText,phoneChangeListener));
        pwdEditText.addTextChangedListener(new MaxLengthWatcher(6, pwdEditText,pwdChangeListener));

        if(mSMSBroadcastReceiver==null){
            mSMSBroadcastReceiver=new SMSBroadcastReceiver();
        }
        mSMSBroadcastReceiver.setOnReceivedMessageListener(new SMSBroadcastReceiver.MessageListener() {
            public void OnReceived(String message) {
                pwdEditText.setText(message);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_close:
                finish();
                break;
            case R.id.iv_validate:
                if (checkPhoneNum()) {
                    getImageViewCode();
                }
                break;
            case R.id.login_btn_submit:
                getRegisterKey();
                break;
        }
    }

    TextChangeListener phoneChangeListener=new TextChangeListener() {
        @Override
        public void updateView(Editable editable) {
            int len = editable.length();
            phoneEditText.setTextColor(ContextCompat.getColor(mContext,R.color.light_black_color));
            tipsTextView.setText("");
            if (len >= 11) {
                validateBtn.setEnabled(true);
                validateBtn.setBackgroundResource(R.drawable.login_validate_btn_normal);
                checkText();
            } else {
                validateBtn.setEnabled(false);
                validateBtn.setBackgroundResource(R.drawable.login_validate_btn);
            }
        }
    };

    TextChangeListener pwdChangeListener=new TextChangeListener() {
        @Override
        public void updateView(Editable editable) {
            int len = editable.length();
            pwdEditText.setTextColor(ContextCompat.getColor(mContext,R.color.light_black_color));
            if (len == 6) {
                checkText();
            }
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_validate_code:
                    getImageViewCode();
                    break;
                case R.id.tv_submit:
                    validateBtn.setEnabled(false);
                    validateBtn.setBackgroundResource(R.drawable.login_validate_btn);
                    getSmsCode();
                    break;

            }
        }
    };

    View.OnClickListener onErrorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_submit:
                    registerErrorDialog.dismiss();
                    getRegisterKey();
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
                        }
                        validateDialog.show();

                        final EditText codeEditText=validateDialog.getCodeEditText();

                        codeEditText.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(codeEditText, 0);

                            }
                        }, 500);

                        Glide.with(mContext)
                                .load(url)
                                .into(validateDialog.getImgeView());
                        break;
                    case 2:
                        Bundle bundle1 = msg.getData();
                        String message = bundle1.getString("msg");
                        tipsTextView.setText(message);
                        phoneEditText.setTextColor(ContextCompat.getColor(mContext,R.color.login_red_color));
                        break;
                    case 3:
                        Bundle bundle2 = msg.getData();
                        validateBtn.setEnabled(true);
                        validateBtn.setBackgroundResource(R.drawable.login_validate_btn_normal);
                        validateDialog.dismiss();
                        tipsTextView.setText(bundle2.getString("msg"));
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
                        Bundle bundle3 = msg.getData();
                        String key = bundle3.getString("key");
                        AppPreference.getInstance(mContext).writeRegKey(key);
                        Intent intent = new Intent(RegisterActivity.this, RegInforActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        CustomToast.createToast(mContext, "注册失败!");
//                        if(registerErrorDialog==null){
//                            registerErrorDialog=new RegisterErrorDialog(mContext,onErrorClickListener);
//                        }
//                        registerErrorDialog.show();
                        break;

                }
            }
        };


    }

    @Override
    public void initData() {

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
            nextBtn.setEnabled(true);
            nextBtn.setBackgroundResource(R.drawable.login_btn_bg);
        } else {
            nextBtn.setEnabled(false);
            nextBtn.setBackgroundResource(R.drawable.login_btn_normal);
        }
    }

    /**
     * 获取图片验证码
     */
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
    private void getImageViewCode() {

        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
        }
        if (StringUtil.isEmpty(phoneNum)) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("phone", phoneNum);
        params.put("type", "register");

        OkHttpUtils.post()
                .url(ApiConstants.GET_CAPTCHAS_API)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, String response, Exception e, int id) {
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
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (StringUtil.isNotNull(response)) {
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
            return;
        }
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
        }
        Map<String, String> params = new HashMap<>();
        params.put("captcha_key", captchaKey);
        params.put("captcha_code", captchaCode);

        OkHttpUtils.post()
                .url(ApiConstants.GET_VERIFICATION_CODES_API)
                .params(params)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String response, Exception e, int id) {
                        if (StringUtil.isNotNull(response) && response.contains("message")) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                String message = jsonObj.optString("message");

                                Message mes = new Message();
                                mes.what = 3;
                                Bundle bundle = new Bundle();
                                bundle.putString("msg", message);
                                mes.setData(bundle);
                                mHandler.sendMessage(mes);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        CustomToast.createToast(mContext, "验证码已发送");
//                        validateBtn.setEnabled(true);
                        validateDialog.dismiss();
                        mHandler.sendEmptyMessageDelayed(4, 1000);
                    }
                });
    }

    /**
     * 获取registerKey
     */
    private void getRegisterKey() {
        Map<String, String> params = new HashMap<>();
        params.put("phone", phoneNum);
        params.put("verification_code",password);
//        params.put("verification_code", "123456");

        OkHttpUtils.post()
                .url(ApiConstants.GET_REGISTER_KEYS)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, String response, Exception e, int id) {
                        mHandler.sendEmptyMessage(6);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (StringUtil.isNotNull(response)) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String key = jsonObject.optString("register_key");
                                Message message = new Message();
                                message.what = 5;
                                Bundle bundle = new Bundle();
                                bundle.putString("key", key);
                                message.setData(bundle);
                                mHandler.sendMessage(message);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    private String getShowTime() {
        int remain = 60 - time;
        return remain + "秒";
    }

    @Override
    protected void onDestroy() {
        //注销
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finish(String message) {
        this.finish();
    }
}
