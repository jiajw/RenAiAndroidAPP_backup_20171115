package com.yousails.chrenai.publish.bean;

/**
 * Author:WangKunHui
 * Date: 2017/7/16 11:55
 * Desc:
 * E-mail:life_artist@163.com
 */
public class ActiveApplyItemWarp {

    /**
     * 是否为添加自定义选择项布局
     */
    private boolean isOption;

    private ActiveApplyItem item;

    public ActiveApplyItemWarp() {
        isOption = true;
    }

    public ActiveApplyItemWarp(ActiveApplyItem item) {
        this.item = item;
        isOption = false;
    }

    public boolean isOption() {
        return isOption;
    }

    public ActiveApplyItem getItem() {
        return item;
    }
}
