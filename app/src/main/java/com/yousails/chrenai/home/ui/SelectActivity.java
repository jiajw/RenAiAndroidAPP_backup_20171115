package com.yousails.chrenai.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.utils.StringUtil;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/7/27.
 */

public class SelectActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout submitLayout;
    private TextView titleView;
    private LinearLayout contentLayout;
    private String[]options=null;
    private String type;
    private String selectValue;
    private int position;
    private ArrayList<String> selectArray=new ArrayList<String>();

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_enroll_select);
    }

    @Override
    protected void init() {

        Bundle bundle= getIntent().getBundleExtra("bundle");
        type=bundle.getString("type");
        options=bundle.getStringArray("option");
        position=bundle.getInt("position");
        selectValue=bundle.getString("selectValue");
    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView=(TextView)findViewById(R.id.title);
        titleView.setText("选择页面");

        submitLayout=(LinearLayout)findViewById(R.id.btn_more);
        TextView sharedView=(TextView)findViewById(R.id.txt_more);
        sharedView.setTextColor(getResources().getColor(R.color.main_blue_color));
        sharedView.setText("完成");

        contentLayout=(LinearLayout)findViewById(R.id.content_layout);

        initView();
    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        submitLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_more:
                Intent intent=new Intent();
                intent.putExtra("position",position);
                intent.putStringArrayListExtra("select",selectArray);
                setResult(1,intent);
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

    private void initView(){
        if(options!=null){
            contentLayout.removeAllViews();
            for(int i=0;i<options.length;i++){
                View view = LayoutInflater.from(mContext).inflate(R.layout.enroll_select_item, null, false);
                TextView titleView=(TextView)view.findViewById(R.id.title_tview);
                ImageView selectView=(ImageView)view.findViewById(R.id.select_imageview);
                final String name=options[i];
                titleView.setText(name);
                if(StringUtil.isNotNull(selectValue)&&StringUtil.isNotNull(type)){
                    if("radio".equals(type)) {
                        if (name.equals(selectValue)) {
                            selectView.setVisibility(View.VISIBLE);
                        }
                    }else{
                        if(selectValue.contains(name)){
                            selectArray.add(name);
                            selectView.setVisibility(View.VISIBLE);
                        }
                    }
                }
                view.setOnClickListener(new ItemClickListener(name,selectView));
                view.setTag(i);

                contentLayout.addView(view);
            }
        }

    }


    class ItemClickListener implements View.OnClickListener{
        private ImageView selectView;
        private String name;
        public  ItemClickListener(String name,ImageView selectView){
            this.selectView=selectView;
            this.name=name;
        }

        @Override
        public void onClick(View view) {
            if(selectView.getVisibility()==View.VISIBLE){

                if(type!=null){
                    if("radio".equals(type)){
                        for(int i=0;i<contentLayout.getChildCount();i++){
                            View childView=contentLayout.getChildAt(i);
                            if(view.getTag()==childView.getTag()){
                                selectArray.remove(name);
                                childView.findViewById(R.id.select_imageview).setVisibility(View.GONE);
                            }
                        }
                    }else{
                        selectArray.remove(name);
                        selectView.setVisibility(View.GONE);
                    }
                }
            }else{
                if(type!=null){
                    if("radio".equals(type)){
                        for(int i=0;i<contentLayout.getChildCount();i++){
                            View childView=contentLayout.getChildAt(i);
                            if(view.getTag()==childView.getTag()){
                                selectArray.clear();
                                selectArray.add(name);
                                childView.findViewById(R.id.select_imageview).setVisibility(View.VISIBLE);
                            }else{
                                childView.findViewById(R.id.select_imageview).setVisibility(View.GONE);
                            }
                        }
                    }else{
                        selectArray.add(name);
                        selectView.setVisibility(View.VISIBLE);
                    }
                }

            }
        }
    }


}
