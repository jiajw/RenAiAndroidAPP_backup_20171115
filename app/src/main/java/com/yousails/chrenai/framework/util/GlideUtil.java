package com.yousails.chrenai.framework.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


/**
 * 图片异步加载工具类
 * Class:ImageLoadUtil
 * User: jiajinwu
 * Date: 2016-08-01
 * Time: 14:18
 */

public class GlideUtil {

    // 加载网络图片
    public static void loadUrlImage(Context mContext, String url, ImageView imageView, int preLoadImageId, int loadErrorImageId) {
        if(mContext == null)
            return;;
        Glide.with(mContext)
                .load(url)
                .placeholder(preLoadImageId)//设置加载的时候的图片
                .error(loadErrorImageId)//设置加载失败后显示的图片
                .dontAnimate()
                .centerCrop()
                .thumbnail(0.1f)
                .into(imageView);
    }

}
