/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanxin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import appLogic.AppConstant;
import common.Util;

public class DbOpenHelper extends SQLiteOpenHelper{
	private static final int DATABASE_VERSION = 1;
	private static DbOpenHelper instance = null;
    private static final String DatabaseName = "WeLove.db";
	 
	private static final String Messages_Table_Create =
            String.format("CREATE TABLE IF NOT EXISTS %s " +
                    "(%s TEXT PRIMARY KEY, %s TEXT, %s INTEGER, %s TEXT, %s BIGINT, %s INTEGER, %s INTEGER)",
                    MessageTable.TableName, MessageTable.ID, MessageTable.FriendId, MessageTable.Direction,
                    MessageTable.Body, MessageTable.Time, MessageTable.MessageType, MessageTable.IsSent);
	private static final String Messages_Index_Create =
            String.format("CREATE INDEX IF NOT EXISTS MESSAGEINDEX ON %s(%s, %s)",
                    MessageTable.TableName, MessageTable.FriendId, MessageTable.Time);

    private static final String UnreadMessageCount_Table_Create =
            String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT PRIMARY KEY, %s INTEGER)",
                    UnreadMessageTable.TableName, UnreadMessageTable.FriendId, UnreadMessageTable.UnreadCount);

    private static final String Conversations_Table_Create =
            String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT PRIMARY KEY, %s TEXT, %s BIGINT, %s INTEGER)",
                    ConversationTable.TableName, ConversationTable.FriendId, ConversationTable.Body, ConversationTable.Time, ConversationTable.UnreadCount);

    private static final String Friends_Table_Create =
            String.format("CREATE TABLE IF NOT EXISTS %s " +
                            "(%s TEXT PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER)",
                    FriendTable.TableName, FriendTable.SYS_ID, FriendTable.ID, FriendTable.Name, FriendTable.NickName, FriendTable.Phone, FriendTable.Gender,
                    FriendTable.ImageUrl, FriendTable.Sign, FriendTable.FriendStatus, FriendTable.RegionProvinceId, FriendTable.RegionCityId, FriendTable.HomeProvinceId, FriendTable.HomeCityId);

	private DbOpenHelper(Context context) {
		super(context, getDatabasePath(), null, DATABASE_VERSION);

        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
	}
	
	public static DbOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DbOpenHelper(context.getApplicationContext());
		}
		return instance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Messages_Table_Create);
        db.execSQL(Messages_Index_Create);
        db.execSQL(UnreadMessageCount_Table_Create);
        db.execSQL(Conversations_Table_Create);
        db.execSQL(Friends_Table_Create);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(getDatabasePath());
    }

    private static String getDatabasePath(){
        String databaseFolder = AppConstant.dataFolder + "/database";
        Util.createFolder(databaseFolder);

        return databaseFolder + "/" + DatabaseName;
    }
}
