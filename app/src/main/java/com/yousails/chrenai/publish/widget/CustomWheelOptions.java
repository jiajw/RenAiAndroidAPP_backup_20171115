package com.yousails.chrenai.publish.widget;

import android.view.View;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.view.WheelOptions;

import java.util.List;

/**
 * Author:WangKunHui
 * Date: 2017/7/14 14:37
 * Desc:
 * E-mail:life_artist@163.com
 */
public class CustomWheelOptions<T> extends WheelOptions<T> {

    protected WheelView wv_option4;

    protected List<T> N_mOptions4Items;

    public CustomWheelOptions(View view, Boolean linkage) {
        super(view, linkage);
        wv_option4 = (WheelView) view.findViewById(com.bigkoo.pickerview.R.id.options4);
    }

    //不联动情况下
    public void setPickerData(List<T> options1Items, List<T> options2Items, List<T> options3Items, List<T> options4Items) {

        this.mOptions1Items = options1Items;
        this.N_mOptions2Items = options2Items;
        this.N_mOptions3Items = options3Items;
        this.N_mOptions4Items = options4Items;

        int len = ArrayWheelAdapter.DEFAULT_LENGTH;
        if (this.N_mOptions3Items == null)
            len = 8;
        if (this.N_mOptions2Items == null)
            len = 12;
        // 选项1
        wv_option1.setAdapter(new ArrayWheelAdapter(mOptions1Items, len));// 设置显示数据
        wv_option1.setCurrentItem(0);// 初始化时显示的数据
        wv_option1.setCyclic(false);

        // 选项2
        if (N_mOptions2Items != null) {
            wv_option2.setAdapter(new ArrayWheelAdapter(N_mOptions2Items));// 设置显示数据
            wv_option2.setCurrentItem(0);// 初始化时显示的数据
            wv_option2.setCyclic(false);
        }

        // 选项3
        if (N_mOptions3Items != null) {
            wv_option3.setAdapter(new ArrayWheelAdapter(N_mOptions3Items));// 设置显示数据
            wv_option3.setCyclic(true);
        }

        // 选项3
        if (N_mOptions4Items != null) {
            wv_option4.setVisibility(View.VISIBLE);
            wv_option4.setAdapter(new ArrayWheelAdapter(N_mOptions4Items));// 设置显示数据
            wv_option4.setCyclic(true);
        }

        wv_option3.setCurrentItem(wv_option3.getCurrentItem());
        wv_option1.setIsOptions(true);
        wv_option2.setIsOptions(true);
        wv_option3.setIsOptions(true);
        wv_option4.setIsOptions(true);
    }

    public void resetCurrentItems() {

        if (mOptions1Items != null && mOptions1Items.size() > 0 && wv_option1 != null) {
            wv_option1.setCurrentItem(0);
        }
        if (N_mOptions2Items != null && N_mOptions2Items.size() > 0 && wv_option2 != null) {
            wv_option2.setCurrentItem(0);
        }

        if (N_mOptions3Items != null && N_mOptions3Items.size() > 0 && wv_option3 != null) {
            wv_option3.setCurrentItem(0);
        }

        if (N_mOptions4Items != null && N_mOptions4Items.size() > 0 && wv_option4 != null) {
            wv_option4.setCurrentItem(0);
        }
    }

}
