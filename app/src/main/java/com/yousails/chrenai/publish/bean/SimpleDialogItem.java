package com.yousails.chrenai.publish.bean;

/**
 * Author:WangKunHui
 * Date: 2017/7/16 11:03
 * Desc:
 * E-mail:life_artist@163.com
 */
public class SimpleDialogItem {

    private String content;

    private float textSize;

    private Object mTag;

    private int colorRes;

    public SimpleDialogItem(String content) {
        this.content = content;
    }

    public SimpleDialogItem(String content, float textSize) {
        this.content = content;
        this.textSize = textSize;
    }

    public SimpleDialogItem(String content, int colorRes) {
        this.content = content;
        this.colorRes = colorRes;
    }

    public SimpleDialogItem(String content, float textSize, int colorRes) {
        this.content = content;
        this.textSize = textSize;
        this.colorRes = colorRes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getColorRes() {
        return colorRes;
    }

    public void setColorRes(int colorRes) {
        this.colorRes = colorRes;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }
}
