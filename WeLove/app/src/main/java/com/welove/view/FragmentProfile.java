package com.welove.view;

import com.welove.activity.MeInfoActivity;
import com.welove.app.R;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import appLogic.AppConstant;
import common.ImageLoaderManager;

public class FragmentProfile extends Fragment {
    private ImageView imageView = null;
    private TextView nameTextView = null;

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
                startActivity(new Intent(getActivity(), MeInfoActivity.class));
            }

        });

        imageView = (ImageView) re_myinfo.findViewById(R.id.iv_avatar);
        nameTextView = (TextView) re_myinfo.findViewById(R.id.tv_name);

        nameTextView.setText(AppConstant.meInfo.name);

        AppConstant.imageLoaderManager.loadImage(imageView, AppConstant.meInfo.id, AppConstant.meInfo.imageUrl, ImageLoaderManager.CacheMode.Memory);
    }

    public void updateProfile(){
        if(nameTextView != null){
            nameTextView.setText(AppConstant.meInfo.name);
        }

        if(imageView != null) {
            AppConstant.imageLoaderManager.loadImage(imageView, AppConstant.meInfo.id, AppConstant.meInfo.imageUrl, ImageLoaderManager.CacheMode.Memory);
        }
    }
}