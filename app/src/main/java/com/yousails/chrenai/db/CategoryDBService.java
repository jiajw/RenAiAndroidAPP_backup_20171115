package com.yousails.chrenai.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.KeyWordBean;
import com.yousails.chrenai.home.bean.MenuBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */

public class CategoryDBService {
    private Context context;
    public CategoryDBService(Context context){
        this.context=context;
    }

    /**
     * 插入Category
     *
     * @param menuBean
     */
    public void insertCategory(MenuBean menuBean) {
        try {
            String table = "categoryTab";
            String where = "cId= ? ";
            String id=menuBean.getId()+"";
            String [] args = { menuBean.getId()+""};
            ContentValues values = new ContentValues();
            values.put("cId",  menuBean.getId()+"");
            values.put("name", menuBean.getName());

            int can = DatabaseUtil.getInstance(context).update(table, values, where,
                    args);
            if (can > 0 ? false : true) {
                DatabaseUtil.getInstance(context).insert(table, null, values);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取关键词
     *
     */
    public List<MenuBean> geCategory(){
        List<MenuBean>menuList=null;
        try {
            String sql = "select cId,name from categoryTab ";
            Cursor cursor = DatabaseUtil.getInstance(context).rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                menuList=new ArrayList<MenuBean>();
                while (cursor.moveToNext()) {
                    MenuBean menuBean=new MenuBean();
                    menuBean.setId(cursor.getInt(0));
                    menuBean.setName(cursor.getString(1));
                    menuList.add(menuBean);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return menuList;
    }
}
