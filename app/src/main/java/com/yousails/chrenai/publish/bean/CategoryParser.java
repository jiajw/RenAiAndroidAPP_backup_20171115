package com.yousails.chrenai.publish.bean;

import com.yousails.chrenai.bean.HttpBaseBean;

import java.util.List;

/**
 * Author:WangKunHui
 * Date: 2017/7/13 12:08
 * Desc:
 * E-mail:life_artist@163.com
 */
public class CategoryParser extends HttpBaseBean {

    private List<Category> data;

    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
        this.data = data;
    }
}
