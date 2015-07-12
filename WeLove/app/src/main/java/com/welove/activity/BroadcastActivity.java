package com.welove.activity;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

/**
 * Created by Long on 6/21/2015.
 */
public class BroadcastActivity extends Activity {
    protected String broadServiceName = null;
    protected Service broadcastService = null;
    protected BroadcastReceiver broadcastReceiver = null;
    protected ServiceConnection serviceConnection = null;
    protected Intent broadcastIntent = null;

    @Override
    public void onStart(){
        super.onStart();

        if(broadcastIntent != null && serviceConnection != null) {
            bindService(broadcastIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }

        if (broadServiceName != null) {
            IntentFilter intentFilter = new IntentFilter(broadServiceName);

            if(broadcastReceiver != null){
                registerReceiver(broadcastReceiver, intentFilter);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }

        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
    }
}
