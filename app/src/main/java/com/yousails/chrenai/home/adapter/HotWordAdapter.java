package com.yousails.chrenai.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.home.bean.KeyWordBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/4.
 */

public class HotWordAdapter extends BaseAdapter {
    private Context mContext;
    private List<KeyWordBean> hotWordList=new ArrayList<KeyWordBean>();
    public HotWordAdapter(Context context,List<KeyWordBean> lists){
        this.mContext=context;
        this.hotWordList=lists;
    }
    @Override
    public int getCount() {
        return hotWordList==null?0:hotWordList.size();
    }

    @Override
    public Object getItem(int i) {
        return hotWordList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setDataChanged(List<KeyWordBean> lists){
        this.hotWordList=lists;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=LayoutInflater.from(mContext).inflate(R.layout.gridview_item, null);
            viewHolder.titleView=(TextView)convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }

        viewHolder.titleView.setText(hotWordList.get(position).getContent());

        return convertView;
    }

    class ViewHolder{
        TextView titleView;
    }
}
