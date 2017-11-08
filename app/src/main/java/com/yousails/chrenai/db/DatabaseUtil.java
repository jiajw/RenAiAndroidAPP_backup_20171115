package com.yousails.chrenai.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Created by Administrator on 2017/7/4.
 */

public class DatabaseUtil {
    public String[] columnNames = new String[] { "default" };
    private static DatabaseUtil instance;
    private static String Tag = "DatabaseUtil";
    private static Context mContext;

    public synchronized static DatabaseUtil getInstance(Context context) {
        mContext=context;
        if (instance == null) {
            instance = new DatabaseUtil();
        }
        return instance;
    }

    private DatabaseUtil() {
        super();
    }

    public SQLiteDatabase getDB() {
        return DBHelper.getInstance(mContext);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getDB();
        if (db != null && !db.isReadOnly()) {
            try {
                return db.delete(table, whereClause, whereArgs);
            }/*
			 * catch (SQLiteDatabaseLockedException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */catch (SQLiteException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            }
        }
        return 0;
    }

    private void CheckException(String string) {
        // TODO Auto-generated method stub
//        Logger.e(Tag, string);
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        SQLiteDatabase db = getDB();
        if (db != null && !db.isReadOnly()) {
            try {
                return db.insert(table, nullColumnHack, values);
            } /*
			 * catch (SQLiteDatabaseLockedException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */catch (SQLiteException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            }
        }
        return 0;
    }

    public int update(String table, ContentValues values, String whereClause,
                      String[] whereArgs) {
        SQLiteDatabase db = getDB();
        if (db != null && !db.isReadOnly()) {
            try {
                return db.update(table, values, whereClause, whereArgs);
            } /*
			 * catch (SQLiteDatabaseLockedException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */catch (SQLiteException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            }
        }
        return 0;
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        SQLiteDatabase db = getDB();
        if (db != null) {
            try {
                return db.rawQuery(sql, selectionArgs);
            }/*
			 * catch (SQLiteDatabaseLockedException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */catch (SQLiteException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            }
        }

        return new MatrixCursor(columnNames);

    }

    public void execSQL(String sql, Object[] bindArgs) {
        SQLiteDatabase db = getDB();
        if (db != null && !db.isReadOnly()) {
            try {
                db.execSQL(sql, bindArgs);
            } /*
			 * catch (SQLiteDatabaseLockedException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */catch (SQLiteException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            }
        }
    }

    public void execSQL(String sql) {
        SQLiteDatabase db = getDB();
        if (db != null && !db.isReadOnly()) {
            try {
                db.execSQL(sql);
            } /*
			 * catch (SQLiteDatabaseLockedException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */catch (SQLiteException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            }
        }
    }

    public void beginTransaction() {
        SQLiteDatabase db = getDB();
        if (db != null) {
            try {
                db.beginTransaction();
            } catch (IllegalStateException e) {
                CheckException(e.toString());
                e.printStackTrace();
            }/*
			 * catch (SQLiteDatabaseLockedException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */catch (SQLiteException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            }
        }
    }

    public void endTransaction() {
        SQLiteDatabase db = getDB();
        if (db != null) {
            try {
                db.endTransaction();
            } catch (IllegalStateException e) {
                CheckException(e.toString());
                e.printStackTrace();
            } /*
			 * catch (SQLiteDatabaseLockedException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */catch (SQLiteException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            }
        }
    }

    public void setTransactionSuccessful() {
        SQLiteDatabase db = getDB();
        if (db != null) {
            try {
                db.setTransactionSuccessful();
            }/*
			 * catch (SQLiteDatabaseLockedException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */catch (IllegalStateException e) {
                CheckException(e.toString());
                e.printStackTrace();
            } catch (SQLiteException e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            }
        }
    }

    public static void destory() {
        if (instance != null) {
            instance.onDestory();
        }
    }

    public void onDestory() {
        instance = null;
    }

    public boolean execSQLIsSuccess(String sql, Object[] bindArgs) {
        boolean isSuccess = true;
        SQLiteDatabase db = getDB();
        if (db != null && !db.isReadOnly()) {
            try {
                db.execSQL(sql, bindArgs);
            } /*
			 * catch (SQLiteDatabaseLockedException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */catch (SQLiteException e) {
                isSuccess = false;
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                isSuccess = false;
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalStateException e) {
                isSuccess = false;
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                isSuccess = false;
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();

            }
        }
        return isSuccess;
    }

    public boolean execSQLIsSuccess(String sql) {
        boolean isSuccess = true;
        SQLiteDatabase db = getDB();
        if (db != null && !db.isReadOnly()) {
            try {
                db.execSQL(sql);
            } /*
			 * catch (SQLiteDatabaseLockedException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */catch (SQLiteException e) {
                isSuccess = false;
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (IllegalStateException e) {
                isSuccess = false;
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                isSuccess = false;
                // TODO Auto-generated catch block
                CheckException(e.toString());
                e.printStackTrace();
            }
        }
        return isSuccess;
    }
}
