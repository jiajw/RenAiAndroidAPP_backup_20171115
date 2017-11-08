package com.yousails.chrenai.publish.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Author:WangKunHui
 * Date: 2017/7/19 10:45
 * Desc:
 * E-mail:life_artist@163.com
 */
public class ActiveApplyItem implements Serializable {

    /**
     * 单行文本
     */
    public static final String TYPE_SINGLE_INPUT = "text";

    /**
     * 多行文本
     */
    public static final String TYPE_MULTI_INPUT = "textarea";

    /**
     * 单选
     */
    public static final String TYPE_SINGLE_CHOICE = "radio";

    /**
     * 多选
     */
    public static final String TYPE_MULTI_CHOICE = "checkbox";

    /**
     * title : 性别
     * type : radio
     * options : ["男","女"]
     * required : true
     */

    private String title;

    private String type;

    private boolean required;

    private List<String> options;

    private int position = -1;

    private boolean isAddView = false;

    public ActiveApplyItem(String title, String type, boolean required) {
        this.title = title;
        this.type = type;
        this.required = required;
    }

    public ActiveApplyItem() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isAddView() {
        return isAddView;
    }

    public void setAddView(boolean addView) {
        isAddView = addView;
    }
}
