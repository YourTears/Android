package com.welove.broadcast;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by Long on 6/21/2015.
 */
public class UpdateInfoService extends Service {
    public final static String ServiceName = "UpdateInfoService";

    public final static String UpdateContactList = "UpdateContactList";
    public final static String UpdateConversationList = "UpdateConversationList";
    public final static String UpdateUserDetail = "UpdateUserDetail";
    public final static String UpdateProfile = "UpdateProfile";

    public final IBinder binder = new UpdateInfoBinder();

    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }

    public class UpdateInfoBinder extends Binder {
        public UpdateInfoService getService(){
            return UpdateInfoService.this;
        }
    }
}
