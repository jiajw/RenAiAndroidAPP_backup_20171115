package com.yousails.chrenai.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.yousails.chrenai.home.bean.BannerBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/4.
 */

public class BannerDBService {
    private Context mContext;

    public BannerDBService(Context context) {
        this.mContext = context;
    }

    /**
     * 插入广告栏
     *
     * @param bannerBean
     */
    public void insertBanner(BannerBean bannerBean) {
        try {
            String table = "bannerTab";
            String where = "bId= ?";
            String[] args = {bannerBean.getId()};
            ContentValues values = new ContentValues();
            values.put("bId", bannerBean.getId());
            values.put("title", bannerBean.getTitle());
            values.put("image", bannerBean.getImage());
            values.put("link", bannerBean.getLink());

            int can = DatabaseUtil.getInstance(mContext).update(table, values, where,
                    args);
            if (can > 0 ? false : true) {
                DatabaseUtil.getInstance(mContext).insert(table, null, values);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取广告栏
     */
    public List<BannerBean> getBannerList() {
        List<BannerBean> bannerList = null;
        try {
            String sql = "select bId,title,image,link from bannerTab";
            Cursor cursor = DatabaseUtil.getInstance(mContext).rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                bannerList = new ArrayList<BannerBean>();
                while (cursor.moveToNext()) {
                    BannerBean bannerBean = new BannerBean();
                    bannerBean.setId(cursor.getString(0));
                    bannerBean.setTitle(cursor.getString(1));
                    bannerBean.setImage(cursor.getString(2));
                    bannerBean.setLink(cursor.getString(3));
                    bannerList.add(bannerBean);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bannerList;
    }
}
