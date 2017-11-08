package com.yousails.chrenai.home.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.config.AppPreference;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */

public class SearchCityAdapter extends BaseAdapter {
    private Context mContext;
    private List<CityBean> cityList = new ArrayList<>();
    public SearchCityAdapter( Context context,List<CityBean> cityList){
        this.mContext=context;
        this.cityList=cityList;
    }
    @Override
    public int getCount() {
        return cityList==null?0:cityList.size();
    }

    @Override
    public Object getItem(int i) {
        return cityList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setDataChanged(List<CityBean> cityList){
        this.cityList=cityList;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=LayoutInflater.from(mContext).inflate(R.layout.list_child_item, null);
            viewHolder.cityLayout=(LinearLayout) convertView.findViewById(R.id.city_name_layout);
            viewHolder.cityNameView = (TextView) convertView.findViewById(R.id.name);
            viewHolder.dividerLine=(LinearLayout)convertView.findViewById(R.id.divider_line);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }

        final CityBean cityBean=cityList.get(position);
        viewHolder.cityNameView.setText(cityBean.getName());
        if(position==cityList.size()-1){
            viewHolder.dividerLine.setVisibility(View.GONE);
        }else{
            viewHolder.dividerLine.setVisibility(View.VISIBLE);
        }

        viewHolder.cityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppPreference.getInstance(mContext).writeCurrentCity(cityBean.getId()+","+cityBean.getName());
                EventBus.getDefault().post("changeCity");
            }
        });



        return convertView;
    }


    class ViewHolder{
        LinearLayout cityLayout;
        TextView cityNameView;
        LinearLayout dividerLine;
    }
}
