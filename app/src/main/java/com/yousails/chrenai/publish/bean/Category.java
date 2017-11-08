package com.yousails.chrenai.publish.bean;

import java.io.Serializable;

/**
 * Author:WangKunHui
 * Date: 2017/7/13 12:08
 * Desc:
 * E-mail:life_artist@163.com
 */
public class Category implements Serializable {

    /**
     * id : 4
     * name : 2222
     */

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
