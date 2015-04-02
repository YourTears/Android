package com.example.blind.welove;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import common.AsyncImageLoader;
import common.MD5;

/**
 * Created by tiazh on 3/28/2015.
 */
public class FragmentMe extends Fragment {

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.page_me, container, false);

        ImageView imageView = (ImageView)view.findViewById(R.id.id_me_image);
        AsyncImageLoader imageLoader = new AsyncImageLoader(imageView, Constant.ImageFolder);
        imageLoader.execute("http://www.baidu.com/img/bd_logo1.png");

        return view;
    }
}
