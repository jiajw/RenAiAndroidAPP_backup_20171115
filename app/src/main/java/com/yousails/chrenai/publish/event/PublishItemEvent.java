package com.yousails.chrenai.publish.event;

/**
 * Author:WangKunHui
 * Date: 2017/7/20 14:26
 * Desc:
 * E-mail:life_artist@163.com
 */
public class PublishItemEvent {

    public static final int TYPE_HELP = 1;

    public static final int TYPE_ONCE_SCAN = 2;

    private int clickType;

    public PublishItemEvent(int clickType) {
        this.clickType = clickType;
    }

    public int getClickType() {
        return clickType;
    }

    public void setClickType(int clickType) {
        this.clickType = clickType;
    }
}
