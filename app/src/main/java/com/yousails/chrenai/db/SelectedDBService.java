package com.yousails.chrenai.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.yousails.chrenai.login.bean.SelectBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/8.
 */

public class SelectedDBService {
    private Context context;
    public SelectedDBService(Context context){
        this.context=context;
    }

    /**
     * 插入学历数据
     *
     * @param selectBean
     */
    public void insertDgrees(SelectBean selectBean) {
        try {
            String table = "dgreesTab";
            String where = "cId= ? ";
            String [] args = { selectBean.getId()};
            ContentValues values = new ContentValues();
            values.put("cId",  selectBean.getId());
            values.put("name", selectBean.getName());
            values.put("selected", selectBean.getIsSelected());

            int can = DatabaseUtil.getInstance(context).update(table, values, where,
                    args);
            if (can > 0 ? false : true) {
                DatabaseUtil.getInstance(context).insert(table, null, values);
            }

        } catch (Exception e) {
            System.out.print(e.toString());
            e.printStackTrace();
        }
    }


    /**
     * 获取学历列表数据
     *
     */
    public ArrayList<SelectBean> getDgrees(){
        ArrayList<SelectBean>selectBeanList=null;
        try {
            String sql = "select cId,name,selected from dgreesTab";
            Cursor cursor = DatabaseUtil.getInstance(context).rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                selectBeanList=new ArrayList<SelectBean>();
                while (cursor.moveToNext()) {
                    SelectBean selectBean=new SelectBean();
                    selectBean.setId(cursor.getString(0));
                    selectBean.setName(cursor.getString(1));
                    selectBean.setIsSelected(cursor.getString(2));

                    selectBeanList.add(selectBean);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            System.out.print(e.toString());
            e.printStackTrace();
        }

        return selectBeanList;
    }


    /**
     * 插入宗教信仰
     *
     * @param selectBean
     */
    public void insertReligions(SelectBean selectBean) {
        try {

            String table = "religionsTab";
            String where = "cId= ? ";
            String [] args = { selectBean.getId()};
            ContentValues values = new ContentValues();
            values.put("cId",  selectBean.getId());
            values.put("name", selectBean.getName());
            values.put("selected", selectBean.getIsSelected());

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
     * 获取宗教信仰列表数据
     *
     */
    public ArrayList<SelectBean> getReligions(){
        ArrayList<SelectBean>selectBeanList=null;
        try {
            String sql = "select cId,name,selected from religionsTab";
            Cursor cursor = DatabaseUtil.getInstance(context).rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                selectBeanList=new ArrayList<SelectBean>();
                while (cursor.moveToNext()) {
                    SelectBean selectBean=new SelectBean();
                    selectBean.setId(cursor.getString(0));
                    selectBean.setName(cursor.getString(1));
                    selectBean.setIsSelected(cursor.getString(2));

                    selectBeanList.add(selectBean);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return selectBeanList;
    }
}
