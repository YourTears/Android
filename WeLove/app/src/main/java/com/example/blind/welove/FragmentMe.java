package com.example.blind.welove;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import common.AsyncImageLoader;
import common.Gender;

/**
 * Created by tiazh on 3/28/2015.
 */
public class FragmentMe extends Fragment {

    private String expectedMessage = null;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        expectedMessage = "期待的" + Constant.it;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.page_me, container, false);

        if(Constant.meInfo.gender == Gender.female)
        {
            ImageView imageView = (ImageView)view.findViewById(R.id.id_view_me_icon);
            if(imageView != null)
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.girlicon));

            imageView = (ImageView)view.findViewById(R.id.id_view_it_icon);
            if(imageView != null)
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.boyicon));

            TextView textView = (TextView)view.findViewById(R.id.id_textview_it_message);
            if(textView != null)
                textView.setText(expectedMessage);
        }

        ImageView imageView = (ImageView)view.findViewById(R.id.id_me_image);
        AsyncImageLoader imageLoader = new AsyncImageLoader(imageView);
        imageLoader.execute(Constant.meInfo.imageUrl, Constant.meInfo.getImageLocalPath());

        TextView textView = (TextView)view.findViewById(R.id.id_textview_me_name);
        if(textView != null)
            textView.setText(Constant.meInfo.name);

        textView = (TextView)view.findViewById(R.id.id_textview_me_id);
        if(textView != null)
            textView.setText("ID: " + Constant.meInfo.id);

        return view;
    }
}
