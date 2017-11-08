package com.yousails.chrenai.person.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.person.adapter.MyActPagerAdapter;
import com.yousails.chrenai.person.fragment.AddRegisterFragment;
import com.yousails.chrenai.person.fragment.RegisterInforFragment;
import com.yousails.chrenai.utils.StringUtil;
import com.yousails.chrenai.utils.TabUtils;

import java.util.ArrayList;
import java.util.List;

import static com.yousails.chrenai.R.id.tabLayout;

/**
 * Created by Administrator on 2017/8/24.
 */

public class AddRegisterActivity extends BaseActivity implements AddRegisterFragment.OnCallbackListener{
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;
    private LinearLayout submitLayout;
    private TextView searchView;
    private EditText contentView;
    private ImageView clearView;
    private TextView titleView;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String[] titles=new String[]{"未审核","已同意","已拒绝"};
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private String cId;
    private String status = "unapply";


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_register_layout);
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void init() {
        cId=getIntent().getStringExtra("cid");
        status=getIntent().getStringExtra("status");
    }

    @Override
    protected void findViews() {
        backLayout = (LinearLayout) findViewById(R.id.title_back);
        searchLayout = (RelativeLayout) findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText("追加报名");

        submitLayout = (LinearLayout) findViewById(R.id.btn_more);
        searchView=(TextView)findViewById(R.id.txt_more);
        searchView.setText("搜索");
        searchView.setTextColor(getResources().getColor(R.color.main_blue_color));

        contentView=(EditText)findViewById(R.id.edit_text);
        contentView.setHint("请输入用户姓名/手机号");
        clearView=(ImageView)findViewById(R.id.iv_clear);

        mTabLayout = (TabLayout)findViewById(tabLayout);
        mViewPager = (ViewPager)findViewById(R.id.viewPager);



        //applied 申请中，passed 已通过，rejected 已拒绝
        AddRegisterFragment appliedFragment = new AddRegisterFragment();
        Bundle bundle0 = new Bundle();
        bundle0.putString("cid",cId);
        bundle0.putString("status","applied");
        appliedFragment.setArguments(bundle0);

        AddRegisterFragment passedFragment = new AddRegisterFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("cid",cId);
        bundle1.putString("status","passed");
        passedFragment.setArguments(bundle1);

        AddRegisterFragment deletedFragment = new AddRegisterFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString("cid",cId);
        bundle2.putString("status","rejected");
        deletedFragment.setArguments(bundle2);

        fragments.add(appliedFragment);
        fragments.add(passedFragment);
        fragments.add(deletedFragment);

        MyActPagerAdapter pagerAdapter = new MyActPagerAdapter(getSupportFragmentManager(),titles,fragments);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), true);
                ((AddRegisterFragment)fragments.get(tab.getPosition())).getRegisterInfor();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                TabUtils.setIndicator(mTabLayout,30,30);
            }
        });

        if(null!=status){
            if(status.equals("unapply")){
                mViewPager.setCurrentItem(0);
            }else if(status.equals("apply")){
                mViewPager.setCurrentItem(1);
            }else if(status.equals("refused")){
                mViewPager.setCurrentItem(2);
            }
        }

    }

    @Override
    protected void setListeners() {
        backLayout.setOnClickListener(this);
        submitLayout.setOnClickListener(this);
        clearView.setOnClickListener(this);
        contentView.addTextChangedListener(commentTextWatcher);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.btn_more:
                if(searchLayout.getVisibility()==View.GONE){
                    searchView.setText("取消");
                    searchLayout.setVisibility(View.VISIBLE);
                }else{
                    searchView.setText("搜索");
                    contentView.setText("");
                    searchLayout.setVisibility(View.GONE);
                    if(mViewPager!=null){
                        ((AddRegisterFragment)fragments.get(mViewPager.getCurrentItem())).getRegisterInfor();
                    }
                }
                break;
            case R.id.iv_clear:
                contentView.setText("");
                clearView.setVisibility(View.GONE);
                if(mViewPager!=null){
                    ((AddRegisterFragment)fragments.get(mViewPager.getCurrentItem())).getRegisterInfor();
                }
                break;
        }
    }

    @Override
    protected void handleMessage() {

    }

    @Override
    public void initData() {

    }


    /**
     * 更改未审核/已同意/已拒绝的报名数量
     */
    @Override
    public void updateTabText(String appliedCount,String passedCount,String rejectedCount) {

       if("0".equals(appliedCount)){
           mTabLayout.getTabAt(0).setText("未审核");
       }else{
           mTabLayout.getTabAt(0).setText("未审核"+appliedCount);
       }

        if("0".equals(passedCount)){
            mTabLayout.getTabAt(1).setText("已同意");
        }else{
            mTabLayout.getTabAt(1).setText("已同意"+passedCount);
        }

        if("0".equals(rejectedCount)){
            mTabLayout.getTabAt(2).setText("已拒绝");
        }else{
            mTabLayout.getTabAt(2).setText("已拒绝"+rejectedCount);
        }

    }

    /**
     * 获取搜索关键字
     */
    @Override
    public String getSearchKey(){
        return contentView.getText().toString().trim();
    }



    /**
     * 搜索
     */
    TextWatcher commentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String commStr = editable.toString().trim();
            if (StringUtil.isEmpty(commStr)) {
                clearView.setVisibility(View.GONE);
            } else {
                clearView.setVisibility(View.VISIBLE);
            }
            if(mViewPager!=null){
                ((AddRegisterFragment)fragments.get(mViewPager.getCurrentItem())).getRegisterInfor();
            }

        }
    };
}
