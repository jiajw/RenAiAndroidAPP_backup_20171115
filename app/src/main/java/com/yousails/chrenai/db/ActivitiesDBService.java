package com.yousails.chrenai.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.yousails.chrenai.home.bean.ActivitiesBean;
import com.yousails.chrenai.login.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/5.
 */

public class ActivitiesDBService {
    private Context mContext;

    public ActivitiesDBService(Context context) {
        this.mContext = context;
    }

    /**
     * 插入活动列表
     *
     * @param activitiesBean
     */
    public void insertActivities(ActivitiesBean activitiesBean) {
        try {
            String table = "activitiesTab";
            String where = "id= ?";
            String[] args = {activitiesBean.getId()};
            ContentValues values = new ContentValues();
            values.put("id", activitiesBean.getId());
            values.put("title", activitiesBean.getTitle());
            values.put("imageUrl", activitiesBean.getCover_image());
            values.put("address", activitiesBean.getAddress());
            values.put("distance", activitiesBean.getDistance());
            values.put("district", activitiesBean.getDistrict());
            values.put("categoryId", activitiesBean.getCategory_id());
            values.put("cityId", activitiesBean.getCity_id());

            values.put("userId", activitiesBean.getUser_id());
            values.put("userName", activitiesBean.getUser().getName());
            values.put("headUrl", activitiesBean.getUser().getAvatar());
            values.put("isCertificated", activitiesBean.getUser().is_certificated() ? "1" : "0");
            values.put("isVip", activitiesBean.getUser().is_vip() ? "1" : "0");

            values.put("workHours", activitiesBean.getUser().getWorking_hours());
            values.put("level", activitiesBean.getUser().getLevel());


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
     * 获取活动列表
     */
    public List<ActivitiesBean> getActivitiesList() {
        List<ActivitiesBean> activitiesBeanList = null;
        try {
            String sql = "select id,title,imageUrl,address,district,distance,userId,userName,headUrl,isCertificated ,isVip,workHours,level from activitiesTab";
            Cursor cursor = DatabaseUtil.getInstance(mContext).rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                activitiesBeanList = new ArrayList<ActivitiesBean>();
                while (cursor.moveToNext()) {
                    ActivitiesBean activitiesBean = new ActivitiesBean();
                    UserBean userBean=new UserBean();

                    activitiesBean.setId(cursor.getString(0));
                    activitiesBean.setTitle(cursor.getString(1));
                    activitiesBean.setCover_image(cursor.getString(2));
                    activitiesBean.setAddress(cursor.getString(3));
                    activitiesBean.setDistrict(cursor.getString(4));
                    activitiesBean.setDistance(cursor.getString(5));
                    activitiesBean.setUser_id(cursor.getString(6));

                    userBean.setName(cursor.getString(7));
                    userBean.setAvatar(cursor.getString(8));
                    userBean.setIs_certificated(cursor.getString(9) == "1" ? true : false);
                    userBean.setIs_vip(cursor.getString(10) == "1" ? true : false);
                    userBean.setWorking_hours(Integer.valueOf(cursor.getString(11)));
                    userBean.setLevel(cursor.getString(12));
                    activitiesBean.setUser(userBean);

                    activitiesBeanList.add(activitiesBean);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return activitiesBeanList;
    }


    /**
     * 根据条件获取活动列表
     */
    public List<ActivitiesBean> getActivitiesByParam(String categoryId,String cityId) {
        //coordinate ,distance ,keyword ,cityId,category,start_date
        if("0".equals(categoryId)){
            return getActivitiesByCityId(cityId);
        }
        List<ActivitiesBean> activitiesBeanList = null;
        try {
            String sql = "select id,title,imageUrl,address,district,distance,userId,userName,headUrl,isCertificated,isVip,workHours,level from activitiesTab where categoryId= ? and cityId=?";
            String[] parm = new String[] { categoryId ,cityId};
            Cursor cursor = DatabaseUtil.getInstance(mContext).rawQuery(sql, parm);
            if (cursor != null && cursor.getCount() > 0) {
                activitiesBeanList = new ArrayList<ActivitiesBean>();
                while (cursor.moveToNext()) {
                    ActivitiesBean activitiesBean = new ActivitiesBean();
                    UserBean userBean=new UserBean();

                    activitiesBean.setId(cursor.getString(0));
                    activitiesBean.setTitle(cursor.getString(1));
                    activitiesBean.setCover_image(cursor.getString(2));
                    activitiesBean.setAddress(cursor.getString(3));
                    activitiesBean.setDistrict(cursor.getString(4));
                    activitiesBean.setDistance(cursor.getString(5));
                    activitiesBean.setUser_id(cursor.getString(6));

                    userBean.setName(cursor.getString(7));
                    userBean.setAvatar(cursor.getString(8));
                    userBean.setIs_certificated(cursor.getString(9) == "1" ? true : false);
                    userBean.setIs_vip(cursor.getString(10) == "1" ? true : false);
                    userBean.setWorking_hours(Integer.valueOf(cursor.getString(11)));
                    userBean.setLevel(cursor.getString(12));
                    activitiesBean.setUser(userBean);

                    activitiesBeanList.add(activitiesBean);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activitiesBeanList;
    }


    private List<ActivitiesBean> getActivitiesByCityId(String cityId){
        List<ActivitiesBean> activitiesBeanList = null;
        try {
            String sql = "select id,title,imageUrl,address,district,distance,userId,userName,headUrl,isCertificated,isVip,workHours,level from activitiesTab where cityId=?";
            String[] parm = new String[] {cityId};
            Cursor cursor = DatabaseUtil.getInstance(mContext).rawQuery(sql, parm);
            if (cursor != null && cursor.getCount() > 0) {
                activitiesBeanList = new ArrayList<ActivitiesBean>();
                while (cursor.moveToNext()) {
                    ActivitiesBean activitiesBean = new ActivitiesBean();
                    UserBean userBean=new UserBean();

                    activitiesBean.setId(cursor.getString(0));
                    activitiesBean.setTitle(cursor.getString(1));
                    activitiesBean.setCover_image(cursor.getString(2));
                    activitiesBean.setAddress(cursor.getString(3));
                    activitiesBean.setDistrict(cursor.getString(4));
                    activitiesBean.setDistance(cursor.getString(5));
                    activitiesBean.setUser_id(cursor.getString(6));

                    userBean.setName(cursor.getString(7));
                    userBean.setAvatar(cursor.getString(8));
                    userBean.setIs_certificated(cursor.getString(9) == "1" ? true : false);
                    userBean.setIs_vip(cursor.getString(10) == "1" ? true : false);
                    userBean.setWorking_hours(Integer.valueOf(cursor.getString(11)));
                    userBean.setLevel(cursor.getString(12));
                    activitiesBean.setUser(userBean);

                    activitiesBeanList.add(activitiesBean);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activitiesBeanList;
    }
}
