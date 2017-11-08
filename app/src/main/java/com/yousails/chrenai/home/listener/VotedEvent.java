package com.yousails.chrenai.home.listener;

/**
 * Created by Administrator on 2017/7/20.
 */

public class VotedEvent {
    private String flag;
    private String cId;
    public VotedEvent(String flag,String cId){
        this.flag=flag;
        this.cId=cId;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }
}
