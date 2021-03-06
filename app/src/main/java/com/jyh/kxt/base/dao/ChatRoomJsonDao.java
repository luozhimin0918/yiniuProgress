package com.jyh.kxt.base.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jyh.kxt.chat.json.ChatRoomJson;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CHAT_ROOM_BEAN".
*/
public class ChatRoomJsonDao extends AbstractDao<ChatRoomJson, Void> {

    public static final String TABLENAME = "CHAT_ROOM_BEAN";

    /**
     * Properties of entity ChatRoomJson.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property MsgSendStatus = new Property(0, int.class, "msgSendStatus", false, "MSG_SEND_STATUS");
        public final static Property ForegoingChatId = new Property(1, String.class, "foregoingChatId", false, "FOREGOING_CHAT_ID");
        public final static Property Id = new Property(2, String.class, "id", false, "ID");
        public final static Property Sender = new Property(3, String.class, "sender", false, "SENDER");
        public final static Property Receiver = new Property(4, String.class, "receiver", false, "RECEIVER");
        public final static Property Content = new Property(5, String.class, "content", false, "CONTENT");
        public final static Property Nickname = new Property(6, String.class, "nickname", false, "NICKNAME");
        public final static Property Avatar = new Property(7, String.class, "avatar", false, "AVATAR");
        public final static Property Datetime = new Property(8, String.class, "datetime", false, "DATETIME");
    }


    public ChatRoomJsonDao(DaoConfig config) {
        super(config);
    }
    
    public ChatRoomJsonDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHAT_ROOM_BEAN\" (" + //
                "\"MSG_SEND_STATUS\" INTEGER NOT NULL ," + // 0: msgSendStatus
                "\"FOREGOING_CHAT_ID\" TEXT," + // 1: foregoingChatId
                "\"ID\" TEXT," + // 2: id
                "\"SENDER\" TEXT," + // 3: sender
                "\"RECEIVER\" TEXT," + // 4: receiver
                "\"CONTENT\" TEXT," + // 5: content
                "\"NICKNAME\" TEXT," + // 6: nickname
                "\"AVATAR\" TEXT," + // 7: avatar
                "\"DATETIME\" TEXT);"); // 8: datetime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHAT_ROOM_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ChatRoomJson entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getMsgSendStatus());
 
        String foregoingChatId = entity.getForegoingChatId();
        if (foregoingChatId != null) {
            stmt.bindString(2, foregoingChatId);
        }
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(3, id);
        }
 
        String sender = entity.getSender();
        if (sender != null) {
            stmt.bindString(4, sender);
        }
 
        String receiver = entity.getReceiver();
        if (receiver != null) {
            stmt.bindString(5, receiver);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(6, content);
        }
 
        String nickname = entity.getNickname();
        if (nickname != null) {
            stmt.bindString(7, nickname);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(8, avatar);
        }
 
        String datetime = entity.getDatetime();
        if (datetime != null) {
            stmt.bindString(9, datetime);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ChatRoomJson entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getMsgSendStatus());
 
        String foregoingChatId = entity.getForegoingChatId();
        if (foregoingChatId != null) {
            stmt.bindString(2, foregoingChatId);
        }
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(3, id);
        }
 
        String sender = entity.getSender();
        if (sender != null) {
            stmt.bindString(4, sender);
        }
 
        String receiver = entity.getReceiver();
        if (receiver != null) {
            stmt.bindString(5, receiver);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(6, content);
        }
 
        String nickname = entity.getNickname();
        if (nickname != null) {
            stmt.bindString(7, nickname);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(8, avatar);
        }
 
        String datetime = entity.getDatetime();
        if (datetime != null) {
            stmt.bindString(9, datetime);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public ChatRoomJson readEntity(Cursor cursor, int offset) {
        ChatRoomJson entity = new ChatRoomJson( //
            cursor.getInt(offset + 0), // msgSendStatus
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // foregoingChatId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // id
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // sender
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // receiver
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // content
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // nickname
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // avatar
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // datetime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ChatRoomJson entity, int offset) {
        entity.setMsgSendStatus(cursor.getInt(offset + 0));
        entity.setForegoingChatId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSender(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setReceiver(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setContent(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setNickname(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setAvatar(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setDatetime(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(ChatRoomJson entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(ChatRoomJson entity) {
        return null;
    }

    @Override
    public boolean hasKey(ChatRoomJson entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
