package com.yousails.chrenai.net;

/**
 * Created by sym on 17/7/22.
 */

public class Path {

    private int mMaxSize; //压缩大于某一size的文件
    private String mPath; //图片路径

    public Path(String path, int maxSize) {

        this.mPath =path;
        this.mMaxSize = maxSize;
    }

    public int getMaxSize(){
        return mMaxSize;
    }

    public String toString(){
        return mPath;
    }
}
