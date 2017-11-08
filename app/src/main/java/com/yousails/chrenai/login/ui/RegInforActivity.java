package com.yousails.chrenai.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.app.ui.MainActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.db.UserDBService;
import com.yousails.chrenai.login.bean.LoginResultBean;
import com.yousails.chrenai.login.bean.MetaBean;
import com.yousails.chrenai.login.bean.UserBean;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.RegisterErrorDialog;
import com.yousails.chrenai.view.WXLoginDialog;
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

public class RegInforActivity extends BaseActivity {
    private LinearLayout closedLayout;//返回
    private RelativeLayout sexLayout;
    private EditText nameView;
    private TextView sexView;
    private TextView tipsTextView;//错误提示
    private Button submitBtn;
    private RegisterErrorDialog regErrorDialog;

    private static String mToken;
    private String nickName;
    private String sex = "";
    private boolean b;
    private WXLoginDialog wxLoginDialog;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_register_infor);
    }

    @Override
    protected void init() {
        // 注册对象
        EventBus.getDefault().register(this);

    }

    @Override
    protected void findViews() {
        closedLayout = (LinearLayout) findViewById(R.id.layout_close);
        sexLayout = (RelativeLayout) findViewById(R.id.sex_relayout);
        nameView = (EditText) findViewById(R.id.tv_name);
        sexView = (TextView) findViewById(R.id.tv_sex);
        tipsTextView = (TextView) findViewById(R.id.tv_error_tips);
        submitBtn = (Button) findViewById(R.id.login_btn_submit);

    }

    @Override
    protected void setListeners() {
        nameView.addTextChangedListener(onTextChangeListener);
        closedLayout.setOnClickListener(this);
        sexLayout.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    @Override
    protected void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
//                        if(wxLoginDialog==null){
//                            wxLoginDialog.dismiss();
//                        }
                        Bundle bundle= msg.getData();
                        String message= bundle.getString("message");
//                        tipsTextView.setText(message);
                        if (regErrorDialog == null) {
                            regErrorDialog = new RegisterErrorDialog(mContext, onClickListener);
                        }
                        regErrorDialog.show();
                        break;
                }
            }
        };
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_close:
                finish();
                break;
            case R.id.sex_relayout:
                Intent intent = new Intent(RegInforActivity.this, SexSelectedActivity.class);
                intent.putExtra("sex", sexView.getText().toString().trim());
                intent.putExtra("from","register");
                startActivityForResult(intent, 0);
                break;
            case R.id.login_btn_submit:
//                updateUserInfor();
                doRegister();
                break;
        }
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_submit:
                    regErrorDialog.dismiss();
//                    updateUserInfor();
//                    doRegister();
                    Intent intent=new Intent(RegInforActivity.this,RegisterActivity.class);
                    startActivity(intent);
                    RegInforActivity.this.finish();
                    break;

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1 && data != null) {
            sex = data.getStringExtra("sex");
            sexView.setText(sex);
            checkInfor();
        }
    }

    /**
     * 用户昵称监听
     */
    private TextWatcher onTextChangeListener = new TextWatcher() {

        private String lenght;

        @Override
        public void onTextChanged(CharSequence charSequence, int start,
                                  int before, int count) {
           /* if (start == 0 && count > 0) {
                btn.setBackgroundResource(R.drawable.registe_btn_blue);
                btn.setClickable(true);
            }*/
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start,
                                      int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            int len = editable.length();
            if (len > 10) {
                tipsTextView.setText(R.string.entry_nickname_error);
            } else {
                tipsTextView.setText("");
                checkInfor();
            }
        }
    };

    /**
     * 检查编辑框信息是否为空
     */
    private void checkInfor() {
        nickName = nameView.getText().toString().trim();
        if (StringUtil.isNotNull(nickName) && StringUtil.isNotNull(sex)) {
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

    /**
     * 修改个人信息
     */
   /* private void updateUserInfor(){
        nickName=nameView.getText().toString().trim();
        Map<String, String> params=new HashMap<>();
        params.put("name",nickName);
        if("男".equals(sex)){
            params.put("sex","male");
        }else{
            params.put("sex","female");
        }

        OkHttpUtils.get()
                .url(ApiConstants.GET_USERS_API)
                .addHeader("Authorization",mToken)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call,String response,  Exception e, int id) {
                        System.out.println("----->"+e.toString());
                        mHandler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        System.out.println("----->" + response);
                        JSONObject jsonObj = null;
                        try {
                            System.out.println("----->" + response);
                            jsonObj = new JSONObject(response);
                            JSONObject obj = jsonObj.optJSONObject("data");


                            UserBean userBean = new Gson().fromJson(obj.toString(), UserBean.class);
                            if(userBean!=null){
                                //保存到sharepreference  Bearer
                                AppPreference.getInstance(mContext).writeUserId(userBean.getId());
                                AppPreference.getInstance(mContext).writeLastUserId(userBean.getId());
                                AppPreference.getInstance(mContext).writeUserName(userBean.getName());
                                AppPreference.getInstance(mContext).writePhone(userBean.getId(),userBean.getPhone()) ;
                                AppPreference.getInstance(mContext).setLogin(true);
                                AppPreference.getInstance(mContext).setLogout(false);
                                //保存到数据库
                                UserDBService userDBService=new UserDBService(mContext);
                                if(userDBService.hasUser(userBean.getId())){
                                    userDBService.updateUser(userBean);
                                }else{
                                    userDBService.insertUser(userBean);
                                }

                                //更新最后登录时间

                                Intent intent = new Intent(RegInforActivity.this,MainActivity.class);
                                startActivity(intent);

                                //关掉登录页面及注册页面
                                EventBus.getDefault().post("finish");
                            }

                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }*/


    /**
     * 注册
     */
    private void doRegister() {
//        showLoginDialog();
        String key = AppPreference.getInstance(mContext).readRegKey();
        nickName = nameView.getText().toString().trim();
        Map<String, String> params = new HashMap<>();
        params.put("register_key", key);
        params.put("name", nickName);
        if ("男".equals(sex)) {
            params.put("sex", "male");
        } else {
            params.put("sex", "female");
        }
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
        }

        OkHttpUtils.post()
                .url(ApiConstants.GET_USERS_API)
                .params(params)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String response, Exception e, int id) {
                        if (StringUtil.isNotNull(response) && response.contains("message")) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                String message = jsonObj.optString("message");

                                Message message1=new Message();
                                message1.what=0;
                                Bundle bundle=new Bundle();
                                bundle.putString("message",message);
                                message1.setData(bundle);
                                mHandler.sendMessage(message1);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
//                        if(wxLoginDialog==null){
//                            wxLoginDialog.dismiss();
//                        }
                        if (StringUtil.isNotNull(response)) {
                            try {
                                LoginResultBean loginResultBean = new Gson().fromJson(response, LoginResultBean.class);

                                if (loginResultBean != null) {

                                    UserBean userBean= loginResultBean.getUser();

                                    //保存到sharepreference
                                    AppPreference.getInstance(mContext).writeToken("Bearer "+loginResultBean.getToken());
                                    AppPreference.getInstance(mContext).writeExpired(loginResultBean.getExpired_at());
                                    AppPreference.getInstance(mContext).writeRefreshExpired(loginResultBean.getRefresh_expired_at());

                                    AppPreference.getInstance(mContext).writeUserId(userBean.getId());
                                    AppPreference.getInstance(mContext).writeLastUserId(userBean.getId());
                                    AppPreference.getInstance(mContext).writeUserName(userBean.getName());
                                    AppPreference.getInstance(mContext).writePhone(userBean.getId(), userBean.getPhone());
                                    AppPreference.getInstance(mContext).writeAvatar(userBean.getAvatar());
                                    AppPreference.getInstance(mContext).writeGender(userBean.getSex());
                                    AppPreference.getInstance(mContext).writeReligion(userBean.getReligion_name());
                                    AppPreference.getInstance(mContext).writeLevel(userBean.getLevel());
                                    AppPreference.getInstance(mContext).writeEMName(userBean.getIm_username());

                                    if(userBean.is_certificated()){
                                        AppPreference.getInstance(mContext).writeCertification("1");
                                    }

                                    if(userBean.is_vip()){
                                        AppPreference.getInstance(mContext).writeIsVip("1");
                                    }

                                    int hours=userBean.getWorking_hours();
                                    if(0!=hours){
                                        AppPreference.getInstance(mContext).writeWorkHours(hours+"");
                                    }


                                    AppPreference.getInstance(mContext).setLogin(true);
                                    AppPreference.getInstance(mContext).setLogout(false);

                                    MetaBean metaBean=loginResultBean.getMeta();
                                    if(metaBean!=null){
                                        AppPreference.getInstance(mContext).writeEMPwd(metaBean.getIm_password());

                                    }

                                    //保存到数据库
                                    UserDBService userDBService = new UserDBService(mContext);
                                    if (userDBService.hasUser(userBean.getId())) {
                                        userDBService.updateUser(userBean);
                                    } else {
                                        userDBService.insertUser(userBean);
                                    }


                                    /*//更新最后登录时间
                                    Intent intent = new Intent(RegInforActivity.this, MainActivity.class);
                                    startActivity(intent);

                                    //关掉登录页面及注册页面
                                    EventBus.getDefault().post("finish");*/

                                    login_Em();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
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


    /**
     * 登录环信
     */
    private void login_Em(){
        String emName=AppPreference.getInstance(mContext).readEMName();
        String emPwd=AppPreference.getInstance(mContext).readEMPwd();
        if(StringUtil.isEmpty(emName)||StringUtil.isEmpty(emPwd)){
            return;
        }

        // reset current user name before login
//        DemoHelper.getInstance().setCurrentUserName(currentUsername);

        // call login method
        Log.d(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(emName, emPwd, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");


                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // update current user's display name for APNs
//                boolean updatenick = EMClient.getInstance().pushManager().updatePushNickname(
//                        DemoApplication.currentUserNick.trim());
//                if (!updatenick) {
//                    Log.e("LoginActivity", "update current user nick fail");
//                }

//                if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
//                    pd.dismiss();
//                }
                // get user's info (this should be get from App's server or 3rd party service)
//                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

               /* Intent intent = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(intent);

                finish();*/

                //更新最后登录时间
                Intent intent = new Intent(RegInforActivity.this, MainActivity.class);
                startActivity(intent);

                //关掉登录页面及注册页面
                EventBus.getDefault().post("finish");

            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);
//                if (!progressShow) {
//                    return;
//                }
                runOnUiThread(new Runnable() {
                    public void run() {
//                        pd.dismiss();
//                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
//                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 登录Dialog
     */
    private void showLoginDialog(){
        if(wxLoginDialog==null){
            wxLoginDialog= new WXLoginDialog(this);
        }
        wxLoginDialog.show();
    }
}
