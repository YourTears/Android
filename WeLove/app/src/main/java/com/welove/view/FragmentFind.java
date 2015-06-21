package com.welove.view;

import com.welove.app.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

import apprtc.RoomConnector;

public class FragmentFind  extends Fragment {
    String roomId = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, container, false);

        RelativeLayout start = (RelativeLayout)view.findViewById(R.id.re_friends);
        start.setOnClickListener(connectListener);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
