package com.jyh.kxt.base.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jyh.kxt.main.json.NewsJson;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NEWS_BEAN".
*/
public class NewsJsonDao extends AbstractDao<NewsJson, Void> {

    public static final String TABLENAME = "NEWS_BEAN";

    /**
     * Properties of entity NewsJson.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Title = new Property(0, String.class, "title", false, "TITLE");
        public final static Property Picture = new Property(1, String.class, "picture", false, "PICTURE");
        public final static Property Author = new Property(2, String.class, "author", false, "AUTHOR");
        public final static Property Datetime = new Property(3, String.class, "datetime", false, "DATETIME");
        public final static Property Type = new Property(4, String.class, "type", false, "TYPE");
        public final static Property Href = new Property(5, String.class, "href", false, "HREF");
        public final static Property O_action = new Property(6, String.class, "o_action", false, "O_ACTION");
        public final static Property O_class = new Property(7, String.class, "o_class", false, "O_CLASS");
        public final static Property O_id = new Property(8, String.class, "o_id", false, "O_ID");
        public final static Property DataType = new Property(9, int.class, "dataType", false, "DATA_TYPE");
    }


    public NewsJsonDao(DaoConfig config) {
        super(config);
    }
    
    public NewsJsonDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NEWS_BEAN\" (" + //
                "\"TITLE\" TEXT," + // 0: title
                "\"PICTURE\" TEXT," + // 1: picture
                "\"AUTHOR\" TEXT," + // 2: author
                "\"DATETIME\" TEXT," + // 3: datetime
                "\"TYPE\" TEXT," + // 4: type
                "\"HREF\" TEXT," + // 5: href
                "\"O_ACTION\" TEXT," + // 6: o_action
                "\"O_CLASS\" TEXT," + // 7: o_class
                "\"O_ID\" TEXT," + // 8: o_id
                "\"DATA_TYPE\" INTEGER NOT NULL );"); // 9: dataType
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NEWS_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, NewsJson entity) {
        stmt.clearBindings();
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(1, title);
        }
 
        String picture = entity.getPicture();
        if (picture != null) {
            stmt.bindString(2, picture);
        }
 
        String author = entity.getAuthor();
        if (author != null) {
            stmt.bindString(3, author);
        }
 
        String datetime = entity.getDatetime();
        if (datetime != null) {
            stmt.bindString(4, datetime);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(5, type);
        }
 
        String href = entity.getHref();
        if (href != null) {
            stmt.bindString(6, href);
        }
 
        String o_action = entity.getO_action();
        if (o_action != null) {
            stmt.bindString(7, o_action);
        }
 
        String o_class = entity.getO_class();
        if (o_class != null) {
            stmt.bindString(8, o_class);
        }
 
        String o_id = entity.getO_id();
        if (o_id != null) {
            stmt.bindString(9, o_id);
        }
        stmt.bindLong(10, entity.getDataType());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, NewsJson entity) {
        stmt.clearBindings();
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(1, title);
        }
 
        String picture = entity.getPicture();
        if (picture != null) {
            stmt.bindString(2, picture);
        }
 
        String author = entity.getAuthor();
        if (author != null) {
            stmt.bindString(3, author);
        }
 
        String datetime = entity.getDatetime();
        if (datetime != null) {
            stmt.bindString(4, datetime);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(5, type);
        }
 
        String href = entity.getHref();
        if (href != null) {
            stmt.bindString(6, href);
        }
 
        String o_action = entity.getO_action();
        if (o_action != null) {
            stmt.bindString(7, o_action);
        }
 
        String o_class = entity.getO_class();
        if (o_class != null) {
            stmt.bindString(8, o_class);
        }
 
        String o_id = entity.getO_id();
        if (o_id != null) {
            stmt.bindString(9, o_id);
        }
        stmt.bindLong(10, entity.getDataType());
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public NewsJson readEntity(Cursor cursor, int offset) {
        NewsJson entity = new NewsJson( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // title
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // picture
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // author
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // datetime
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // type
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // href
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // o_action
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // o_class
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // o_id
            cursor.getInt(offset + 9) // dataType
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, NewsJson entity, int offset) {
        entity.setTitle(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setPicture(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAuthor(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDatetime(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setType(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setHref(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setO_action(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setO_class(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setO_id(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDataType(cursor.getInt(offset + 9));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(NewsJson entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(NewsJson entity) {
        return null;
    }

    @Override
    public boolean hasKey(NewsJson entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
