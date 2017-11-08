package com.yousails.chrenai.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yousails.chrenai.login.bean.UserBean;
import com.yousails.chrenai.utils.StringUtil;

import java.util.UUID;

/**
 * Created by Administrator on 2017/6/22.
 */

public class UserDBService {
    private SQLiteDatabase db;

    public UserDBService(Context context) {
        db = DBHelper.getInstance(context);
    }

    /**
     * 添加用户登录数据
     *
     * @param userBean
     */

    public void insertUser(UserBean userBean) {

        String insertSql = "insert into userTab(userId,userName,phoneNum,headUrl,gender) values (?,?,?,?,?)";
        Object[] obj = { userBean.getId(),userBean.getName(),userBean.getPhone(),
                userBean.getAvatar(),userBean.getSex()};
        try {
            db.execSQL(insertSql, obj);
        } catch (Exception e) {
            System.out.print(e.toString());
            e.printStackTrace();
        }
    }


    /**
     * 更新user数据
     *
     * @param userBean
     */
    public void updateUser(UserBean userBean){
        String sql = "update userTab set userName = ? ,gender=? where  userId=?";
        String[] args = { userBean.getName(),userBean.getSex() ,userBean.getId()};
        db.execSQL(sql, args);
    }

    /**
     * 从用户表读取某个用户；
     *
     * @param userId
     *
     */
    public UserBean getUser(String userId){
        UserBean user = null;
        if (StringUtil.isNotNull(userId)) {
            String sql = "select userId,userName,phoneNum,headUrl,gender from userTab where userId = ?";
            Cursor cursor = db.rawQuery(sql,
                    new String[] { userId });
            if (cursor!=null&&cursor.getCount() > 0 && cursor.moveToFirst()) {
                user = new UserBean();
                user.setId(cursor.getString(0));
                user.setName(cursor.getString(1));
                user.setPhone(cursor.getString(2));
                user.setAvatar(cursor.getString(3));
                user.setSex(cursor.getString(4));
            }
            cursor.close();
        }
        return user;
    }

    /**
     * 用户是否存在；
     *
     * @param id
     *
     */
    public  boolean hasUser(String id) {
        boolean has = false;
        if (id == null) {
            id = "";
        }
        String[] arg = new String[] { id };
        String sql = "select userId from userTab where userId = ?";
        try {
            Cursor cursor = db.rawQuery(sql, arg);
            if (cursor != null && cursor.getCount() > 0) {
                has = true;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return has;

    }
}
