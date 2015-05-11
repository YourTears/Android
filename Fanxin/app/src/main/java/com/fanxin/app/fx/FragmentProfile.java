package com.fanxin.app.fx;

import com.fanxin.app.R;
import android.content.Intent;
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
import common.AsyncImageLoader;

public class FragmentProfile extends Fragment {
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

        ImageView imageView = (ImageView) re_myinfo.findViewById(R.id.iv_avatar);
        TextView nameTextView = (TextView) re_myinfo.findViewById(R.id.tv_name);
        TextView idTextView = (TextView) re_myinfo.findViewById(R.id.tv_id);

        nameTextView.setText(AppConstant.meInfo.name);
        idTextView.setText("ID:" + AppConstant.meInfo.id);

        AsyncImageLoader imageLoader = new AsyncImageLoader(imageView, true);
        imageLoader.execute(AppConstant.meInfo.imageUrl,
                ImageManager.getImageLocalPath(AppConstant.meInfo.imageUrl, AppConstant.meInfo.id));
    }
}