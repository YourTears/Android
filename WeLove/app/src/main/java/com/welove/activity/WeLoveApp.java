package com.welove.activity;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

import appLogic.AppConstant;
import chat.leanchatlib.controller.ChatManager;

/**
 * Created by Long on 6/27/2015.
 */
public class WeLoveApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final WeLoveApp app = this;

        new Thread() {
            @Override
            public void run() {
                // chat library related
                AVOSCloud.initialize(app, "g71g5iqo3dpe3o09mwho925607kdsuy4foevpcwnqfny8bkr",
                        "98s54ea6v0lbixzpbb2x339cfn4dhx19zoixixmurwwic1cd");

                ChatManager chatManager = ChatManager.getInstance();
                chatManager.init(app);
            }
        }.run();
    }
}
