package com.fanxin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import appLogic.UserInfo;

/**
 * Created by Long on 6/2/2015.
 */
public class UserTable {
    public static final String TableName = "Friends";
    public static final String SYS_ID = "SYS_ID";
    public static final String ID = "ID";
    public static final String Name = "Name";
    public static final String NickName = "NickName";
    public static final String Gender = "Gender";
    public static final String ImageUrl = "ImageUrl";
    public static final String Sign = "Sign";
    public static final String FriendStatus = "FriendStatus";
    public static final String RegionProvinceId = "RegionProvinceId";
    public static final String RegionCityId = "RegionCityId";
    public static final String HomeProvinceId = "HomeProvinceId";
    public static final String HomeCityId = "HomeCityId";

    private DbOpenHelper dbHelper;
    private SQLiteDatabase dbWriter = null, dbReader = null;

    private UserTable(Context context) {
        dbHelper = DbOpenHelper.getInstance(context);
    }

    private static UserTable instance = null;
    public static UserTable getInstance(Context context){
        if(instance == null)
            instance = new UserTable(context);

        return instance;
    }

    public synchronized boolean addOrReplaceFriend(UserInfo friend) {
        try {
            dbWriter = dbHelper.getWritableDatabase();

            if (dbWriter.isOpen()) {
                ContentValues content = new ContentValues();
                content.put(SYS_ID, friend.sys_id);
                content.put(ID, friend.id);
                content.put(Name, friend.name);
                content.put(NickName, friend.nickName);
                content.put(Gender, UserInfo.parseGender(friend.gender));
                content.put(ImageUrl, friend.imageUrl);
                content.put(Sign, friend.sign);
                content.put(FriendStatus, UserInfo.parseFriendStatus(friend.friendStatus));
                content.put(RegionProvinceId, friend.regionProvinceId);
                content.put(RegionCityId, friend.regionCityId);
                content.put(HomeProvinceId, friend.homeProvinceId);
                content.put(HomeCityId, friend.homeCityId);

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

    public List<UserInfo> getFriends() {
        List<UserInfo> friends = new ArrayList<>();

        try {
            dbReader = dbHelper.getReadableDatabase();
            if (dbReader.isOpen()) {
                String query = String.format("SELECT * FROM " + TableName);

                Cursor cursor = dbReader.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    UserInfo friend = new UserInfo();

                    friend.sys_id = cursor.getString(cursor.getColumnIndex(SYS_ID));
                    friend.id = cursor.getString(cursor.getColumnIndex(ID));
                    friend.name = cursor.getString(cursor.getColumnIndex(Name));
                    friend.nickName = cursor.getString(cursor.getColumnIndex(NickName));
                    friend.gender = UserInfo.parseGender(cursor.getInt(cursor.getColumnIndex(Gender)));
                    friend.imageUrl = cursor.getString(cursor.getColumnIndex(ImageUrl));
                    friend.sign = cursor.getString(cursor.getColumnIndex(Sign));
                    friend.friendStatus = UserInfo.parseFriendStatus(cursor.getInt(cursor.getColumnIndex(FriendStatus)));
                    friend.regionProvinceId = cursor.getInt(cursor.getColumnIndex(RegionProvinceId));
                    friend.regionCityId = cursor.getInt(cursor.getColumnIndex(RegionCityId));
                    friend.homeProvinceId = cursor.getInt(cursor.getColumnIndex(HomeProvinceId));
                    friend.homeCityId = cursor.getInt(cursor.getColumnIndex(HomeCityId));

                    friends.add(0, friend);
                }

                cursor.close();
            }
        } catch (Exception e) {
            Log.e("FriendTable", e.getMessage());
        } finally {
            dbReader.close();
            dbReader = null;
        }

        return friends;
    }


    public void deleteFriend(String friendId) {
        try {
            dbWriter = dbHelper.getWritableDatabase();
            if (dbWriter.isOpen()) {
                String query = "DELETE FROM " + TableName + " WHERE ID = '" + friendId + "'";

                dbWriter.execSQL(query);
            }
        } catch (Exception e) {
            Log.e("FriendTable", e.getMessage());
        } finally {
            dbWriter.close();
            dbWriter = null;
        }
    }

    public void deleteFriends() {
        try {
            dbWriter = dbHelper.getWritableDatabase();
            if (dbWriter.isOpen()) {
                String query = "DELETE FROM " + TableName;

                dbWriter.execSQL(query);
            }
        } catch (Exception e) {
            Log.e("FriendTable", e.getMessage());
        } finally {
            dbWriter.close();
            dbWriter = null;
        }
    }
}
