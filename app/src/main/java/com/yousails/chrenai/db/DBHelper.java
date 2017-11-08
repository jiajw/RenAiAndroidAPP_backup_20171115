package com.yousails.chrenai.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/15.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "chrenai.db";
    private static final int VERSION = 1;

    private static DBHelper mInstance;
    private static SQLiteDatabase database;

    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static SQLiteDatabase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context);
        }
        if (database == null) {
            database = mInstance.getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        initDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    /**
     * 初始化本地数据库
     */
    public void initDB(SQLiteDatabase db) {
        /*//城市地区表
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + "district(districtid INTEGER, districtname VARCHAR(30),cityid VARCHAR(30),cityname VARCHAR(20),"
                + "PRIMARY KEY(districtid))");
*/

        //user信息表
        db.execSQL("CREATE TABLE IF NOT EXISTS userTab(userId VARCHAR(20),userToken VARCHAR(30),userName VARCHAR(20),identityNo VARCHAR(20),headUrl VARCHAR(50),gender VARCHAR(10),"
                +"city VARCHAR(10),phoneNum VARCHAR(20),isVip INTEGER,lastActivedTime DATETIME,religion VARCHAR(20),degree VARCHAR(20),work VARCHAR(30),skills VARCHAR(30),status VARCHAR(20),PRIMARY KEY(userId))");

        //关键词表
        db.execSQL("CREATE TABLE IF NOT EXISTS keyWordTab(keyId VARCHAR(20),userId VARCHAR(20),content VARCHAR(20),PRIMARY KEY(keyId))");

        //广告栏表
        db.execSQL("CREATE TABLE IF NOT EXISTS bannerTab(bId VARCHAR(20) ,title VARCHAR(20),image VARCHAR(20),link VARCHAR(20),PRIMARY KEY(bId))");

        //活动详情表
        db.execSQL("CREATE TABLE IF NOT EXISTS activitiesTab(id VARCHAR(20),userId VARCHAR(20),userName VARCHAR(20),headUrl VARCHAR(50),isCertificated VARCHAR(10),isVip VARCHAR(10),workHours VARCHAR(10),level VARCHAR(10),title VARCHAR(20),imageUrl VARCHAR(50),district VARCHAR(20),distance VARCHAR(20),address VARCHAR(20),startTime VARCHAR(20),categoryId VARCHAR(10),cityId VARCHAR(10),PRIMARY KEY(id))");

        //城市
        db.execSQL("CREATE TABLE IF NOT EXISTS cityTab(cId VARCHAR(20) ,name VARCHAR(20),parentId VARCHAR(10),initial VARCHAR(10),initials VARCHAR(10),pinyin VARCHAR(20),suffix VARCHAR(10),code INTEGER, PRIMARY KEY(cId))");

        //首页顶部菜单category
        db.execSQL("CREATE TABLE IF NOT EXISTS categoryTab(cId VARCHAR(20),name VARCHAR(20),PRIMARY KEY(cId))");

        //学历
        db.execSQL("CREATE TABLE IF NOT EXISTS dgreesTab(cId VARCHAR(20),name VARCHAR(20),selected VARCHAR(10),PRIMARY KEY(cId))");

        //宗教信仰
        db.execSQL("CREATE TABLE IF NOT EXISTS religionsTab(cId VARCHAR(20),name VARCHAR(20),selected VARCHAR(10),PRIMARY KEY(cId))");

    }
}
