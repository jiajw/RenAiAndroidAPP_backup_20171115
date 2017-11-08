package com.yousails.chrenai.bean;

/**
 * Created by liuwen on 2017/8/22.
 */

public class EMUser {


    /**
     * id : 157
     * name : Www
     * avatar : http://oq6x42vnt.bkt.clouddn.com/uploads/assets/avatar_male.png
     * im_username : chrenai_157
     */

    private int id;
    private String name;
    private String avatar;
    private String im_username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIm_username() {
        return im_username;
    }

    public void setIm_username(String im_username) {
        this.im_username = im_username;
    }
}
