package com.welove.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.welove.activity.ChatActivity;
import com.welove.app.R;

import appLogic.AppConstant;
import appLogic.UserInfo;

public class FragmentConversation extends Fragment {
    private static String[] conversationItemLongClickItem = {"删除聊天"};

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
                String friendId = (String)view.getTag();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("id", friendId);
                getActivity().startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final String friendId = (String)view.getTag();
                UserInfo friend = AppConstant.userManager.getUser(friendId);

                new AlertDialog.Builder(getActivity())
                        .setTitle(friend.nickName)
                        .setItems(conversationItemLongClickItem, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0) {
                                    AppConstant.conversationManager.deleteConversation(friendId);
                                }
                            }
                        })
                        .show();

                return true;
            }
        });
    }
}