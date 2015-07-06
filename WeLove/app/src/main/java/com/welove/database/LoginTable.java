package com.welove.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

/**
 * Created by Long on 7/6/2015.
 */
public class LoginTable {
    public static final String TableName = "Login";
    public static final String Idx = "Idx";
    public static final String Id = "Id";
    public static final String Password = "Password";
    public static final String Time = "Time";

    private DbOpenHelper dbHelper;
    private SQLiteDatabase dbWriter = null, dbReader = null;

    private LoginTable(Context context) {
        dbHelper = DbOpenHelper.getInstance(context);
    }

    private static LoginTable instance = null;
    public static LoginTable getInstance(Context context){
        if(instance == null)
            instance = new LoginTable(context);

        return instance;
    }

    public synchronized boolean addOrReplaceLogin(String id, String password) {
        try {
            dbWriter = dbHelper.getWritableDatabase();

            if(dbWriter.isOpen()) {
                dbWriter = dbHelper.getWritableDatabase();

                ContentValues content = new ContentValues();
                content.put(Idx, 0);
                content.put(Id, id);
                content.put(Password, password);
                content.put(Time, (new Date().getTime()));

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

    public String getLoginId()
    {
        try {
            dbReader = dbHelper.getReadableDatabase();
            if (dbReader.isOpen()) {
                String query = String.format("SELECT * FROM " + TableName);

                Cursor cursor = dbReader.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    return cursor.getString(cursor.getColumnIndex(Id));
                }
            }
        }
        catch (Exception e){

        }
        finally {
            dbReader.close();
            dbReader = null;
        }

        return null;
    }
}
