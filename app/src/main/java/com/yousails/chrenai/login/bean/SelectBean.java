package com.yousails.chrenai.login.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/8.
 */

public class SelectBean implements Serializable {
    private String id;
    private String name;
    private String isSelected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }
}
