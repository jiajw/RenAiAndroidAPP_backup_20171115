package com.yousails.chrenai.person.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/5.
 */

public class AuthorityBean implements Serializable {

    private String name;
    private String display_name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

}
