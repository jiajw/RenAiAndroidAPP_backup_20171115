package com.yousails.chrenai.person.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.login.listener.MaxLengthWatcher;
import com.yousails.chrenai.login.listener.TextChangeListener;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.PhoneUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.CheckPhoneDialog;
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
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/8/10.
 */

public class UpatePhoneActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;

    private EditText phoneView;
    private EditText identCodeView;
    private TextView tipsTextView;//错误提示

    private Button validateBtn;
    private Button submitBtn;
    private String phoneNum;
    private String password;

    private ValidateDialog validateDialog;
    private CheckPhoneDialog checkPhoneDialog;
    private String url;
    private String captchaKey;
    private int time = 0;
    private boolean b;
    private boolean sucess;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_update_phone);
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
        titleView.setText("更改手机号");

        sharedLayout=(LinearLayout)findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.INVISIBLE);

        phoneView=(EditText)findViewById(R.id.tv_phone);
        identCodeView=(EditText)findViewById(R.id.tv_pwd);
        tipsTextView = (TextView) findViewById(R.id.tv_error_tips);

        validateBtn=(Button)findViewById(R.id.iv_validate);
        submitBtn=(Button)findViewById(R.id.login_btn_submit);

        phoneView.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(phoneView, 0);

            }
        }, 500);

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        validateBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        phoneView.addTextChangedListener(new MaxLengthWatcher(11, phoneView,phoneChangeListener));
        identCodeView.addTextChangedListener(new MaxLengthWatcher(6, identCodeView,pwdChangeListener));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.iv_validate:
                if (checkPhoneNum()) {

                    checkPhone();
                }
                break;
            case R.id.login_btn_submit:
                //先判断登录
                boolean isLogin=AppPreference.getInstance(mContext).readLogin();
                if(isLogin){
                    upatePhone();
                }else{
                    CustomToast.createToast(mContext,"请先登录");
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

                        Glide.with(mContext)
                                .load(url)
                                .into(validateDialog.getImgeView());
                        break;
                    case 2:
                        Bundle bundle1 = msg.getData();
                        String message = bundle1.getString("msg");
                        tipsTextView.setText(message);
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
                        }
                        break;
                    case 5:
                        CustomToast.createToast(mContext,"更改手机号成功");
                        sucess=true;
                        String uid=AppPreference.getInstance(mContext).readUerId();
                        AppPreference.getInstance(mContext).writePhone(uid,phoneNum);

                        //更改手机号
                        EventBus.getDefault().post("updatePhone");
                        finish();
                        break;
                    case 6:
                        CustomToast.createToast(mContext,"更改手机号失败");
                        break;
                    case 7:
                        getImageViewCode();
                        break;
                    case 8:
                        showPhoneDialog();
                        break;
                }
            }
        };
    }


    private String getShowTime() {
        int remain = 60 - time;
        return remain + "秒";
    }

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


    TextChangeListener phoneChangeListener=new TextChangeListener() {
        @Override
        public void updateView(Editable editable) {
            int len = editable.length();
            phoneView.setTextColor(ContextCompat.getColor(mContext,R.color.light_black_color));
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

    TextChangeListener pwdChangeListener=new TextChangeListener() {
        @Override
        public void updateView(Editable editable) {
//            int len = editable.length();
            identCodeView.setTextColor(ContextCompat.getColor(mContext,R.color.light_black_color));
//            if (len == 6) {
                checkText();
//            }
        }
    };


    /**
     * 检查输入框是否为空
     */
    private void checkText() {

        phoneNum = phoneView.getText().toString().trim();
        password = identCodeView.getText().toString().trim();
        if (StringUtil.isNotNull(phoneNum) && StringUtil.isNotNull(password)&&phoneNum.length()==11&&password.length()==6) {
            b = true;
        } else {
            b = false;
        }
        if (b) {
            submitBtn.setEnabled(true);
            submitBtn.setBackgroundResource(R.drawable.login_btn_bg);
        } else {
            submitBtn.setEnabled(false);
            submitBtn.setBackgroundResource(R.drawable.login_btn_normal);
        }
    }

    @Override
    public void initData() {

    }

    private boolean checkPhoneNum() {
        phoneNum = phoneView.getText().toString().trim();
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
        params.put("type", "reset");

        OkHttpUtils.post().url(ApiConstants.GET_CAPTCHAS_API).params(params).build().execute(new StringCallback() {
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
            return;
        }
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
        }
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
                validateDialog.dismiss();
                mHandler.sendEmptyMessageDelayed(4, 1000);
            }
        });

    }


    private void showPhoneDialog(){
        if(checkPhoneDialog==null){
            checkPhoneDialog=new CheckPhoneDialog(mContext,onCheckListener);
        }
        checkPhoneDialog.show();
    }

    View.OnClickListener onCheckListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.quit_btn:
                    checkPhoneDialog.dismiss();
                    getImageViewCode();
                    break;

            }
        }
    };

    /**
     * 更改手机号
     */

    private void upatePhone(){

        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
        }

       String identCode= identCodeView.getText().toString().trim();
        if(StringUtil.isEmpty(identCode)){
            Toast.makeText(mContext, "请输入短信验证码！", Toast.LENGTH_SHORT).show();
            return;
        }

        String token= AppPreference.getInstance(mContext).readToken();

        String url=ApiConstants.UPDATE_USERINFOR_API+"/phone";
        RequestBody requestBody = new FormBody.Builder().add("phone", phoneNum).add("verification_code", identCode).build();
        OkHttpUtils.put().url(url).addHeader("Authorization",token).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                mHandler.sendEmptyMessage(6);
            }

            @Override
            public void onResponse(String response, int id) {
                mHandler.sendEmptyMessage(5);
            }
        });

    }


    /**
     * 检查手机号
     */
    private void checkPhone(){
        String token=AppPreference.getInstance(mContext).readToken();
        String url=ApiConstants.GET_USER+phoneNum;
        OkHttpUtils.get().url(url).addHeader("Authorization",token).addParams("id_or_phone",phoneNum).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                mHandler.sendEmptyMessage(7);
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.print(response);
                mHandler.sendEmptyMessage(8);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatePhone(String message) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销
        EventBus.getDefault().unregister(this);
    }
}
