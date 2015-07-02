package com.welove.database;

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
    public static final String ExternalID = "ExternalID";
    public static final String FriendId = "FriendId";
    public static final String Direction = "Direction";
    public static final String Body = "Body";
    public static final String MessageType = "MessageType";
    public static final String Time = "Time";
    public static final String Status = "Status";

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
                content.put(ExternalID, message.externalId);
                content.put(FriendId, message.friendId);
                content.put(Direction, Message.parseDirection(message.direction));
                content.put(Body, message.body);
                content.put(MessageType, Message.parseType(message.type));
                content.put(Time, message.time);
                content.put(Status, Message.parseStatus(message.status));

                if (dbWriter.replace(TableName, null, content) != -1)
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
                String query = String.format("SELECT * FROM " + TableName + " WHERE FriendId = '" + friendId + "' AND Time <= " + endTime + " ORDER BY Time DESC LIMIT %d",
                        MessageCount);

                Cursor cursor = dbReader.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    Message message = new Message();

                    message.id = UUID.fromString(cursor.getString(cursor.getColumnIndex(ID)));
                    message.externalId = cursor.getString(cursor.getColumnIndex(ExternalID));
                    message.friendId = cursor.getString(cursor.getColumnIndex(FriendId));
                    message.direction = Message.parseDirection(cursor.getInt(cursor.getColumnIndex(Direction)));
                    message.body = cursor.getString(cursor.getColumnIndex(Body));
                    message.type = Message.parseType(cursor.getInt(cursor.getColumnIndex(MessageType)));
                    message.time = cursor.getLong(cursor.getColumnIndex(Time));
                    message.status = Message.parseStatus(cursor.getInt(cursor.getColumnIndex(Status)));

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

    public Message getMessageByExternalId(String externalId){
        try {
            dbReader = dbHelper.getReadableDatabase();
            if (dbReader.isOpen()) {
                String query = String.format("SELECT * FROM " + TableName + " WHERE ExternalId = '" + externalId + "'",
                        MessageCount);

                Cursor cursor = dbReader.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    Message message = new Message();

                    message.id = UUID.fromString(cursor.getString(cursor.getColumnIndex(ID)));
                    message.externalId = cursor.getString(cursor.getColumnIndex(ExternalID));
                    message.friendId = cursor.getString(cursor.getColumnIndex(FriendId));
                    message.direction = Message.parseDirection(cursor.getInt(cursor.getColumnIndex(Direction)));
                    message.body = cursor.getString(cursor.getColumnIndex(Body));
                    message.type = Message.parseType(cursor.getInt(cursor.getColumnIndex(MessageType)));
                    message.time = cursor.getLong(cursor.getColumnIndex(Time));
                    message.status = Message.parseStatus(cursor.getInt(cursor.getColumnIndex(Status)));

                    return message;
                }

                cursor.close();
            }
        } catch (Exception e) {
            Log.e("MessageTable", e.getMessage());
        } finally {
            dbReader.close();
            dbReader = null;
        }

        return null;
    }
}