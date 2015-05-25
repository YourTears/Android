package com.fanxin.database;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import appLogic.Message;

/**
 * Created by Long on 5/24/2015.
 */
public class UnreadMessageTable {
    public static final String TableName = "UnreadMessage";
    public static final String FriendId = "FriendId";
    public static final String UnreadCount = "UnreadCount";

    private DbOpenHelper dbHelper;
    private SQLiteDatabase dbWriter = null, dbReader = null;

    public UnreadMessageTable(Context context) {
        dbHelper = DbOpenHelper.getInstance(context);
    }

    public synchronized boolean replaceMessageCount(String friendId, int unreadCount) {
        try {
            if(dbWriter.isOpen()) {
                dbWriter = dbHelper.getWritableDatabase();

                ContentValues content = new ContentValues();
                content.put(FriendId, friendId);
                content.put(UnreadCount, unreadCount);

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

    public int getUnreadCount(String friendId)
    {
        try {
            dbReader = dbHelper.getReadableDatabase();
            if (dbReader.isOpen()) {
                String query = String.format("SELECT {3} FROM {0} WHERE {1} = {2}",
                        TableName, FriendId, friendId, UnreadCount);

                Cursor cursor = dbReader.rawQuery(query, null);

                if (cursor.moveToNext()) {
                    return cursor.getInt(cursor.getColumnIndex(FriendId));
                }
            }
        }
        catch (Exception e){

        }
        finally {
            dbReader.close();
            dbReader = null;
        }

        return 0;
    }
}