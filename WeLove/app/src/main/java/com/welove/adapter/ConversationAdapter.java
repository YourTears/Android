package com.welove.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.welove.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import appLogic.AppConstant;
import appLogic.Conversation;
import appLogic.UserInfo;
import common.DateUtils;
import common.ImageLoaderManager;

public class ConversationAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<Conversation> conversations;

    public ConversationAdapter(Context context, List<Conversation> conversations) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.conversations = conversations;
    }

    @Override
    public int getCount() {
        return conversations.size();
    }

    @Override
    public Object getItem(int position) {
        return conversations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Conversation conversation = (Conversation) getItem(position);

        view = inflater.inflate(R.layout.item_conversation_single, parent, false);
        view.setTag(conversation.friendId);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_avatar);

        UserInfo friend = AppConstant.userManager.getUser(conversation.friendId);
        if (friend != null) {
            AppConstant.imageLoaderManager.loadImage(imageView, friend.id, friend.imageUrl, ImageLoaderManager.CacheMode.Memory);
        }

        TextView nameView = (TextView) view.findViewById(R.id.tv_name);
        TextView unreadView = (TextView) view.findViewById(R.id.tv_unread);
        TextView contentView = (TextView) view.findViewById(R.id.tv_content);
        TextView timeView = (TextView) view.findViewById(R.id.tv_time);
        ImageView statusView = (ImageView) view.findViewById(R.id.msg_state);

        nameView.setText(friend.nickName);
        contentView.setText(conversation.body);
        timeView.setText(DateUtils.getDateTimeStringForConversation(conversation.time));

        if (conversation.unreadCount > 0) {
            unreadView.setText(String.valueOf(conversation.unreadCount));
            unreadView.setVisibility(View.VISIBLE);
        } else {
            unreadView.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}
