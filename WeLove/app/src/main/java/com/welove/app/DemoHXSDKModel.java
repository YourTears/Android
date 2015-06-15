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
package com.welove.app;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.welove.app.db.UserDao;
import com.welove.app.fx.others.TopUser;
import com.welove.app.fx.others.TopUserDao;
import com.welove.applib.model.DefaultHXSDKModel;

import appLogic.UserInfo;

public class DemoHXSDKModel extends DefaultHXSDKModel{

    public DemoHXSDKModel(Context ctx) {
        super(ctx);
        // TODO Auto-generated constructor stub
    }

    public boolean getUseHXRoster() {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean isDebugMode(){
        return true;
    }
    
    public boolean saveContactList(List<UserInfo> contactList) {
        // TODO Auto-generated method stub
        UserDao dao = new UserDao(context);
        dao.saveContactList(contactList);
        return true;
    }

    public Map<String, UserInfo> getContactList() {
        // TODO Auto-generated method stub
        UserDao dao = new UserDao(context);
        return dao.getContactList();
    }
    public Map<String, TopUser> getTopUserList() {
        // TODO Auto-generated method stub
        TopUserDao dao = new TopUserDao(context);
        return dao.getTopUserList();
    }
    public boolean saveTopUserList(List<TopUser> contactList) {
        // TODO Auto-generated method stub
        TopUserDao dao = new TopUserDao(context);
        dao.saveTopUserList(contactList);
        return true;
    }
    
    @Override
    public String getAppProcessName() {
        // TODO Auto-generated method stub
        return "com.welove.app";
    }

    
}
