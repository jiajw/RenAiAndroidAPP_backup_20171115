package com.yousails.chrenai.publish.widget;

import android.graphics.Typeface;
import android.view.View;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.bigkoo.pickerview.view.WheelOptions;

import java.util.List;

/**
 * Author:WangKunHui
 * Date: 2017/7/20 15:12
 * Desc:
 * E-mail:life_artist@163.com
 */
public class OnceScanWheelOptions<T> {

    protected View view;
    protected WheelView wv_option1;
    protected WheelView wv_option2;

    protected List<T> mOptions1Items;
    protected List<T> N_mOptions2Items;

    protected boolean linkage;

    protected OnItemSelectedListener wheelListener_option1;
    protected OnItemSelectedListener wheelListener_option2;

    //文字的颜色和分割线的颜色
    protected int textColorOut;
    protected int textColorCenter;
    protected int dividerColor;

    protected WheelView.DividerType dividerType;

    // 条目间距倍数
    protected float lineSpacingMultiplier = 1.6F;

    public OnceScanWheelOptions(View view, Boolean linkage) {
        super();

        this.linkage = linkage;
        this.view = view;
        wv_option1 = (WheelView) view.findViewById(com.bigkoo.pickerview.R.id.options1);// 初始化时显示的数据
        wv_option2 = (WheelView) view.findViewById(com.bigkoo.pickerview.R.id.options2);
    }

    public int[] getChoice() {
        int hours = wv_option1.getCurrentItem();
        int min = wv_option2.getCurrentItem();
        return new int[]{hours, min};
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    //不联动情况下
    public void setPickerData(List<T> options1Items, List<T> options2Items) {

        this.mOptions1Items = options1Items;
        this.N_mOptions2Items = options2Items;

        int len = ArrayWheelAdapter.DEFAULT_LENGTH;

        // 选项1
        wv_option1.setAdapter(new ArrayWheelAdapter(mOptions1Items, len));// 设置显示数据
        wv_option1.setCurrentItem(0);// 初始化时显示的数据
        wv_option1.setCyclic(true);

        // 选项2
        if (N_mOptions2Items != null) {
            wv_option2.setAdapter(new ArrayWheelAdapter(N_mOptions2Items));// 设置显示数据
            wv_option2.setCurrentItem(0);// 初始化时显示的数据
            wv_option2.setCyclic(true);
        }


        wv_option1.setIsOptions(true);
        wv_option2.setIsOptions(true);
    }

    public void setSelectedPosition(int hourPosition, int minPosition) {
        wv_option1.setCurrentItem(hourPosition);
        wv_option2.setCurrentItem(minPosition);
    }


    public void setTextContentSize(int textSize) {
        wv_option1.setTextSize(textSize);
        wv_option2.setTextSize(textSize);
    }

    private void setTextColorOut() {
        wv_option1.setTextColorOut(textColorOut);
        wv_option2.setTextColorOut(textColorOut);
    }

    private void setTextColorCenter() {
        wv_option1.setTextColorCenter(textColorCenter);
        wv_option2.setTextColorCenter(textColorCenter);
    }

    private void setDividerColor() {
        wv_option1.setDividerColor(dividerColor);
        wv_option2.setDividerColor(dividerColor);
    }

    private void setDividerType() {
        wv_option1.setDividerType(dividerType);
        wv_option2.setDividerType(dividerType);
    }

    private void setLineSpacingMultiplier() {
        wv_option1.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_option2.setLineSpacingMultiplier(lineSpacingMultiplier);
    }

    /**
     * 设置选项的单位
     *
     * @param label1 单位
     * @param label2 单位
     * @param label3 单位
     */
    public void setLabels(String label1, String label2, String label3) {
        if (label1 != null)
            wv_option1.setLabel(label1);
        if (label2 != null)
            wv_option2.setLabel(label2);
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic 是否循环
     */
    public void setCyclic(boolean cyclic) {
        wv_option1.setCyclic(cyclic);
        wv_option2.setCyclic(cyclic);
    }

    /**
     * 设置字体样式
     *
     * @param font 系统提供的几种样式
     */
    public void setTypeface(Typeface font) {
        wv_option1.setTypeface(font);
        wv_option2.setTypeface(font);
    }

    /**
     * 分别设置第一二三级是否循环滚动
     *
     * @param cyclic1,cyclic2,cyclic3 是否循环
     */
    public void setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3) {
        wv_option1.setCyclic(cyclic1);
        wv_option2.setCyclic(cyclic2);
    }

    /**
     * 设置间距倍数,但是只能在1.2-2.0f之间
     *
     * @param lineSpacingMultiplier
     */
    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        this.lineSpacingMultiplier = lineSpacingMultiplier;
        setLineSpacingMultiplier();
    }

    /**
     * 设置分割线的颜色
     *
     * @param dividerColor
     */
    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        setDividerColor();
    }

    /**
     * 设置分割线的类型
     *
     * @param dividerType
     */
    public void setDividerType(WheelView.DividerType dividerType) {
        this.dividerType = dividerType;
        setDividerType();
    }

    /**
     * 设置分割线之间的文字的颜色
     *
     * @param textColorCenter
     */
    public void setTextColorCenter(int textColorCenter) {
        this.textColorCenter = textColorCenter;
        setTextColorCenter();
    }

    /**
     * 设置分割线以外文字的颜色
     *
     * @param textColorOut
     */
    public void setTextColorOut(int textColorOut) {
        this.textColorOut = textColorOut;
        setTextColorOut();
    }

    /**
     * Label 是否只显示中间选中项的
     *
     * @param isCenterLabel
     */

    public void isCenterLabel(Boolean isCenterLabel) {
        wv_option1.isCenterLabel(isCenterLabel);
        wv_option2.isCenterLabel(isCenterLabel);
    }
}
