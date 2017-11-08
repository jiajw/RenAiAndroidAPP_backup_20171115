package com.yousails.chrenai.person.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/9/7.
 */

public class UserPermsBean implements Serializable {
    private List<AuthorityBean> data;

    public List<AuthorityBean> getData() {
        return data;
    }

    public void setData(List<AuthorityBean> data) {
        this.data = data;
    }
}
