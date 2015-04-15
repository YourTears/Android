package com.blind.welove;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.appspot.apprtc.CallActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import logic.RoomConnector;

/**
 * Created by tiazh on 3/28/2015.
 */
public class FragmentLove extends Fragment {
    private View view = null;
    private String roomId = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if(view == null) {
            view = inflater.inflate(R.layout.frame_love, container, false);

            TextView textView = (TextView)view.findViewById(R.id.id_love_button);

            textView.setOnClickListener(connectListener);
        }

        return view;
    }

    private final View.OnClickListener connectListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RoomConnector connector = new RoomConnector(getActivity(),getActivity());

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmm");
            roomId = dateFormat.format(new Date());

            connector.connectToRoom(roomId, 0);
        }
    };
}
