package com.yousails.chrenai.publish.event;

/**
 * Author:WangKunHui
 * Date: 2017/7/16 14:10
 * Desc:
 * E-mail:life_artist@163.com
 */
public class ApplySettingEvent {

    /**
     * 添加
     */
    public static final int TYPE_ADD = 1;

    /**
     * 删除
     */
    public static final int TYPE_DEL = 2;

    /**
     * 修改
     */
    public static final int TYPE_MODIFY = 3;

    private int type;

    private int position;

    public ApplySettingEvent(int type) {
        this.type = type;
    }

    public ApplySettingEvent(int type, int position) {
        this.type = type;
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
