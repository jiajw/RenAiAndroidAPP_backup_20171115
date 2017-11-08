package com.yousails.chrenai.home.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/12.
 */

public class PaginationBean implements Serializable {
    private String total;
    private String count;
    private String per_page;
    private String current_page;
    private String total_pages;
          //  links

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPer_page() {
        return per_page;
    }

    public void setPer_page(String per_page) {
        this.per_page = per_page;
    }

    public String getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(String current_page) {
        this.current_page = current_page;
    }

    public String getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(String total_pages) {
        this.total_pages = total_pages;
    }
}
