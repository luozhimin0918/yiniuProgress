package com.jyh.kxt.base.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by Mr'Dai on 2016/10/17.
 */

public class DBManager {
    public final static String dbName = "kxt_emoje_db";
    private static DBManager mInstance;
    private MyDevOpenHelper openHelper;
    private Context context;


    public DBManager(Context context) {
        this.context = context;
        openHelper = new MyDevOpenHelper(context, dbName, null);

    }

    public DaoSession getDaoSessionRead() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        return daoSession;
    }

    public DaoSession getDaoSessionWrit() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        return daoSession;
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                    QueryBuilder.LOG_SQL = true;
                    QueryBuilder.LOG_VALUES = true;
                }
            }
        }
        return mInstance;
    }


    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new MyDevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new MyDevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }


    /**
     * WARNING: Drops all table on Upgrade! Use only during development.
     */
    public class MyDevOpenHelper extends DaoMaster.OpenHelper {
        public MyDevOpenHelper(Context context, String name) {
            super(context, name);
        }

        public MyDevOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            upgradeDB(db, oldVersion, newVersion);
        }

        /**
         * 数据更新方
         *
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        public void upgradeDB(Database db, int oldVersion, int newVersion) {
            //oldVersion 1  newVersion 2
            //case  对应版本为旧版本 需要更改的，如果新版本为2  旧版本为1  则case对应版本1
            for (int i = oldVersion; i < newVersion; i++) {
                switch (i) {
                    case 1:
                        upgradeToVersion1(db);
                        break;
                    default:
                        break;
                }
            }
        }

        private void upgradeToVersion1(Database db) {
           /* try
            {
                String sql1 = "ALTER TABLE MAJOR_CAROUSEL ADD COLUMN URL VARCHAR";
                db.execSQL(sql1);
            } catch (SQLException e)
            {
                e.printStackTrace();
            }

            try
            {
                String sql2 = "ALTER TABLE MAJOR_ITEM_CONTENT2 ADD COLUMN URL VARCHAR";
                db.execSQL(sql2);
            } catch (SQLException e)
            {
                e.printStackTrace();
            }

            try
            {
                String sql3 = "ALTER TABLE MAJOR_ITEM_CONTENT ADD COLUMN URL VARCHAR";
                db.execSQL(sql3);
            } catch (SQLException e)
            {
                e.printStackTrace();
            }*/
        }
    }
}
