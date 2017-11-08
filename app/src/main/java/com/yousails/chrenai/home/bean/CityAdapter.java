package com.yousails.chrenai.home.bean;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.utils.AssortPinyinList;
import com.yousails.chrenai.utils.pinyin.LanguageComparator_CN;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/7/9.
 */

public class CityAdapter extends BaseExpandableListAdapter {
    private Handler mHandler;
    // 字符串
    private List<String> strList;
    private List<CityBean> cityList = new ArrayList<>();
    private AssortPinyinList assort ;

    private Context context;

    private LayoutInflater inflater;
    // 中文排序
    private LanguageComparator_CN cnSort = new LanguageComparator_CN();

    public CityAdapter(Context context, List<CityBean> cityList, Handler mHandler) {
        super();
        this.context = context;
        this.mHandler = mHandler;
        this.inflater = LayoutInflater.from(context);
        this.cityList = cityList;

        // 排序
        sort();

    }

    private void sort() {
        assort= new AssortPinyinList();
        // 分类
        for (CityBean cityBean : cityList) {
            assort.getHashList().add(cityBean.getName());
        }
        assort.getHashList().sortKeyComparator(cnSort);
        for (int i = 0, length = assort.getHashList().size(); i < length; i++) {
            Collections.sort((assort.getHashList().getValueListIndex(i)), cnSort);
        }

    }

    public Object getChild(int group, int child) {
        // TODO Auto-generated method stub
        return assort.getHashList().getValueIndex(group, child);
    }

    public long getChildId(int group, int child) {
        // TODO Auto-generated method stub
        return child;
    }

    public View getChildView(final int group, final int child, boolean arg2,
                             View contentView, ViewGroup arg4) {
        // TODO Auto-generated method stub
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.list_child_item, null);
        }
        TextView textView = (TextView) contentView.findViewById(R.id.name);
        LinearLayout dividerLine=(LinearLayout)contentView.findViewById(R.id.divider_line);
        if(child==getChildrenCount(group)-1){
            dividerLine.setVisibility(View.GONE);
        }else{
            dividerLine.setVisibility(View.VISIBLE);
        }
        final String cityName=assort.getHashList().getValueIndex(group, child);
        textView.setText(cityName);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityId=getCityId(cityName);
                AppPreference.getInstance(context).writeCurrentCity(cityId+","+cityName);
                EventBus.getDefault().post("changeCity");
            }
        });
        return contentView;
    }

    public int getChildrenCount(int group) {
        // TODO Auto-generated method stub
        return assort.getHashList().getValueListIndex(group).size();
    }

    public Object getGroup(int group) {
        // TODO Auto-generated method stub
        return assort.getHashList().getValueListIndex(group);
    }

    public int getGroupCount() {
        // TODO Auto-generated method stub
        return assort.getHashList().size();
    }

    public long getGroupId(int group) {
        // TODO Auto-generated method stub
        return group;
    }

    public View getGroupView(int group, boolean arg1, View contentView,
                             ViewGroup arg3) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.list_group_item, null);
            contentView.setClickable(true);
        }
        TextView textView = (TextView) contentView.findViewById(R.id.name);
        textView.setText(assort.getFirstChar(assort.getHashList()
                .getValueIndex(group, 0)));
        // 禁止伸展

        return contentView;
    }

    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean isChildSelectable(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    public AssortPinyinList getAssort() {
        return assort;
    }

    public void setDataChanged(List<CityBean> cityList) {
        this.cityList = cityList;

        // 排序
        sort();

        this.notifyDataSetChanged();
    }


    private String getCityId(String cityName){
        String cityId="";
        for(CityBean cityBean:cityList){
            if(cityName.equals(cityBean.getName())){
                cityId=cityBean.getId();
                break;
            }
        }

        return cityId;
    }
}
