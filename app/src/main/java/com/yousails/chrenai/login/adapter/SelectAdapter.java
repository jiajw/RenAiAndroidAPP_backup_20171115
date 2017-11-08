package com.yousails.chrenai.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.login.bean.SelectBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/8.
 */

public class SelectAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<SelectBean> selectBeanList;
    public SelectAdapter(Context mContext,ArrayList<SelectBean> selectBeanList){
        this.mContext=mContext;
        this.selectBeanList=selectBeanList;
    }
    @Override
    public int getCount() {
        return selectBeanList==null?0:selectBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return selectBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setDataChanged(ArrayList<SelectBean> selectBeanList){
        if(selectBeanList!=null){
            this.selectBeanList=selectBeanList;
        }
        this.notifyDataSetChanged();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.enroll_select_item, null, false);
            viewHolder.titleView=(TextView)convertView.findViewById(R.id.title_tview);
            viewHolder.selectView=(ImageView)convertView.findViewById(R.id.select_imageview);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }

        SelectBean selectBean=selectBeanList.get(position);
        viewHolder.titleView.setText(selectBean.getName());
        if(selectBean.getIsSelected().equals("1")){
            viewHolder.selectView.setVisibility(View.VISIBLE);
        }else{
            viewHolder.selectView.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder{
       private TextView titleView;
       private ImageView selectView;

    }
}
