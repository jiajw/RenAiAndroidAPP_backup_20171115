package com.yousails.chrenai.module.bean;

import java.io.Serializable;

/**
 * User: jiajinwu
 * Date: 2017-11-01
 * Time: 15:41
 * 修改备注：
 * version:
 */


public class VersionBean implements Serializable {


    /**
     * id : 5
     * version : 4
     * description : 测试
     * type : android
     * created_at : 2017-11-01 16:37:43
     * url : http://chrenai.yousails-project.com/packages/android/app-release.apk
     */

    private int id;
    private int version;
    private String description;
    private String type;
    private String created_at;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "VersionBean{" +
                "id=" + id +
                ", version=" + version +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", created_at='" + created_at + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
