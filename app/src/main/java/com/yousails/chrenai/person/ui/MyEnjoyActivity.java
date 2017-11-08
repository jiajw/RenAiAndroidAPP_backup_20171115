package com.yousails.chrenai.person.ui;

import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseActivity;
import com.yousails.chrenai.person.adapter.MyActPagerAdapter;
import com.yousails.chrenai.person.adapter.MyEnjoyPagerAdapter;
import com.yousails.chrenai.person.fragment.MyActFragment;
import com.yousails.chrenai.person.fragment.MyEnjoyFragment;
import com.yousails.chrenai.utils.TabUtils;

import java.util.ArrayList;
import java.util.List;

import static com.yousails.chrenai.R.id.iv_close;

/**
 * Created by liuwen on 2017/8/10.
 */

public class MyEnjoyActivity extends BaseActivity {
    private LinearLayout backLayout;
    private RelativeLayout searchLayout;

    private TabLayout tabLayout;
    private ViewPager myViewpager;


    private TextView title;

    private String[] titles=new String[]{"新报名","已结束"};
    private List<Fragment> fragments = new ArrayList<>();

    private String from ="mine";
    private String user = "";
    private boolean isFinish = false;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_my_enjoy);
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
        isFinish = getIntent().getBooleanExtra("isFinish",false);
        from = getIntent().getStringExtra("from");
        user = getIntent().getStringExtra("user");

    }

    @Override
    protected void findViews() {
        backLayout=(LinearLayout)findViewById(R.id.title_back);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchLayout=(RelativeLayout)findViewById(R.id.search_content_layout);
        searchLayout.setVisibility(View.GONE);
        title = (TextView) findViewById(R.id.title) ;
        title.setText(from.equals("mine")?"我的参与":"TA的参与");
        myViewpager = (ViewPager) findViewById(R.id.my_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        MyEnjoyFragment firstPage = new MyEnjoyFragment(false,3,from,user);
        MyEnjoyFragment secondPage = new MyEnjoyFragment(true,4,from,user);
        fragments.add(firstPage);
        fragments.add(secondPage);
        MyEnjoyPagerAdapter pagerAdapter = new MyEnjoyPagerAdapter(getSupportFragmentManager(),titles,fragments);
        myViewpager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(myViewpager);

        myViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                myViewpager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                TabUtils.setIndicator(tabLayout,60,60);
            }
        });

        if(isFinish){
            myViewpager.setCurrentItem(1);
        }else{
            myViewpager.setCurrentItem(0);
        }
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void handleMessage() {

    }

    @Override
    public void initData() {

    }
}
