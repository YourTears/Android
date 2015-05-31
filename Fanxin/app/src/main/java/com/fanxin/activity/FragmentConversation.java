package com.fanxin.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanxin.app.R;
import com.fanxin.adapter.ConversationAdapter;

import appLogic.AppConstant;

public class FragmentConversation extends Fragment {

    private ListView listView;

    public RelativeLayout errorItem;
    public TextView errorText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if(AppConstant.conversationView == null)
            AppConstant.conversationView = inflater.inflate(R.layout.fragment_home, container, false);

        return AppConstant.conversationView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);

        listView = (ListView) getView().findViewById(R.id.conversation_list);
        listView.setAdapter(AppConstant.conversationManager.adapter);
    }
}