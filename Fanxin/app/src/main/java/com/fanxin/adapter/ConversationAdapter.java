package com.fanxin.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fanxin.app.R;
import com.fanxin.activity.ChatActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import appLogic.AppConstant;
import appLogic.Conversation;
import appLogic.FriendInfo;
import common.DateUtils;
import common.ImageLoaderManager;

public class ConversationAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<Conversation> conversations;
    private Map<String, View> views = null;

    public ConversationAdapter(Context context, List<Conversation> conversations) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.conversations = conversations;
        views = new HashMap<>();
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
    public View getView(int position, View convertView, ViewGroup parent) {

        final Conversation conversation = (Conversation) getItem(position);
        FriendInfo friend = AppConstant.friendManager.getFriend(conversation.friendId);

        View view = null;
        if(views.containsKey(conversation.friendId)){
            view = views.get(conversation.friendId);
        }else{
            view = inflater.inflate(R.layout.item_conversation_single, parent, false);
            view.setTag(conversation.friendId);
            views.put(conversation.friendId, view);

            TextView nameView = (TextView) view.findViewById(R.id.tv_name);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_avatar);

            if (friend != null) {
                nameView.setText(friend.name);

                AppConstant.imageLoaderManager.loadImage(imageView, friend.id, friend.imageUrl, ImageLoaderManager.CacheMode.Memory);
            }

            RelativeLayout re_parent = (RelativeLayout) view.findViewById(R.id.re_parent);

            re_parent.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("id", conversation.friendId);
                    context.startActivity(intent);
                }
            });

            re_parent.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
        }

        TextView unreadView = (TextView) view.findViewById(R.id.tv_unread);
        TextView contentView = (TextView) view.findViewById(R.id.tv_content);
        TextView timeView = (TextView) view.findViewById(R.id.tv_time);
        ImageView statusView = (ImageView) view.findViewById(R.id.msg_state);

        contentView.setText(conversation.body);
        timeView.setText(DateUtils.getDateTimeString(conversation.time));

        if (conversation.unreadCount > 0) {
            unreadView.setText(String.valueOf(conversation.unreadCount));
            unreadView.setVisibility(View.VISIBLE);
        } else {
            unreadView.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    private String getMessageDigest(Object message, Context context) {
        String digest = "";
        return digest;
    }
}
