package com.yousails.chrenai.person.bean;

import com.yousails.chrenai.home.bean.PaginationBean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/24.
 */

public class SignInMetaBean implements Serializable {
    private String reached_count;
    private String unreached_count;
    private String left_count;
    private PaginationBean pagination;

    public String getReached_count() {
        return reached_count;
    }

    public void setReached_count(String reached_count) {
        this.reached_count = reached_count;
    }

    public String getUnreached_count() {
        return unreached_count;
    }

    public void setUnreached_count(String unreached_count) {
        this.unreached_count = unreached_count;
    }

    public String getLeft_count() {
        return left_count;
    }

    public void setLeft_count(String left_count) {
        this.left_count = left_count;
    }

    public PaginationBean getPagination() {
        return pagination;
    }

    public void setPagination(PaginationBean pagination) {
        this.pagination = pagination;
    }
}
