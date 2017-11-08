package com.yousails.chrenai.home.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/5.
 */

public class AppConfigBean implements Serializable{
    private String key;
    private String title;
    private String type;
    private String[] options;

//    private Object options;
    private boolean required;
//    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

//    public String getValue() {
//        return value;
//    }
//
//    public void setValue(String value) {
//        this.value = value;
//    }
}
