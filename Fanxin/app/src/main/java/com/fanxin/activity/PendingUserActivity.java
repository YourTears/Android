package com.fanxin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanxin.app.R;

import appLogic.AppConstant;
import appLogic.UserInfo;
import common.ImageLoaderManager;

public class PendingUserActivity extends Activity {
    private UserInfo friend = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendinguser);

        String id = this.getIntent().getStringExtra("id");
        friend = AppConstant.userManager.getUser(id);

        ImageView imageView = (ImageView) this.findViewById(R.id.iv_avatar);
        imageView.setImageDrawable(AppConstant.defaultImageDrawable);
        AppConstant.imageLoaderManager.loadImage(imageView, friend.id, friend.imageUrl, ImageLoaderManager.CacheMode.Memory);

        Button requestButton = (Button) this.findViewById(R.id.btn_sendmsg);
        requestButton.setClickable(false);

        ImageView iv_sex = (ImageView) this.findViewById(R.id.iv_sex);
        TextView tv_name = (TextView) this.findViewById(R.id.tv_name);
        TextView friendStatusView = (TextView) this.findViewById(R.id.tv_friendstatus);

        if (friend != null) {
            tv_name.setText(friend.nickName);
            if (friend.gender == UserInfo.Gender.Male) {
                iv_sex.setImageResource(R.drawable.ic_sex_male);
            } else if (friend.gender == UserInfo.Gender.Female) {
                iv_sex.setImageResource(R.drawable.ic_sex_female);
            }

            if(friend.friendStatus == UserInfo.FriendStatus.ToAccept){
                friendStatusView.setText("对方请求添加你为好友");
                requestButton.setText("接受");
                requestButton.setClickable(true);

                requestButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppConstant.userManager.acceptFriendInvitation(friend.id);
                        finish();
                    }
                });

            } else if(friend.friendStatus == UserInfo.FriendStatus.PendingRequest){
                friendStatusView.setText("未添加对方为好友");
                requestButton.setText("申请加为好友");
                requestButton.setClickable(true);
            } else if(friend.friendStatus == UserInfo.FriendStatus.PendingAccepted){
                friendStatusView.setText("已申请添加对方为好友");
                requestButton.setText("等待对方确认");
            }
        }

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("cacheId", friend.id);
                intent.putExtra("imageUrl", friend.imageUrl);
                intent.setClass(PendingUserActivity.this, BigImageActivity.class);
                startActivity(intent);
            }
        });

        final ImageView moreInfoView = (ImageView) this.findViewById(R.id.iv_detail);
        moreInfoView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FriendPopupWindow friendPopupWindow = new FriendPopupWindow(PendingUserActivity.this);
                friendPopupWindow.showPopupWindow(friend, moreInfoView);
            }

        });
    }

    public void back(View view) {
        finish();
    }
}