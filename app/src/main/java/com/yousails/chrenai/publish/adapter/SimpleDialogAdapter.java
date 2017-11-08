package com.yousails.chrenai.publish.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yousails.chrenai.R;
import com.yousails.chrenai.publish.bean.SimpleDialogItem;

import java.util.List;

/**
 * Author:WangKunHui
 * Date: 2017/7/13 15:07
 * Desc: 选择图片Dialog的Adapter
 * E-mail:life_artist@163.com
 */
public class SimpleDialogAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;

    private List<SimpleDialogItem> data;

    private Context context;

    public SimpleDialogAdapter(Context context, List<SimpleDialogItem> data) {
        layoutInflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        SimpleDialogItem simpleDialogItem = data.get(position);
        if (simpleDialogItem == null) {
            return view;
        }

        if (view == null) {
            view = layoutInflater.inflate(R.layout.adapter_item_simple_dialog, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (simpleDialogItem.getTextSize() != 0) {
            viewHolder.textView.setTextSize(simpleDialogItem.getTextSize());
        }

        if (simpleDialogItem.getColorRes() != 0) {
            viewHolder.textView.setTextColor(ContextCompat.getColor(context, simpleDialogItem.getColorRes()));
        }

        viewHolder.textView.setText(simpleDialogItem.getContent() + "");

        return view;
    }

    private class ViewHolder {
        TextView textView;
    }
}
