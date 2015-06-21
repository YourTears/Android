package com.welove.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

import appLogic.AppConstant;

import com.welove.app.R;

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
	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {
			public void run() {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }

				if (AppConstant.isLogin) {
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
				}else {
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
				}

                finish();
			}
		}).start();
	}
}
