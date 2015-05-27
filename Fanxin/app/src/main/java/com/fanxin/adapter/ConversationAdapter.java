package com.fanxin.adapter;

import java.util.List;

import com.fanxin.app.Constant;
import com.fanxin.app.R;
import com.fanxin.activity.ChatActivity;

import android.annotation.SuppressLint;
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

import org.w3c.dom.Text;

import appLogic.AppConstant;
import appLogic.Conversation;
import appLogic.FriendInfo;
import appLogic.ImageManager;
import common.AsyncImageLoader;
import common.DateUtils;

@SuppressLint("InflateParams")
public class ConversationAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<Conversation> conversations;

    @SuppressLint("SdCardPath")
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
        int count = getCount();
        return conversations.get(count - 1 - position);
    }

    @Override
    public long getItemId(int position) {
        int count = getCount();
        return count - 1 - position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Conversation conversation = (Conversation) getItem(position);

        convertView = inflater.inflate(R.layout.item_conversation_single, null, false);

        TextView nameView = (TextView) convertView.findViewById(R.id.tv_name);
        TextView unreadView = (TextView) convertView.findViewById(R.id.tv_unread);
        TextView contentView = (TextView) convertView.findViewById(R.id.tv_content);
        TextView timeView = (TextView) convertView.findViewById(R.id.tv_time);
        ImageView statusView = (ImageView) convertView.findViewById(R.id.msg_state);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_avatar);

        contentView.setText(conversation.body);
        timeView.setText(DateUtils.getDateTimeString(conversation.time));

        if (conversation.unreadCount > 0) {
            // 显示与此用户的消息未读数
            unreadView.setText(String.valueOf(conversation.unreadCount));
            unreadView.setVisibility(View.VISIBLE);
        } else {
            unreadView.setVisibility(View.INVISIBLE);
        }

        FriendInfo friend = AppConstant.friendManager.getFriend(conversation.friendId);
        if (friend != null) {
            nameView.setText(friend.name);

            AsyncImageLoader imageLoader = new AsyncImageLoader(imageView, true);
            imageLoader.execute(friend.imageUrl, ImageManager.getImageLocalPath(friend.imageUrl, friend.id));
        }

        RelativeLayout re_parent = (RelativeLayout) convertView
                .findViewById(R.id.re_parent);

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

                return false;
            }
        });

        return convertView;
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
