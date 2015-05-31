package com.fanxin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import appLogic.Message;

/**
 * Created by Long on 5/24/2015.
 */
public class MessageTable {
    public static final String TableName = "Messages";
    public static final String ID = "ID";
    public static final String FriendId = "FriendId";
    public static final String Direction = "Direction";
    public static final String Body = "Body";
    public static final String MessageType = "MessageType";
    public static final String Time = "Time";
    public static final String IsSent = "IsSent";

    private static final int MessageCount = 20;

    private DbOpenHelper dbHelper;
    private SQLiteDatabase dbWriter = null, dbReader = null;

    private MessageTable(Context context) {
        dbHelper = DbOpenHelper.getInstance(context);
    }

    private static MessageTable instance = null;
    public static MessageTable getInstance(Context context){
        if(instance == null)
            instance = new MessageTable(context);

        return instance;
    }

    public synchronized boolean insertMessage(Message message) {
        try {
            dbWriter = dbHelper.getWritableDatabase();

            if (dbWriter.isOpen()) {
                ContentValues content = new ContentValues();
                content.put(ID, message.id.toString());
                content.put(FriendId, message.friendId);
                content.put(Direction, convertDirection(message.direction));
                content.put(Body, message.body);
                content.put(MessageType, convertType(message.type));
                content.put(Time, message.time);
                content.put(IsSent, message.isSent);

                if (dbWriter.insert(TableName, null, content) != -1)
                    return true;
            }
        } catch (Exception e) {

        } finally {
            dbWriter.close();
            dbWriter = null;
        }
        return false;
    }

    public List<Message> getMessages(String friendId, long endTime) {
        List<Message> messages = new ArrayList<>();

        try {
            dbReader = dbHelper.getReadableDatabase();
            if (dbReader.isOpen()) {
                String query = String.format("SELECT %s, %s, %s, %s, %s, %s, %s FROM " + TableName + " WHERE FriendId = '" + friendId + "' AND Time <= " + endTime + " ORDER BY Time DESC LIMIT %d",
                        ID, FriendId, Direction, Body, MessageType, Time, IsSent, MessageCount);

                Cursor cursor = dbReader.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    Message message = new Message();

                    message.id = UUID.fromString(cursor.getString(cursor.getColumnIndex(ID)));
                    message.friendId = cursor.getString(cursor.getColumnIndex(FriendId));
                    message.direction = restoreDirection(cursor.getInt(cursor.getColumnIndex(Direction)));
                    message.body = cursor.getString(cursor.getColumnIndex(Body));
                    message.type = restoreType(cursor.getInt(cursor.getColumnIndex(MessageType)));
                    message.time = cursor.getLong(cursor.getColumnIndex(Time));
                    message.isSent = cursor.getInt(cursor.getColumnIndex(IsSent)) == 0 ? false : true;

                    messages.add(0, message);
                }

                cursor.close();
            }
        } catch (Exception e) {
            Log.e("MessageTable", e.getMessage());
        } finally {
            dbReader.close();
            dbReader = null;
        }

        return messages;
    }

    public void deleteMessages(String friendId) {
        try {
            dbWriter = dbHelper.getWritableDatabase();
            if (dbWriter.isOpen()) {
                String query = "DELETE FROM " + TableName + " WHERE FriendId = '" + friendId + "'";

                dbWriter.execSQL(query);
            }
        } catch (Exception e) {
            Log.e("MessageTable", e.getMessage());
        } finally {
            dbWriter.close();
            dbWriter = null;
        }
    }

    public void deleteMessage(String messageId) {
        try {
            dbWriter = dbHelper.getWritableDatabase();
            if (dbWriter.isOpen()) {
                String query = "DELETE FROM " + TableName + " WHERE ID = " + messageId;

                dbWriter.execSQL(query);
            }
        } catch (Exception e) {
            Log.e("MessageTable", e.getMessage());
        } finally {
            dbWriter.close();
            dbWriter = null;
        }
    }

    private int convertDirection(Message.Direction direction) {
        if (direction == Message.Direction.SEND)
            return 0;
        return 1;
    }

    private int convertType(Message.MessageType type) {
        if (type == Message.MessageType.TEXT)
            return 0;
        else if (type == Message.MessageType.IMAGE)
            return 1;
        else if (type == Message.MessageType.AUDIO)
            return 2;
        else if (type == Message.MessageType.VIDEO)
            return 3;
        return 4;
    }

    private Message.Direction restoreDirection(int direction) {
        if (direction == 0)
            return Message.Direction.SEND;
        return Message.Direction.RECEIVE;
    }

    private Message.MessageType restoreType(int type) {
        if (type == 0)
            return Message.MessageType.TEXT;
        else if (type == 1)
            return Message.MessageType.IMAGE;
        else if (type == 2)
            return Message.MessageType.AUDIO;
        else if (type == 3)
            return Message.MessageType.VIDEO;
        return Message.MessageType.TEXT;
    }
}
