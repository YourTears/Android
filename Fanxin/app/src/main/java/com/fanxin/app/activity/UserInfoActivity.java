package com.fanxin.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import appLogic.AppConstant;
import appLogic.FriendInfo;
import appLogic.enums.FriendStatus;
import appLogic.enums.Gender;
import appLogic.ImageManager;
import common.AsyncImageLoader;

import com.fanxin.activity.ChatActivity;
import com.fanxin.app.R;

public class UserInfoActivity extends Activity {
    private FriendInfo friend = null;
    String hxid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        Button btn_sendmsg = (Button) this.findViewById(R.id.btn_sendmsg);
        ImageView imageView = (ImageView) this.findViewById(R.id.iv_avatar);
        ImageView iv_sex = (ImageView) this.findViewById(R.id.iv_sex);
        TextView tv_name = (TextView) this.findViewById(R.id.tv_name);
        String id = this.getIntent().getStringExtra("id");
        friend = AppConstant.friendManager.getFriend(id);

        if (friend != null) {
            tv_name.setText(friend.name);
            if (friend.gender == Gender.male) {
                iv_sex.setImageResource(R.drawable.ic_sex_male);
            } else if (friend.gender == Gender.female) {
                iv_sex.setImageResource(R.drawable.ic_sex_female);
            } else {
                iv_sex.setVisibility(View.GONE);
            }

            if (friend.friendStatus == FriendStatus.friend) {
                btn_sendmsg.setText("发消息");
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

        imageView.setImageDrawable(AppConstant.defaultImageDrawable);

        AsyncImageLoader imageLoader = new AsyncImageLoader(imageView, true);
        imageLoader.execute(friend.imageUrl, ImageManager.getImageLocalPath(friend.imageUrl, friend.id));
    }

    public void back(View view) {
        finish();
    }
}