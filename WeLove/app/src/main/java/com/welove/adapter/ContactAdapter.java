package com.welove.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.welove.app.R;

import appLogic.AppConstant;
import appLogic.UserInfo;
import common.ImageLoaderManager;

public class ContactAdapter extends BaseAdapter {
    private List<UserInfo> friends, pendingFriends;
    private LayoutInflater layoutInflater;
    private Map<String, View> views = null;

	public ContactAdapter(Context context, List<UserInfo> friends, List<UserInfo> pendingFriends) {
        this.friends = friends;
        this.pendingFriends = pendingFriends;
        layoutInflater = LayoutInflater.from(context);
        views = new HashMap<String, View>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String itemKey = getItemKey(position);
        UserInfo friend = getItem(position);

        View view = null;

        if(views.containsKey(itemKey)) {
            view = views.get(itemKey);

            if(friend != null){
                updateStatusView((TextView)view.findViewById(R.id.tv_status), friend.friendStatus);
            }

            return view;
        }

        if(position == 0 || position == friends.size() + 1){
            view = layoutInflater.inflate(R.layout.contact_view_title, parent, false);

            TextView textView = (TextView) view.findViewById(R.id.id_contact_view_title);

            if(position == 0)
                textView.setText("我的好友");
            else
                textView.setText("等待中");
        } else {
            view = layoutInflater.inflate(R.layout.item_contact_list, parent, false);
            view.setTag(friend.id);

            ImageView imageView = (ImageView) view.findViewById(R.id.iv_avatar);
            TextView nameTextView = (TextView) view.findViewById(R.id.tv_name);
            TextView statusTextView = (TextView) view.findViewById(R.id.tv_status);

            nameTextView.setText(friend.nickName);

            updateStatusView(statusTextView, friend.friendStatus);

            AppConstant.imageLoaderManager.loadImage(imageView, friend.id, friend.imageUrl, ImageLoaderManager.CacheMode.Memory);
        }

        views.put(itemKey, view);
        return view;
    }

    private void updateStatusView(TextView textView, UserInfo.FriendStatus status){
        if(status == UserInfo.FriendStatus.Friend)
            textView.setVisibility(View.GONE);
        else if(status == UserInfo.FriendStatus.PendingRequest)
            textView.setText("未加为好友");
        else if(status == UserInfo.FriendStatus.ToAccept)
            textView.setText("等待确认");
        else if(status == UserInfo.FriendStatus.PendingAccepted)
            textView.setText("等待对方确认");
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public UserInfo getItem(int position) {
        if(position > 0 && position <= friends.size())
            return friends.get(position - 1);

        if(position > friends.size() + 1 && position < getCount())
            return pendingFriends.get(position - friends.size() - 2);

        return null;
    }

    @Override
    public int getCount() {
        return friends.size() + pendingFriends.size() + 2;
    }

    private String getItemKey(int position){
        if(position == 0)
            return "MyFriends";
        else if(position == friends.size() + 1)
            return "PendingFriends";
        else{
            UserInfo userInfo = getItem(position);
            return "ID" + userInfo.id;
        }
    }
}
