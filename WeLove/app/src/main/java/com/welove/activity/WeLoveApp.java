package com.welove.activity;

import android.app.Application;

import chat.ConversationProxy;

/**
 * Created by Long on 6/27/2015.
 */
public class WeLoveApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ConversationProxy.initApp(this);
    }
}
