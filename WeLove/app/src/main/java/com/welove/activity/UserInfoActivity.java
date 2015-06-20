package com.welove.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import appLogic.AppConstant;
import appLogic.UserInfo;
import common.ImageLoaderManager;

import com.welove.app.R;

public class UserInfoActivity extends Activity {
    private UserInfo friend = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        String id = this.getIntent().getStringExtra("id");
        friend = AppConstant.userManager.getUser(id);

        ImageView imageView = (ImageView) this.findViewById(R.id.iv_avatar);
        imageView.setImageDrawable(AppConstant.defaultImageDrawable);
        AppConstant.imageLoaderManager.loadImage(imageView, friend.id, friend.imageUrl, ImageLoaderManager.CacheMode.Memory);

        Button btn_sendmsg = (Button) this.findViewById(R.id.btn_sendmsg);
        ImageView iv_sex = (ImageView) this.findViewById(R.id.iv_sex);
        TextView tv_name = (TextView) this.findViewById(R.id.tv_name);

        if (friend != null) {
            tv_name.setText(friend.nickName);
            if (friend.gender == UserInfo.Gender.Male) {
                iv_sex.setImageResource(R.drawable.ic_sex_male);
            } else if (friend.gender == UserInfo.Gender.Female) {
                iv_sex.setImageResource(R.drawable.ic_sex_female);
            }
        }

        btn_sendmsg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("id", friend.id);
                intent.setClass(UserInfoActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("cacheId", friend.id);
                intent.putExtra("imageUrl", friend.imageUrl);
                intent.setClass(UserInfoActivity.this, BigImageActivity.class);
                startActivity(intent);
            }
        });

        final ImageView moreInfoView = (ImageView) this.findViewById(R.id.iv_detail);
        moreInfoView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                FriendPopupWindow friendPopupWindow = new FriendPopupWindow(UserInfoActivity.this);
                friendPopupWindow.showPopupWindow(friend, moreInfoView);
            }

        });
    }

    public void back(View view) {
        finish();
    }
}