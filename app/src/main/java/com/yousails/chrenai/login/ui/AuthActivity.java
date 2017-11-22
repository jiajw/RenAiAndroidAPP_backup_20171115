package com.yousails.chrenai.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.common.LogUtil;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.framework.util.CommonUtil;
import com.yousails.chrenai.home.ui.ProtocolActivity;
import com.yousails.chrenai.login.bean.SelectBean;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.LogUtils;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.PhoneUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 实名认证第一步
 * Created by Administrator on 2017/8/8.
 */

public class AuthActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout sharedLayout;
    private TextView titleView;

    private RelativeLayout degressLayout;
    private RelativeLayout religionLayout;

    private EditText nameView;
    private EditText identView;
    private EditText edUrgentPeople;
    private EditText edUrgentPhone;
    private TextView tipsView;

    private TextView degressView;
    private TextView religionView;

    private RelativeLayout workLayout;
    private RelativeLayout specLayout;
    private TextView workView;
    private TextView specialityView;
    private Button nextBtn;

    private LinearLayout phone_divider;
    private RelativeLayout phone_layout;
    private EditText phoneView;

    private RelativeLayout protocolLayout;
    private CheckBox checkBox;

    private SelectBean degressBean;//选中返回的数据
    private SelectBean religionBean;
    private HashMap<String, String> inforMap = new HashMap<String, String>();
    private String name;
    private String phoneNum;
    private String identNum;
    private String urgentPeople, urgentPhone;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_certification);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText("实名认证");

        sharedLayout = (LinearLayout) findViewById(R.id.btn_more);
        sharedLayout.setVisibility(View.GONE);

        degressLayout = (RelativeLayout) findViewById(R.id.degrees_layout);
        religionLayout = (RelativeLayout) findViewById(R.id.religion_layout);

        nameView = (EditText) findViewById(R.id.name_edit_text);
        identView = (EditText) findViewById(R.id.identi_edit_text);

        edUrgentPeople = (EditText) findViewById(R.id.tv_urgent_people);
        edUrgentPhone = (EditText) findViewById(R.id.tv_urgent_phone);

        tipsView = (TextView) findViewById(R.id.tips_tview);//提示

        degressView = (TextView) findViewById(R.id.record_school_tview);
        religionView = (TextView) findViewById(R.id.religion_tview);

        workLayout = (RelativeLayout) findViewById(R.id.work_layout);
        specLayout = (RelativeLayout) findViewById(R.id.speciality_layout);
        workView = (TextView) findViewById(R.id.do_work_tview);
        specialityView = (TextView) findViewById(R.id.speciality_tview);

        nextBtn = (Button) findViewById(R.id.exit_btn_submit);

        phone_divider = (LinearLayout) findViewById(R.id.phone_divider);
        phone_layout = (RelativeLayout) findViewById(R.id.phone_layout);
        phoneView = (EditText) findViewById(R.id.phone_edit_text);

        protocolLayout = (RelativeLayout) findViewById(R.id.protocol_layout);
        checkBox = (CheckBox) findViewById(R.id.id_checkbox);


        String phone = AppPreference.getInstance(mContext).readPhone(AppPreference.getInstance(mContext).readUerId());
        if (StringUtil.isNotNull(phone)) {
            phoneView.setText(phone);
            phone_divider.setVisibility(View.VISIBLE);
            phone_layout.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        degressLayout.setOnClickListener(this);
        religionLayout.setOnClickListener(this);
        workLayout.setOnClickListener(this);
        specLayout.setOnClickListener(this);
        protocolLayout.setOnClickListener(this);

        nextBtn.setOnClickListener(this);

        nameView.addTextChangedListener(nameWatcher);

        identView.addTextChangedListener(identWatcher);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.degrees_layout:
                Intent intent = new Intent(mContext, DataSelectActivity.class);
                intent.putExtra("title", "学历");
                intent.putExtra("type", "dgrees");
                intent.putExtra("selected", degressView.getText().toString().trim());
                startActivityForResult(intent, 0);
                break;
            case R.id.religion_layout:
                Intent rIntent = new Intent(mContext, DataSelectActivity.class);
                rIntent.putExtra("title", "宗教信仰");
                rIntent.putExtra("type", "religions");
                rIntent.putExtra("selected", religionView.getText().toString().trim());
                startActivityForResult(rIntent, 1);
                break;
            case R.id.work_layout:
                Intent wIntent = new Intent(mContext, WriteActivity.class);
                wIntent.putExtra("title", "工作");
//                wIntent.putExtra("type","work");
                wIntent.putExtra("content", workView.getText().toString().trim());
                startActivityForResult(wIntent, 0);
                break;
            case R.id.speciality_layout:
                Intent sIntent = new Intent(mContext, WriteActivity.class);
                sIntent.putExtra("title", "特长");
//                sIntent.putExtra("type","特长");
                sIntent.putExtra("content", specialityView.getText().toString().trim());
                startActivityForResult(sIntent, 1);
                break;
            case R.id.exit_btn_submit:
                if (checkForm()) {

                    if (checkBox.isChecked()) {
                        checkIdent(identNum);
                    } else {
                        Toast.makeText(mContext, "请阅读并同意《志愿者服务协议》", Toast.LENGTH_SHORT).show();
                    }

                }

                break;

            case R.id.protocol_layout:
                Intent pIntent = new Intent(AuthActivity.this, ProtocolActivity.class);
                pIntent.putExtra("from", "service");
                startActivity(pIntent);
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
//                        Bundle bundle=msg.getData();
//                        String message=bundle.getString("message");
                        tipsView.setText(R.string.text_ident_error);
                        tipsView.setVisibility(View.VISIBLE);
                        identView.setTextColor(getResources().getColor(R.color.login_red_color));
                        break;
                    case 1:
//                        Intent intent = new Intent(mContext, CardActivity.class);
//                        intent.putExtra("map", inforMap);
//                        startActivity(intent);

                        submitPersonInfo(inforMap);
                        break;
                    case 2:
                        Bundle bundle = msg.getData();
                        String message = bundle.getString("message");
                        CustomToast.createToast(mContext, message);
                        break;
                    case 3:
                        CustomToast.createToast(mContext, "您的实名认证已完成");
                        Intent intent = new Intent(mContext, CertCompleteActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                }
            }
        };
    }


    private void submitPersonInfo(HashMap<String, String> paramMap) {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        String mToken = AppPreference.getInstance(mContext).readToken();

        final FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }

        OkHttpUtils.put().url(ApiConstants.SUBMIT_CERTIFICATION_API).addHeader("Authorization", mToken).requestBody(builder.build()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        LogUtils.e("=onError=response===" + response);
                        String message = jsonObject.optString("message");

                        Message message1 = new Message();
                        message1.what = 2;
                        Bundle bundle = new Bundle();
                        bundle.putString("message", message);
                        message1.setData(bundle);
                        mHandler.sendMessage(message1);

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        LogUtils.e("=onResponse=response===" + response);
                        //返回成功 保存宗教
                        JSONObject reJson = jsonObject.optJSONObject("religion");
                        if (reJson != null) {
                            AppPreference.getInstance(mContext).writeReligion(reJson.optString("name"));
                        }

                        String status = jsonObject.optString("status");
                        if ("passed".equals(status)) {
                            AppPreference.getInstance(mContext).writeCertification("1");
                        }

                        String userId = AppPreference.getInstance(mContext).readUerId();
                        AppPreference.getInstance(mContext).writeStatus(userId, status);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mHandler.sendEmptyMessage(3);

            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (1 == resultCode) {
                SelectBean selectedBean = (SelectBean) data.getSerializableExtra("selected");
                if (0 == requestCode) {
                    degressBean = selectedBean;
                    degressView.setText(selectedBean.getName());
                } else {
                    religionBean = selectedBean;
                    religionView.setText(selectedBean.getName());
                }

            } else if (resultCode == 2) {
                String content = data.getStringExtra("content");
                if (0 == requestCode) {
                    workView.setText(content);
                } else {
                    specialityView.setText(content);
                }
            }

        } else {
            if (1 == resultCode) {
                if (0 == requestCode) {
                    degressView.setText("");
                    degressBean = null;
                } else {
                    religionView.setText("");
                    religionBean = null;
                }

            } else if (resultCode == 2) {
                if (0 == requestCode) {
                    workView.setText("");
                } else {
                    specialityView.setText("");
                }
            }
        }
    }

    private boolean checkForm() {

        inforMap.clear();
        name = nameView.getText().toString().trim();
        if (StringUtil.isEmpty(name)) {
            CustomToast.createToast(mContext, "请输入姓名");
            return false;
        }

        if (checkName()) {
            tipsView.setText(R.string.text_name_error);
            tipsView.setVisibility(View.VISIBLE);
            nameView.setTextColor(getResources().getColor(R.color.login_red_color));
            return false;
        }

        inforMap.put("name", name);


        identNum = identView.getText().toString().trim();
        if (StringUtil.isEmpty(identNum)) {
            CustomToast.createToast(mContext, "请输入身份证号");
            return false;
        } else {
            //验证身份证
            String errorMsg;
            try {
                errorMsg = CommonUtil.IDCardValidate(identNum);
            } catch (ParseException e) {
                e.printStackTrace();
                errorMsg = "无效身份证";
            }
            if (!TextUtils.isEmpty(errorMsg)) {
                tipsView.setText(errorMsg);
                tipsView.setVisibility(View.VISIBLE);
                identView.setTextColor(getResources().getColor(R.color.login_red_color));
                return false;
            }

        }

        inforMap.put("identity_card_no", identNum);

        urgentPeople = edUrgentPhone.getText().toString().trim();
        if (StringUtil.isEmpty(urgentPeople)) {
            CustomToast.createToast(mContext, "请填写紧急联系人姓名");
            return false;
        }
        if (checkName()) {
            tipsView.setText(R.string.text_name_error);
            tipsView.setVisibility(View.VISIBLE);
            edUrgentPhone.setTextColor(getResources().getColor(R.color.login_red_color));
            return false;
        }

        inforMap.put("emergency_contact", urgentPeople);

        urgentPhone = edUrgentPhone.getText().toString().trim();

        if (StringUtil.isNotNull(urgentPhone)) {
            if (!PhoneUtil.checkPhoneNumber(urgentPhone)) {
                CustomToast.createToast(mContext, "手机号输入错误，请检查");
                return false;
            } else {
                inforMap.put("emergency_phone", urgentPhone);
            }
        } else {
            CustomToast.createToast(mContext, "请填写紧急联系人电话");
            return false;
        }

        phoneNum = phoneView.getText().toString().trim();
        if (StringUtil.isNotNull(phoneNum)) {
            if (!PhoneUtil.checkPhoneNumber(phoneNum)) {
                CustomToast.createToast(mContext, "手机号输入错误，请检查");
            } else {
                inforMap.put("phone", phoneNum);
            }
        }

        String work = workView.getText().toString().trim();
        if (StringUtil.isNotNull(work)) {
            inforMap.put("work", work);
        }

        String spec = specialityView.getText().toString().trim();
        if (StringUtil.isNotNull(work)) {
            inforMap.put("skills", spec);
        }

        if (degressBean != null) {
            inforMap.put("degree_id", degressBean.getId());
        }

        if (religionBean != null) {
            inforMap.put("religion_id", religionBean.getId());
        }

        return true;
    }

    TextWatcher identWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            int length = editable.length();
            if (length > 0) {
                tipsView.setVisibility(View.GONE);
            }
            identView.setTextColor(getResources().getColor(R.color.light_black_color));
        }
    };


    TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            int length = editable.length();
            if (length > 0) {
                tipsView.setVisibility(View.GONE);
            }
            nameView.setTextColor(getResources().getColor(R.color.light_black_color));
        }
    };


    /**
     * 检查身份证号是否正确
     */
    private void checkIdent(String ident) {

        String token = AppPreference.getInstance(mContext).readToken();
        String url = ApiConstants.CHECK_IDENTITY_API + ident + "/validity";

        OkHttpUtils.get().url(url).addHeader("Authorization", token).addParams("card_no", ident).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {

                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.e("==response==" + response);
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }


    /**
     * 验证姓名是否含有数字或是特殊字符
     */
    private boolean checkName() {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(name);
        if (!isNum.matches()) {
            return isSpecialChar(name);
        }
        return true;
    }

    /**
     * 验证手机号是否合法
     */
    private boolean checkPhoneNum() {
        phoneNum = phoneView.getText().toString().trim();
        if (StringUtil.isNotNull(phoneNum)) {
            if (!PhoneUtil.checkPhoneNumber(phoneNum)) {
                Toast.makeText(this, R.string.phone_error_tips, Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }


    /**
     * 验证输入的身份证号是否合法
     */
    private static boolean isLegalId(String id) {
        if (id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)")) {
            return true;
        } else {
            return false;
        }
    }

}
