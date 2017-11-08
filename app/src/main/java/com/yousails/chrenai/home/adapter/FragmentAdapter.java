package com.yousails.chrenai.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yousails.chrenai.home.bean.MenuBean;
import com.yousails.chrenai.home.fragment.ColumnFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/27.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {
    private List<ColumnFragment> mFragments;
    private List<String> mTitles;
    private List<MenuBean>menuLists=new ArrayList<MenuBean>();
    public FragmentAdapter(FragmentManager fm, List<ColumnFragment> fragments, List<MenuBean>menuList) {
        super(fm);
        mFragments = fragments;
        menuLists = menuList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return menuLists.get(position).getName();
    }

}
