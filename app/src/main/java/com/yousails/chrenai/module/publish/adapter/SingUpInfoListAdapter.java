package com.yousails.chrenai.module.publish.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * 帮助中心列表Adapter
 * User: jiajinwu
 * Date: 2016-08-01
 * Time: 16:17
 */

public class SingUpInfoListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Object> helpBeanList;

    public SingUpInfoListAdapter(Context mContext, List<Object> helpBeanList) {
        this.mContext = mContext;
        this.helpBeanList = helpBeanList;
    }

    @Override
    public int getCount() {
        return helpBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return helpBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.header_help_center_layout, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        viewHolder.tv_help_name = convertView.findViewById(R.id.tv_new_guider);
//        viewHolder.tv_help_name.setText(helpBeanList.get(position).getCatalogName());
        return convertView;
    }

    static class ViewHolder {
        private TextView tv_help_name;

    }
}
