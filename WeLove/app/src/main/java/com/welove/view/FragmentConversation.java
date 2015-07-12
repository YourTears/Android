package com.welove.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.welove.activity.ChatActivity;
import com.welove.activity.MainActivity;
import com.welove.app.R;

import appLogic.AppConstant;
import appLogic.UserInfo;

public class FragmentConversation extends Fragment {
    private static String[] conversationItemLongClickItem = {"删除聊天"};

    private ListView listView;

    private RelativeLayout serverStatusView = null;

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

        serverStatusView = (RelativeLayout) getView().findViewById(R.id.rl_error_item);

        listView = (ListView) getView().findViewById(R.id.conversation_list);
        listView.setAdapter(AppConstant.conversationManager.adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String friendId = (String) view.getTag();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("id", friendId);
                getActivity().startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final String friendId = (String) view.getTag();
                UserInfo friend = AppConstant.userManager.getUser(friendId);

                new AlertDialog.Builder(getActivity())
                        .setTitle(friend.nickName)
                        .setItems(conversationItemLongClickItem, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0) {
                                    AppConstant.conversationManager.deleteConversation(friendId);
                                    AppConstant.conversationManager.refreshView();

                                    if (MainActivity.instance != null) {
                                        MainActivity.instance.updateUnreadMessageLabel();
                                    }
                                }
                            }
                        })
                        .show();

                return true;
            }
        });

        refreshServerStatus();
    }

    public void refreshListView(){
        AppConstant.conversationManager.refreshView();
    }

    public void refreshServerStatus(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int retryCount = 3;

                while(retryCount > 0){
                    if(AppConstant.conversationProxy.isConnected())
                        break;

                    retryCount --;

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(MainActivity.instance != null) {
                    MainActivity.instance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (AppConstant.conversationProxy.isConnected()) {
                                serverStatusView.setVisibility(View.GONE);
                            } else {
                                serverStatusView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        }).start();
    }
}