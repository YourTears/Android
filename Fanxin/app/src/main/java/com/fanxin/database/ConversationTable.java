package com.fanxin.database;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import appLogic.Conversation;
import appLogic.Message;

/**
 * Created by Long on 5/26/2015.
 */
public class ConversationTable {
    public static final String TableName = "Conversations";
    public static final String FriendId = "FriendId";
    public static final String Body = "Body";
    public static final String Time = "Time";
    public static final String UnreadCount = "UnreadCount";

    private DbOpenHelper dbHelper;
    private SQLiteDatabase dbWriter = null, dbReader = null;

    private ConversationTable(Context context) {
        dbHelper = DbOpenHelper.getInstance(context);
    }

    private static ConversationTable instance = null;
    public static ConversationTable getInstance(Context context){
        if(instance == null)
            instance = new ConversationTable(context);

        return instance;
    }

    public synchronized boolean replaceConversation(Conversation conversation) {
        try {
            dbWriter = dbHelper.getWritableDatabase();

            if(dbWriter.isOpen()) {
                dbWriter = dbHelper.getWritableDatabase();

                ContentValues content = new ContentValues();
                content.put(FriendId, conversation.friendId);
                content.put(Body, conversation.body);
                content.put(Time, conversation.time);
                content.put(UnreadCount, conversation.unreadCount);

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

    public List<Conversation> getConversations()
    {
        List<Conversation> conversations = new ArrayList<>();

        try {
            dbReader = dbHelper.getReadableDatabase();
            if (dbReader.isOpen()) {
                String query = String.format("SELECT * FROM " + TableName + " ORDER BY Time DESC");

                Cursor cursor = dbReader.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    Conversation conversation = new Conversation();

                    conversation.friendId = cursor.getString(cursor.getColumnIndex(FriendId));
                    conversation.body = cursor.getString(cursor.getColumnIndex(Body));
                    conversation.time = cursor.getLong(cursor.getColumnIndex(Time));
                    conversation.unreadCount = cursor.getInt(cursor.getColumnIndex(UnreadCount));

                    conversations.add(conversation);
                }
            }
        }
        catch (Exception e){

        }
        finally {
            dbReader.close();
            dbReader = null;
        }

        return conversations;
    }

    public void deleteConversation(String friendId) {
        try {
            dbWriter = dbHelper.getWritableDatabase();
            if (dbWriter.isOpen()) {
                String query = "DELETE FROM " + TableName + " WHERE FriendId = '" + friendId + "'";

                dbWriter.execSQL(query);
            }
        } catch (Exception e) {
            Log.e("ConversationTable", e.getMessage());
        } finally {
            dbWriter.close();
            dbWriter = null;
        }
    }
}
