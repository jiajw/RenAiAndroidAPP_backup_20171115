package com.yousails.chrenai.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yousails.chrenai.config.AppPreference;
import com.yousails.chrenai.home.bean.KeyWordBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/4.
 */

public class KeyWordDBService {
//    private SQLiteDatabase db;
    private Context context;
    public KeyWordDBService(Context context){
        //    db = DBHelper.getInstance(context);
        this.context=context;

    }

    /**
     * 插入关键词
     *
     * @param keyWordBean
     */
    public void insertKeyWords(KeyWordBean keyWordBean) {
        try {
            String table = "keyWordTab";
            String where = "keyId= ? and userId=?";
            String userId=AppPreference.getInstance(context).readUerId();
            String [] args = { keyWordBean.getId(),userId};
            ContentValues values = new ContentValues();
            values.put("keyId", keyWordBean.getId());
            values.put("content", keyWordBean.getContent());
            values.put("userId", userId);

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
    public  List<KeyWordBean> getKeyWords(){
        List<KeyWordBean>keyWordList=null;
        try {
            String sql = "select keyId,content from keyWordTab where userId=?";
            String id=AppPreference.getInstance(context).readUerId();
            String[] args = { AppPreference.getInstance(context).readUerId() };
            Cursor cursor = DatabaseUtil.getInstance(context).rawQuery(sql, args);
            if (cursor != null && cursor.getCount() > 0) {
                keyWordList=new ArrayList<KeyWordBean>();
                while (cursor.moveToNext()) {
                    KeyWordBean keyWordBean=new KeyWordBean();
                    keyWordBean.setId(cursor.getString(0));
                    keyWordBean.setContent(cursor.getString(1));
                    keyWordList.add(keyWordBean);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return keyWordList;
    }
}
