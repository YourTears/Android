package com.welove.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import appLogic.AppConstant;
import appLogic.UserInfo;
import common.ImageLoaderManager;

import com.welove.app.R;
import com.welove.broadcast.UpdateInfoService;
import com.welove.view.FriendPopupWindow;

public class UserInfoActivity extends BroadcastActivity {
    private UserInfo user = null;
    private TextView nameView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        String id = this.getIntent().getStringExtra("id");
        user = AppConstant.userManager.getUser(id);

        ImageView imageView = (ImageView) this.findViewById(R.id.iv_avatar);
        imageView.setImageDrawable(AppConstant.defaultImageDrawable);
        AppConstant.imageLoaderManager.loadImage(imageView, user.id, user.imageUrl, ImageLoaderManager.CacheMode.Memory);

        Button btn_sendmsg = (Button) this.findViewById(R.id.btn_sendmsg);
        ImageView iv_sex = (ImageView) this.findViewById(R.id.iv_sex);
        nameView = (TextView) this.findViewById(R.id.tv_name);

        if (user != null) {
            nameView.setText(user.nickName);
            if (user.gender == UserInfo.Gender.Male) {
                iv_sex.setImageResource(R.drawable.ic_sex_male);
            } else if (user.gender == UserInfo.Gender.Female) {
                iv_sex.setImageResource(R.drawable.ic_sex_female);
            }
        }

        btn_sendmsg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("id", user.id);
                intent.setClass(UserInfoActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("cacheId", user.id);
                intent.putExtra("imageUrl", user.imageUrl);
                intent.setClass(UserInfoActivity.this, BigImageActivity.class);
                startActivity(intent);
            }
        });

        final ImageView moreInfoView = (ImageView) this.findViewById(R.id.iv_detail);
        moreInfoView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FriendPopupWindow friendPopupWindow = new FriendPopupWindow(UserInfoActivity.this);
                friendPopupWindow.showPopupWindow(user, moreInfoView);
            }

        });

        initBroadcastService();
    }

    private void initBroadcastService(){
        broadServiceName = UpdateInfoService.ServiceName;
        broadcastIntent = new Intent(UserInfoActivity.this, UpdateInfoService.class);
        broadcastReceiver = new BroadcastReceiver(){
            public void onReceive(Context context, Intent intent) {
                boolean updateUserDetail = intent.getBooleanExtra(UpdateInfoService.UpdateUserDetail, false);

                if(updateUserDetail){
                    String userId = intent.getStringExtra("id");
                    if(userId.equals(user.id)){
                        if(nameView != null){
                            nameView.setText(user.nickName);
                        }
                    }
                }
            }
        };
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                broadcastService = ((UpdateInfoService.UpdateInfoBinder) binder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                broadcastService = null;
            }
        };
    }

    public void back(View view) {
        finish();
    }
}