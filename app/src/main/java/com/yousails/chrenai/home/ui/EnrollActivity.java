package com.yousails.chrenai.home.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.home.bean.AppConfigBean;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.PhoneUtil;
import com.yousails.chrenai.utils.StringUtil;


import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/26.
 */

public class EnrollActivity extends BaseActivity {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout submitLayout;
    private TextView titleView;
    private List<AppConfigBean> appConfigList;
    private EditText nameView, phoneView;
    private TextView arrow_name_right, arrow_phone_right;
    private LinearLayout contentLayout;
    private RelativeLayout add_enroll_layout;
    private EditText reasonEditText;
    private TextView add_tview_right;

    private RelativeLayout protocolLayout;
    private CheckBox checkBox;

    private ActivitiesBean activitiesBean;
    private StringBuffer strBuffer;
    private String isAdd;



    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_enroll);
    }

    @Override
    protected void init() {
        isAdd = getIntent().getStringExtra("add");
        activitiesBean = (ActivitiesBean) getIntent().getSerializableExtra("activitiesBean");
    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText("报名表");

        submitLayout = (LinearLayout) findViewById(R.id.btn_more);
        TextView sharedView = (TextView) findViewById(R.id.txt_more);
        sharedView.setTextColor(getResources().getColor(R.color.main_blue_color));
        sharedView.setText("提交");

        nameView = (EditText) findViewById(R.id.tv_name);
        phoneView = (EditText) findViewById(R.id.tv_phone);

        arrow_name_right = (TextView) findViewById(R.id.name_right);
        arrow_phone_right = (TextView) findViewById(R.id.phone_right);

        contentLayout = (LinearLayout) findViewById(R.id.content_layout);

        add_enroll_layout = (RelativeLayout) findViewById(R.id.add_relayout);
        reasonEditText = (EditText) findViewById(R.id.et_reason);
        add_tview_right = (TextView) findViewById(R.id.add_tview_right);

        //如果是追加报名
        if (StringUtil.isNotNull(isAdd) && "1".equals(isAdd)) {
            add_enroll_layout.setVisibility(View.VISIBLE);
        }

        initView();

        protocolLayout=(RelativeLayout)findViewById(R.id.protocol_layout);
        protocolLayout.setVisibility(View.VISIBLE);
        checkBox=(CheckBox)protocolLayout.findViewById(R.id.id_checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkBox.setChecked(true);
                }else{
                    checkBox.setChecked(false);
                }
            }
        });

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        submitLayout.setOnClickListener(this);
        nameView.addTextChangedListener(new CustomTextWatcher(arrow_name_right));
        phoneView.addTextChangedListener(new CustomTextWatcher(arrow_phone_right));
        reasonEditText.addTextChangedListener(new CustomTextWatcher(add_tview_right));
        protocolLayout.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkBox.setChecked(true);
                }else{
                    checkBox.setChecked(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_more:
                checkView();
                break;
            case R.id.protocol_layout:
                Intent intent=new Intent(EnrollActivity.this,ProtocolActivity.class);
                intent.putExtra("from","activities");
                startActivity(intent);
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
                        CustomToast.createToast(mContext, "报名失败");
                        break;
                    case 1:
                        Bundle bundle = msg.getData();
                        String status = bundle.getString("status");
                        boolean isAdd = bundle.getBoolean("isAdd");
                        if ("passed".equals(status)) {
                            CustomToast.createToast(mContext, "报名成功");
                        } else if ("applied".equals(status)) {
                            CustomToast.createToast(mContext, "审核中");
                        } else if ("rejected".equals(status)) {
                            CustomToast.createToast(mContext, "审核拒绝");
                        }else{
                            CustomToast.createToast(mContext, "报名失败");
                        }

                        Intent intent = new Intent();
                        intent.putExtra("status", status);
                        intent.putExtra("isAdd", isAdd);
                        setResult(1, intent);
                        finish();
                        break;
                    case 2:

                        break;
                }
            }
        };
    }

    @Override
    public void initData() {

    }

    private void initView() {

        String phone=AppPreference.getInstance(mContext).readPhone(AppPreference.getInstance(mContext).readUerId());
        if(StringUtil.isNotNull(phone)){
            phoneView.setText(phone);
        }
        String certification=AppPreference.getInstance(mContext).readCertification();
        String realName=AppPreference.getInstance(mContext).readRealName();

        if(StringUtil.isNotNull(certification)&&"1".equals(certification)){
            if(StringUtil.isNotNull(realName)&&!"null".equals(realName)){
                nameView.setText(realName);
                nameView.setSelection(nameView.getText().length());
            }
        }


        if (activitiesBean != null) {
            if (activitiesBean.getUserApplication() == null) {
                appConfigList = activitiesBean.getApplication_config();
            } else {
                appConfigList = activitiesBean.getUserApplication().getContent();
            }

            contentLayout.removeAllViews();

            if (appConfigList != null && appConfigList.size() > 0) {

                for (int i = 0; i < appConfigList.size(); i++) {
                    AppConfigBean configBean = appConfigList.get(i);

                    String title = configBean.getTitle();
                    String type = configBean.getType();
                    boolean isRequired = configBean.isRequired();
                    final String[] options = configBean.getOptions();

                    View view = LayoutInflater.from(mContext).inflate(R.layout.enroll_item, null, false);

                    RelativeLayout inputLayout = (RelativeLayout) view.findViewById(R.id.input_relayout);
                    EditText inputEdiText = (EditText) view.findViewById(R.id.et_input);
                    TextView arrow_input_right = (TextView) view.findViewById(R.id.tv_input_right);
                    inputEdiText.addTextChangedListener(new CustomTextWatcher(arrow_input_right));


                    RelativeLayout multipleLayout = (RelativeLayout) view.findViewById(R.id.multiple_relayout);
                    TextView multipleImageView = (TextView) view.findViewById(R.id.tv_multiple_image);
                    TextView multipleView = (TextView) view.findViewById(R.id.tv_multiple);
                    TextView arrow_multiple_right = (TextView) view.findViewById(R.id.tv_multiple_right);
                    multipleLayout.setTag(i);

                    RelativeLayout textareaLayout = (RelativeLayout) view.findViewById(R.id.textarea_relayout);
                    EditText textareaEdiText = (EditText) view.findViewById(R.id.et_textarea);
                    TextView arrow_textarea_right = (TextView) view.findViewById(R.id.textarea_right);
                    textareaEdiText.addTextChangedListener(new CustomTextWatcher(arrow_textarea_right));


                    if ("text".equals(type)) {
                        inputLayout.setVisibility(View.VISIBLE);
                        multipleLayout.setVisibility(View.GONE);
                        textareaLayout.setVisibility(View.GONE);
                        inputEdiText.setHint(title);

                        if (isRequired) {
                            arrow_input_right.setText("必填");
                        } else {
                            arrow_input_right.setText("非必填");
                        }


                    } else if ("radio".equals(type)) {
                        inputLayout.setVisibility(View.GONE);
                        multipleLayout.setVisibility(View.VISIBLE);
                        textareaLayout.setVisibility(View.GONE);

                        Drawable icon = this.getResources().getDrawable(R.drawable.ic_radio);
                        multipleImageView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                        multipleView.setText(title);

                        if (isRequired) {
                            arrow_multiple_right.setText("必填");
                        } else {
                            arrow_multiple_right.setText("非必填");
                        }
                        multipleLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                TextView multipleView = (TextView) view.findViewById(R.id.tv_multiple);
                                String multipleValue = multipleView.getText().toString().trim();

                                Intent intent = new Intent(mContext, SelectActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putStringArray("option", options);
                                bundle.putString("type", "radio");
                                bundle.putInt("position", (int) view.getTag());
                                bundle.putString("selectValue", multipleValue);
                                intent.putExtra("bundle", bundle);
                                startActivityForResult(intent, 0);
                            }
                        });

                    } else if ("checkbox".equals(type)) {
                        inputLayout.setVisibility(View.GONE);
                        multipleLayout.setVisibility(View.VISIBLE);
                        textareaLayout.setVisibility(View.GONE);

                        Drawable icon = this.getResources().getDrawable(R.drawable.ic_checkbox);
                        multipleImageView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                        multipleView.setHint(title);

                        if (isRequired) {
                            arrow_multiple_right.setText("必填");
                        } else {
                            arrow_multiple_right.setText("非必填");
                        }

                        multipleLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                TextView multipleView = (TextView) view.findViewById(R.id.tv_multiple);
                                String multipleValue = multipleView.getText().toString().trim();

                                Intent intent = new Intent(mContext, SelectActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putStringArray("option", options);
                                bundle.putString("type", "checkbox");
                                bundle.putInt("position", (int) view.getTag());
                                bundle.putString("selectValue", multipleValue);
                                intent.putExtra("bundle", bundle);
                                startActivityForResult(intent, 0);
                            }
                        });
                    } else if ("textarea".equals(type)) {
                        inputLayout.setVisibility(View.GONE);
                        multipleLayout.setVisibility(View.GONE);
                        textareaLayout.setVisibility(View.VISIBLE);

                        textareaEdiText.setHint(title);
                        if (isRequired) {
                            arrow_textarea_right.setText("必填");
                        } else {
                            arrow_textarea_right.setText("非必填");
                        }
                    }

                    contentLayout.addView(view);
                }


            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (data != null) {
                int position = data.getIntExtra("position", 0);
                ArrayList<String> selectArray = data.getStringArrayListExtra("select");

                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < selectArray.size(); i++) {
                    stringBuffer.append(selectArray.get(i) + " ");
                }

                String multValue = stringBuffer.toString().trim();
                try {
                    if (contentLayout != null) {
                        for (int i = 0; i < contentLayout.getChildCount(); i++) {
                            if (i == position) {
                                View childView = contentLayout.getChildAt(i);
                                TextView multipleView = (TextView) childView.findViewById(R.id.tv_multiple);
                                TextView arrow_multiple_right = (TextView) childView.findViewById(R.id.tv_multiple_right);
                                multipleView.setText(multValue);
                                if (StringUtil.isNotNull(multValue)) {
                                    arrow_multiple_right.setVisibility(View.GONE);
                                } else {
                                    arrow_multiple_right.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("EnrollActivity", e.toString());
                }

            }
        }
    }

    private void checkView() {
        if (activitiesBean != null) {

            strBuffer = new StringBuffer();

            nameView = (EditText) findViewById(R.id.tv_name);
            phoneView = (EditText) findViewById(R.id.tv_phone);
            String name = nameView.getText().toString().trim();
            if (StringUtil.isEmpty(name)) {
                CustomToast.createToast(mContext, "姓名不能为空");
                return;
            }

            strBuffer.append("{\"name\":\"" + name + "\",");

            String phone = phoneView.getText().toString().trim();
            if (StringUtil.isEmpty(phone)) {
                CustomToast.createToast(mContext, "手机号不能为空");
                return;
            }

            if (!PhoneUtil.checkPhoneNumber(phone)) {
                CustomToast.createToast(mContext, "手机号输入错误，请检查!");
                return;
            }

            strBuffer.append("\"phone\":\"" + phone + "\",");

            if (appConfigList != null && appConfigList.size() > 0) {

                strBuffer.append("\"answers\":{");

                for (int i = 0; i < appConfigList.size(); i++) {
                    AppConfigBean configBean = appConfigList.get(i);
                    String title = configBean.getTitle();
                    String type = configBean.getType();
                    String key = configBean.getKey();
                    boolean isRequired = configBean.isRequired();

                    if ("text".equals(type)) {
                        RelativeLayout inputLayout = (RelativeLayout) contentLayout.getChildAt(i).findViewById(R.id.input_relayout);
                        EditText inputEdiText = (EditText) inputLayout.findViewById(R.id.et_input);

                        String inputStr = inputEdiText.getText().toString().trim();
                        if (StringUtil.isEmpty(inputStr)) {
                            if (isRequired) {
                                CustomToast.createToast(mContext, title + "不能为空");
                                return;
                            }
                        } else {

                            strBuffer.append("\"" + key + "\":" + "\"" + inputStr + "\",");
                        }
                    } else if ("radio".equals(type) || "checkbox".equals(type)) {
                        RelativeLayout multipleLayout = (RelativeLayout) contentLayout.getChildAt(i).findViewById(R.id.multiple_relayout);
                        TextView multipleView = (TextView) multipleLayout.findViewById(R.id.tv_multiple);

                        String multipleStr = multipleView.getText().toString().trim();
                        if (StringUtil.isEmpty(multipleStr)) {
                            if (isRequired) {
                                CustomToast.createToast(mContext, title + "不能为空");
                                return;
                            }
                        } else {

                            if ("radio".equals(type)) {
                                strBuffer.append("\"" + key + "\":" + "\"" + multipleStr + "\",");
                            } else {
                                String[] strArray = multipleStr.split(" ");

                                strBuffer.append("\"" + key + "\":[");

                                for (int j = 0; j < strArray.length; j++) {
                                    if (j != strArray.length - 1) {

                                        strBuffer.append("\"" + strArray[j] + "\",");
                                    } else {

                                        strBuffer.append("\"" + strArray[j] + "\"],");
                                    }

                                }
                            }

                        }

                    } else if ("textarea".equals(type)) {
                        RelativeLayout textareaLayout = (RelativeLayout) contentLayout.getChildAt(i).findViewById(R.id.textarea_relayout);
                        EditText textareaEdiText = (EditText) textareaLayout.findViewById(R.id.et_textarea);
                        String textareaStr = textareaEdiText.getText().toString().trim();
                        if (StringUtil.isEmpty(textareaStr)) {
                            if (isRequired) {
                                CustomToast.createToast(mContext, title + "不能为空");
                                return;
                            }
                        } else {

                            strBuffer.append("\"" + key + "\":" + "\"" + textareaStr + "\",");

                        }
                    }
                }


                String temp = strBuffer.toString().trim();
                strBuffer.deleteCharAt(temp.length() - 1);
                strBuffer.append("}");
            } else {

                String temp = strBuffer.toString().trim();
                strBuffer.deleteCharAt(temp.length() - 1);
            }

            String tempStr=strBuffer.toString();
            if(tempStr.contains(":}")){
                int index=tempStr.lastIndexOf(",");
                strBuffer.delete(index-1,tempStr.length());
                strBuffer.append("\"");
            }

            //如果是追加报名
            if (StringUtil.isNotNull(isAdd) && "1".equals(isAdd)) {
                String reason = reasonEditText.getText().toString().trim();
                if (StringUtil.isEmpty(reason)) {
                    CustomToast.createToast(mContext, "追加原因不能为空");
                    return;
                } else {
                    strBuffer.append(",\"description\":\"" + reason + "\"");
                }

            }

            strBuffer.append("}");

            if(checkBox.isChecked()){
                signUp(strBuffer.toString().trim());
            }else{
                Toast.makeText(mContext,"请阅读并同意《志愿活动参与须知》",Toast.LENGTH_SHORT).show();
            }



        }
    }

    /**
     * 报名活动（创建活动申请表）
     */
    private void signUp(String json) {
        String url;
        if (StringUtil.isNotNull(isAdd) && "1".equals(isAdd)) {
            url = ApiConstants.GET_COMMENTS_API + activitiesBean.getId() + "/additional/applications";
        } else {
            url = ApiConstants.GET_COMMENTS_API + activitiesBean.getId() + "/applications";
        }
        String mToken = AppPreference.getInstance(mContext).readToken();

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", mToken)
                .addHeader("content-type", "application/json")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                if (StringUtil.isNotNull(res)) {
                    try {
                        JSONObject jsonObject = new JSONObject(res);

                        String status = jsonObject.optString("status");
                        boolean isAdd = jsonObject.optBoolean("is_additional");

                        Bundle bundle = new Bundle();
                        bundle.putString("status", status);
                        bundle.putBoolean("isAdd", isAdd);
                        Message message1 = new Message();
                        message1.what = 1;
                        message1.setData(bundle);

                        mHandler.sendMessage(message1);


                    } catch (Exception e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(0);
                    }
                }

            }
        });
    }


    /**
     * 取消报名 （取消申请表）
     */

    private void deleteEnroll(String aId) {
        String url = ApiConstants.GET_COMMENTS_API + "1/user/application";
        String mToken = AppPreference.getInstance(mContext).readToken();
        //测试
        RequestBody requestBody = new FormBody.Builder().add("id", "1").add("cancel_reason", "不想参加了").build();
        OkHttpUtils.delete().url(url).addHeader("Authorization", mToken).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                CustomToast.createToast(mContext, "取消报名失败");
            }

            @Override
            public void onResponse(String response, int id) {
                CustomToast.createToast(mContext, "取消报名成功");
            }
        });
    }

    class CustomTextWatcher implements TextWatcher {
        TextView rightView;

        public CustomTextWatcher(TextView rightView) {
            this.rightView = rightView;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            int length = editable.toString().trim().length();
            if (length > 0) {
                this.rightView.setVisibility(View.GONE);
            } else {
                this.rightView.setVisibility(View.VISIBLE);
            }
        }
    }
}
