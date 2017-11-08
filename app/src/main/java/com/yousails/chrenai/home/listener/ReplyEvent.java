package com.yousails.chrenai.home.listener;

/**
 * @author: wuchongtang
 * @date: 2017/9/18 10:06
 * @email:wuchongtang@yousails.com
 * @desc:
 */

public class ReplyEvent {
    private String type;
    public ReplyEvent(String type){
        this.type=type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
