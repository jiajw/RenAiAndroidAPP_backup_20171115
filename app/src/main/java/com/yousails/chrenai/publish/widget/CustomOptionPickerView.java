package com.yousails.chrenai.publish.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.view.BasePickerView;
import com.bigkoo.pickerview.view.WheelOptions;
import com.yousails.chrenai.R;

import java.util.List;

/**
 * Author:WangKunHui
 * Date: 2017/7/14 14:36
 * Desc:
 * E-mail:life_artist@163.com
 */
public class CustomOptionPickerView<T> extends BasePickerView implements View.OnClickListener {

    private View pickerView;

    private Button btnSubmit, btnCancel; //确定、取消按钮

    private TextView tvTitle;

    private CustomWheelOptions<T> wheelOptions;

    public CustomOptionPickerView(Context context) {
        super(context);

        initViews(0);
        init();

        pickerView = LayoutInflater.from(context).inflate(R.layout.widget_time_picker, contentContainer);
        btnSubmit = (Button) pickerView.findViewById(R.id.btnSubmit);
        btnCancel = (Button) (pickerView.findViewById(R.id.btnCancel));
        tvTitle = (TextView) pickerView.findViewById(R.id.tvTitle);

        wheelOptions = new CustomWheelOptions(pickerView, false);

    }

    public void setTitle(String title) {
        tvTitle.setText(title + "");
    }

    public void setIsCanOutSideCancelable(boolean flag) {
        setOutSideCancelable(flag);
    }

    /**
     * @param day  日期
     * @param date 上下午
     * @param hour 小时数
     * @param min  分钟数
     */
    public void setData(List<T> day, List<T> date, List<T> hour, List<T> min) {
        wheelOptions.setPickerData(day, date, hour, min);
        resetCurrentItems();
    }

    private void resetCurrentItems() {
        wheelOptions.resetCurrentItems();
    }

    @Override
    public void onClick(View v) {

    }
}
