package com.yousails.chrenai.publish.bean;

import com.yousails.chrenai.bean.HttpBaseBean;

import java.io.Serializable;

/**
 * Author:WangKunHui
 * Date: 2017/7/19 11:44
 * Desc:
 * E-mail:life_artist@163.com
 */
public class ImageUploadEntity extends HttpBaseBean {

    /**
     * content_type : image/png
     * file : http://chrenai.dev/uploads/assets/2017/05/1_14962093287012.png
     * user_id : 1
     * origin_name : 屏幕快照 2017-05-10 下午10.58.15.png
     * size : 28131
     * width : 446
     * height : 230
     * updated_at : 2017-05-31 13:42:08
     * created_at : 2017-05-31 13:42:08
     * id : 2
     */

    private String content_type;
    private String file;
    private int user_id;
    private String origin_name;
    private int size;
    private int width;
    private int height;
    private String updated_at;
    private String created_at;
    private int id;

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getOrigin_name() {
        return origin_name;
    }

    public void setOrigin_name(String origin_name) {
        this.origin_name = origin_name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
