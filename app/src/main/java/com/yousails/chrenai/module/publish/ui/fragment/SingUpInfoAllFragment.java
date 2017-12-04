package com.yousails.chrenai.module.publish.ui.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.framework.base.TabBaseFragment;

/**
 * User: jiajinwu
 * Date: 2017-12-02
 * Time: 15:22
 * 修改备注：
 * version:
 */


public class SingUpInfoAllFragment extends TabBaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_sing_up, container, false);
        swipeRefreshLayout = view.findViewById(R.id.refresh_Layout);
        listView = view.findViewById(R.id.lv_list);
        return view;
    }

    @Override
    protected void initData() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    @Override
    protected void processClick(View v) {

    }
}
