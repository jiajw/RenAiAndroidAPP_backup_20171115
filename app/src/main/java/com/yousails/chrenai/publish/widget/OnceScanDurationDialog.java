package com.yousails.chrenai.publish.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.view.BasePickerView;
import com.yousails.chrenai.R;
import com.yousails.chrenai.common.ToastUtils;

import java.util.List;

/**
 * Author:WangKunHui
 * Date: 2017/7/20 15:11
 * Desc:
 * E-mail:life_artist@163.com
 */
public class OnceScanDurationDialog<T> extends BasePickerView implements View.OnClickListener {

    private View pickerView;

    private Button btnSubmit, btnCancel; //确定、取消按钮

    private TextView tvTitle;

    private OnceScanWheelOptions wheelOptions;

    public OnceScanDurationDialog(final Context context) {
        super(context);

        initViews(0);
        init();

        pickerView = LayoutInflater.from(context).inflate(R.layout.widget_once_scan_picker, contentContainer);
        btnSubmit = (Button) pickerView.findViewById(R.id.btnSubmit);
        btnCancel = (Button) (pickerView.findViewById(R.id.btnCancel));
        tvTitle = (TextView) pickerView.findViewById(R.id.tvTitle);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        wheelOptions = new OnceScanWheelOptions(pickerView, false);
    }

    public void setTitle(String title) {
        tvTitle.setText(title + "");
    }

    public void setOnChoiceListener(View.OnClickListener listener) {
        btnSubmit.setOnClickListener(listener);
    }

    public void setSelected(int hourPosition, int minPosition) {
        wheelOptions.setSelectedPosition(hourPosition, minPosition);
    }

    public int[] getChoice() {
        return wheelOptions.getChoice();
    }

    /**
     * @param hour 小时数
     * @param min  分钟数
     */
    public void setData(List<T> hour, List<T> min) {
        wheelOptions.setPickerData(hour, min);
    }

    public void setIsCanOutSideCancelable(boolean flag) {
        setOutSideCancelable(flag);
    }

    @Override
    public void onClick(View v) {

    }
}
