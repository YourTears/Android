package com.welove.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

import appLogic.AppConstant;
import appLogic.AppNotification;
import chat.leanchatlib.controller.ChatManager;
import common.Util;

import com.welove.app.R;
import com.welove.database.DbOpenHelper;
import com.welove.database.LoginTable;

/**
 * 开屏页
 */
public class SplashActivity extends Activity {

	private static final int sleepTime = 2000;

	@Override
	protected void onCreate(Bundle arg0) {
	    final View view = View.inflate(this, R.layout.activity_splash, null);
		setContentView(view);
		super.onCreate(arg0);

		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		view.startAnimation(animation);

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

	}

	@Override
	protected void onStart() {
		super.onStart();

		initialize();
		ChatManager.getInstance().init(this);

		AppConstant.id = LoginTable.getInstance(this).getLoginId();

		new Thread(new Runnable() {
			public void run() {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }

				if (AppConstant.id == null) {
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
				}else {
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
				}

				finish();
			}
		}).start();
	}

	private void initialize()
	{
		AppConstant.dataFolder = Util.getAppFilePath(this);
		AppConstant.cacheFolder = Util.getAppCachePath(this);
		AppConstant.imageFolder = AppConstant.dataFolder + "/images/";

		Util.createFolder(AppConstant.dataFolder);
		Util.createFolder(AppConstant.imageFolder);
		Util.createFolder(AppConstant.cacheFolder + "/images");

		//DbOpenHelper.getInstance(this).deleteDatabase(this);
	}
}
