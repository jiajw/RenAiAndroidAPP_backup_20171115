package com.yousails.chrenai.home.ui;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.utils.CustomToast;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.view.CancelEnrollDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/7/29.
 */

public class DeleteEnrollActivity extends BaseActivity {
    private CancelEnrollDialog cancelEnrollDialog;
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout submitLayout;
    private TextView titleView;
    private EditText reasonEditText;
    private TextView promptView;
    private String aId;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_cancel_enroll);
    }

    @Override
    protected void init() {

        aId=getIntent().getStringExtra("id");
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("取消报名");

        submitLayout=(LinearLayout)findViewById(R.id.btn_more);
        TextView sharedView=(TextView)findViewById(R.id.txt_more);
        sharedView.setTextColor(getResources().getColor(R.color.main_blue_color));
        sharedView.setText("提交");

        reasonEditText=(EditText)findViewById(R.id.et_reason);
        promptView=(TextView)findViewById(R.id.prompt_tview);


    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        submitLayout.setOnClickListener(this);

        reasonEditText.addTextChangedListener(textWatcher);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                finish();
//                quit();
                break;
            case R.id.btn_more:
              String reasonStr=reasonEditText.getText().toString().trim();
                if(StringUtil.isNotNull(reasonStr)) {
                    deleteEnroll(reasonStr);
                }else{
                    CustomToast.createToast(mContext,"请输入取消原因");
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
                    case 1:
                        CustomToast.createToast(mContext,"取消报名成功");
                        setResult(2);
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
     * 取消报名 （取消申请表）
     */
    private void deleteEnroll(String reason){
        if(!NetUtil.detectAvailable(mContext)){
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtil.isEmpty(aId)) return;

        String url= ApiConstants.GET_COMMENTS_API+aId+"/user/application";
        String mToken= AppPreference.getInstance(mContext).readToken();
        RequestBody requestBody = new FormBody.Builder().add("id",aId).add("cancel_reason", reason).build();
        OkHttpUtils.delete().url(url).addHeader("Authorization", mToken).requestBody(requestBody).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, String response, Exception e, int id) {
                CustomToast.createToast(mContext,"取消报名失败");
            }

            @Override
            public void onResponse(String response, int id) {
                mHandler.sendEmptyMessage(1);
            }
        });
    }


    TextWatcher textWatcher=new TextWatcher() {
        private CharSequence temp;
        private int editStart ;
        private int editEnd ;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            temp = charSequence;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            int length=editable.toString().trim().length();
            if(length>0){
                promptView.setVisibility(View.GONE);

                editStart = reasonEditText.getSelectionStart();
                editEnd = reasonEditText.getSelectionEnd();
                if (temp.length() > 200) {
                    editable.delete(editStart-1, editEnd);
                    int tempSelection = editStart;
                    reasonEditText.setText(editable);
                    reasonEditText.setSelection(tempSelection);
                }
            }else{
                promptView.setVisibility(View.VISIBLE);
            }


        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.quit_btn:
                    cancelEnrollDialog.dismiss();
                    DeleteEnrollActivity.this.finish();
                    break;

            }
        }
    };


    /**
     * 取消报名Dialog
     */
    private void quit(){
        if (cancelEnrollDialog == null) {
            cancelEnrollDialog = new CancelEnrollDialog(mContext, onClickListener);
        }
        cancelEnrollDialog.show();

    }


    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            quit();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

}
