package com.yousails.chrenai.home.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/5.
 * 首页菜单列表
 */

public class CategoryBean implements Serializable {
    private String id;
    private String name;

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
}
