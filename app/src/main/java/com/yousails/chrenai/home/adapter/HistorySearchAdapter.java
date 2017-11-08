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

public class HistorySearchAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> historyList=new ArrayList<String>();
    public HistorySearchAdapter(Context context, List<String> lists){
        this.mContext=context;
        this.historyList=lists;
    }
    @Override
    public int getCount() {
        return historyList==null?0:historyList.size();
    }

    @Override
    public Object getItem(int i) {
        return historyList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=LayoutInflater.from(mContext).inflate(R.layout.history_listview_item, null);
            viewHolder.titleView=(TextView)convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }

        viewHolder.titleView.setText(historyList.get(position));

        return convertView;
    }

    class ViewHolder{
        TextView titleView;
    }
}
