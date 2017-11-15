package com.yousails.common.event;

/**
 * Author:WangKunHui
 * Date: 2017/7/12 13:53
 * Desc:
 * E-mail:life_artist@163.com
 */
public class PublishTitleClickEvent {

    /**
     * 返回键
     */
    public static final int TYPE_BACK = 1;

    /**
     * 预览
     */
    public static final int TYPE_PREVIEW = 2;

    /**
     * 发布
     */
    public static final int TYPE_PUBLISH = 3;

    private int type;

    public PublishTitleClickEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
