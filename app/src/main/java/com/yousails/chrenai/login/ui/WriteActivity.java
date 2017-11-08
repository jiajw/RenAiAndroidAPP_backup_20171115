package com.yousails.chrenai.login.ui;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.login.listener.MaxLengthWatcher;
import com.yousails.chrenai.login.listener.TextChangeListener;
import com.yousails.chrenai.utils.StringUtil;

/**
 * Created by Administrator on 2017/8/8.
 */

public class WriteActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout comfirmLayout;
    private TextView comfirmView;

    private TextView titleView;
    private EditText contentView;
    private TextView tipsView;
    private String title;
    private String content;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_write);
    }

    @Override
    protected void init() {
        title=getIntent().getStringExtra("title");
        content=getIntent().getStringExtra("content");
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText(title);

        comfirmLayout=(LinearLayout)findViewById(R.id.btn_more);
        comfirmView=(TextView)findViewById(R.id.txt_more);
        comfirmView.setText("完成");
        comfirmView.setTextColor(getResources().getColor(R.color.main_blue_color));

        contentView=(EditText)findViewById(R.id.et_textarea);
        tipsView=(TextView)findViewById(R.id.textarea_right);


        if(StringUtil.isNotNull(content)){
            contentView.setText(content);
            contentView.setSelection(content.length());
        }

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        comfirmLayout.setOnClickListener(this);
//        contentView.addTextChangedListener(textWatcher);
        contentView.addTextChangedListener(new MaxLengthWatcher(30, contentView,editTextListener));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_more:
               String content= contentView.getText().toString().trim();
                if(StringUtil.isNotNull(content)){
                    Intent intent=new Intent();
                    intent.putExtra("content",content);
                    setResult(2,intent);
                }else{
                    setResult(2);
                }
                finish();
                break;
        }
    }

    @Override
    protected void handleMessage() {

    }

    @Override
    public void initData() {

    }

    TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    TextChangeListener editTextListener=new TextChangeListener() {
        @Override
        public void updateView(Editable editable) {
            int length=editable.length();
            if(length>0){
                tipsView.setVisibility(View.GONE);
            }else{
                tipsView.setVisibility(View.VISIBLE);
            }
        }
    };
}
