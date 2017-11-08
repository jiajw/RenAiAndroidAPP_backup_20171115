package com.yousails.chrenai.home.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.yousails.chrenai.R;
import com.yousails.chrenai.app.ui.BaseFragment;
import com.yousails.chrenai.config.ApiConstants;
import com.yousails.chrenai.db.CategoryDBService;
import com.yousails.chrenai.home.adapter.FragmentAdapter;
import com.yousails.chrenai.home.bean.MenuBean;
import com.yousails.chrenai.utils.NetUtil;
import com.yousails.chrenai.utils.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/6/16.
 */

public class HomeFragment extends BaseFragment {
    private static final String TAG = "HomeFragment";
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    protected Handler mHandler;
    private List<MenuBean> menuLists = new ArrayList<MenuBean>();
    private List<ColumnFragment> fragments =null;
    private CategoryDBService categoryDBService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(categoryDBService==null){
            categoryDBService=new CategoryDBService(mContext);
        }

    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        findViews();
        setListeners();
        handleMessage();
        getCategories();
        return mRootView;
    }

    @Override
    public void initData() {

    }

    private void findViews() {

        mTabLayout = (TabLayout) mRootView.findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
    }

    private void initViewPage() {

        if (menuLists != null && menuLists.size() > 0) {
            for (int i = 0; i < menuLists.size(); i++) {
                mTabLayout.addTab(mTabLayout.newTab().setText(menuLists.get(i).getName()));
            }
           fragments = new ArrayList<ColumnFragment>();
            ColumnFragment columnFragment = null;
            for (int i = 0; i < menuLists.size(); i++) {
                columnFragment = new ColumnFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("index", i);
                bundle.putInt("cId",menuLists.get(i).getId());
                columnFragment.setArguments(bundle);
                fragments.add(columnFragment);
            }
            FragmentAdapter mFragmentAdapter =
                    new FragmentAdapter(getChildFragmentManager(), fragments, menuLists);
            //给ViewPager设置适配器
            mViewPager.setAdapter(mFragmentAdapter);
            //  mViewPager.setOffscreenPageLimit(1);
            //将TabLayout和ViewPager关联起来。
            mTabLayout.setupWithViewPager(mViewPager);
            //给TabLayout设置适配器
            mTabLayout.setTabsFromPagerAdapter(mFragmentAdapter);

        }

//        mViewPager.setCurrentItem(1);

    }

    private void setListeners() {

    }


    private void handleMessage() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
//                       Toast.makeText(mContext, "获取验证码失败!", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        initViewPage();
                        break;
                    case 2:
                        Bundle bundle1 = msg.getData();
                        String message = bundle1.getString("msg");

                        break;
                    case 3:

                    case 4:

                        break;
                }
            }
        };
    }

    /**
     * 获取分类(从属)列表
     */
    private void getCategories() {
        if (!NetUtil.detectAvailable(mContext)) {
            Toast.makeText(mContext, "请链接网络！", Toast.LENGTH_SHORT).show();
        }

        OkHttpUtils.get().url(ApiConstants.GET_CATEGORIES_API).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, String response, Exception e, int id) {
                if(categoryDBService!=null){
                    menuLists = categoryDBService.geCategory();
                }
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onResponse(String response, int id) {
                if (StringUtil.isNotNull(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        menuLists.clear();
                        MenuBean menuBean = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            menuBean = new MenuBean();
                            menuBean.setId(json.optInt("id"));
                            menuBean.setName(json.optString("name"));
                            menuLists.add(menuBean);
                        }
                        //需插入到数据库
                        for(MenuBean menuBean1:menuLists){
                            if(categoryDBService!=null){
                                categoryDBService.insertCategory(menuBean1);
                            }
                        }

                        mHandler.sendEmptyMessage(1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 切换城市，重新获取活动列表
     */
    public void changeCity() {
        int position=mViewPager.getCurrentItem();
        if(fragments!=null&&fragments.size()>0){
            ((ColumnFragment)fragments.get(position)).refreshActivites();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
