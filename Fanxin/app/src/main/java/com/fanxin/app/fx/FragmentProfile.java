package com.fanxin.app.fx;

import com.fanxin.app.Constant;
import com.fanxin.app.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import appLogic.AppConstant;
import appLogic.ImageManager;
import appLogic.MeInfo;
import common.AsyncImageLoader;

public class FragmentProfile extends Fragment {

    private String avatar = "";
    private ImageView iv_avatar;
    private TextView tv_name;
    TextView tv_fxid;
    String fxid;
    String nick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RelativeLayout re_myinfo = (RelativeLayout) getView().findViewById(
                R.id.re_myinfo);
        re_myinfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),
                        MyUserInfoActivity.class));
            }

        });
        RelativeLayout re_setting = (RelativeLayout) getView().findViewById(
                R.id.re_setting);
        re_setting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }

        });
        nick = MeInfo.getInstance().name;
        fxid = MeInfo.getInstance().sys_id;

        avatar = MeInfo.getInstance().imageUrl;

        iv_avatar = (ImageView) re_myinfo.findViewById(R.id.iv_avatar);
        tv_name = (TextView) re_myinfo.findViewById(R.id.tv_name);
        tv_fxid = (TextView) re_myinfo.findViewById(R.id.tv_fxid);
        tv_name.setText(nick);
        if (fxid.equals("0")) {
            tv_fxid.setText("微信号：未设置");
        } else {
            tv_fxid.setText("微信号:" + fxid);
        }

        AsyncImageLoader imageLoader = new AsyncImageLoader(iv_avatar, true);
        imageLoader.execute(AppConstant.meInfo.imageUrl,
                ImageManager.getImageLocalPath(AppConstant.meInfo.imageUrl, AppConstant.meInfo.id));
    }
}
