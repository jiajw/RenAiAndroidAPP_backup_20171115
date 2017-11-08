package com.yousails.chrenai.home.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/4.
 */

public class KeyWordBean implements Serializable{
    private String id;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
