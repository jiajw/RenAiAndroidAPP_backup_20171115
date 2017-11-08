package com.yousails.chrenai.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.yousails.chrenai.home.bean.BannerBean;
import com.yousails.chrenai.home.bean.CityBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/9.
 */

public class CityDBService {
    private Context mContext;

    public CityDBService(Context context) {
        this.mContext = context;
    }

    /**
     * 插入城市数据
     *
     * @param bannerBean
     */
    public void insertCity(CityBean cityBean) {
        try {
            String table = "cityTab";
            String where = "cId= ?";
            String[] args = {cityBean.getId()};
            ContentValues values = new ContentValues();
            values.put("cId", cityBean.getId());
            values.put("name", cityBean.getName());
            values.put("parentId", cityBean.getParent_id());
            values.put("initial", cityBean.getInitial());
            values.put("initials", cityBean.getInitials());
            values.put("pinyin", cityBean.getPinyin());
            values.put("suffix", cityBean.getSuffix());
            values.put("code", cityBean.getCode());

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
     * 获取城市列表
     */
    public List<CityBean> getCityList() {
        List<CityBean> cityList = null;
        try {
            String sql = "select cId,name,parentId,initial,initials,pinyin,suffix,code from cityTab";
            Cursor cursor = DatabaseUtil.getInstance(mContext).rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                cityList = new ArrayList<CityBean>();
                while (cursor.moveToNext()) {
                    CityBean cityBean = new CityBean();
                    cityBean.setId(cursor.getString(0));
                    cityBean.setName(cursor.getString(1));
                    cityBean.setParent_id(cursor.getString(2));
                    cityBean.setInitial(cursor.getString(3));
                    cityBean.setInitials(cursor.getString(4));
                    cityBean.setPinyin(cursor.getString(5));
                    cityBean.setSuffix(cursor.getString(6));
                    cityBean.setCode(cursor.getInt(7));
                    cityList.add(cityBean);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cityList;
    }
}
