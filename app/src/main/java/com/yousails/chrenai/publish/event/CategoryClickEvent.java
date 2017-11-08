package com.yousails.chrenai.publish.event;

import com.yousails.chrenai.publish.bean.Category;

/**
 * Author:WangKunHui
 * Date: 2017/7/13 14:06
 * Desc:
 * E-mail:life_artist@163.com
 */
public class CategoryClickEvent {

    private Category category;

    public CategoryClickEvent(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
