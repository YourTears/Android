package com.fanxin.app.fx;

import com.fanxin.app.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import appLogic.FriendInfo;

public class FriendDetailActivity extends Activity {

    String hxid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfrienddetail);
        hxid = this.getIntent().getStringExtra("hxid");
        if (hxid != null && !hxid.equals("")) {

            initView();

        }

    }

    private void initView() {

        FriendInfo user = null;
        if (user != null) {

            TextView tv_name = (TextView) this.findViewById(R.id.tv_name);
            TextView tv_region = (TextView) this.findViewById(R.id.tv_region);
            TextView tv_fxid = (TextView) this.findViewById(R.id.tv_fxid);
            TextView tv_sign = (TextView) this.findViewById(R.id.tv_sign);
            ImageView iv_sex = (ImageView) this.findViewById(R.id.iv_sex);
            ImageView iv_detail = (ImageView) this.findViewById(R.id.iv_detail);
            tv_name.setText(user.nickName);
            tv_region.setText(user.region);
            tv_fxid.setText(user.sys_id);
            tv_sign.setText(user.sign);
            if (user.gender == FriendInfo.Gender.Male) {
                iv_sex.setImageResource(R.drawable.ic_sex_male);
            } else {
                iv_sex.setImageResource(R.drawable.ic_sex_female);
            }

            iv_detail.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                }

            });
        }

    }

    public void back(View view) {
        finish();
    }

}
