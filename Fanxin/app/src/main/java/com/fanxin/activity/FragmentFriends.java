package com.fanxin.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.fanxin.app.Constant;
import com.fanxin.app.R;

import appLogic.AppConstant;
import appLogic.UserInfo;

/**
 * 联系人列表页
 * 
 */
@SuppressLint("InflateParams")
public class FragmentFriends extends Fragment {
    private View mainView = null;
    private ListView listView;
    private boolean hidden;

    private TextView tv_total;
    private LayoutInflater infalter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if(mainView == null)
            mainView = inflater.inflate(R.layout.fragment_contactlist, container, false);

        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false))
            return;
        
        listView = (ListView) getView().findViewById(R.id.list);

        infalter=LayoutInflater.from(getActivity());
        View footerView = infalter.inflate(R.layout.item_contact_list_footer,
                null);
        listView.addFooterView(footerView);
        
        tv_total = (TextView) footerView.findViewById(R.id.tv_total);


        listView.setAdapter(AppConstant.userManager.adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String friendId = (String)view.getTag();
                if(friendId != null && !friendId.isEmpty()) {

                    UserInfo friend = AppConstant.userManager.getUser(friendId);

                    if(friend.friendStatus == UserInfo.FriendStatus.Friend)
                        startActivity(new Intent(getActivity(), UserInfoActivity.class).putExtra("id", friendId));
                    else
                        startActivity(new Intent(getActivity(), PendingUserActivity.class).putExtra("id", friendId));
                }
            }
        });
       
        tv_total.setText(String.valueOf(AppConstant.userManager.getFriendCount())+"位联系人");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    // 刷新ui
    public void refresh() {
        try {
            // 可能会在子线程中调到这方法
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    AppConstant.userManager.adapter.notifyDataSetChanged();
                    tv_total.setText(String.valueOf(AppConstant.userManager.getFriendCount())+"位联系人");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }

    }
}
