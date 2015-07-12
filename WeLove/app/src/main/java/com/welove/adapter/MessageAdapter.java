package com.welove.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.welove.activity.BigImageActivity;
import com.welove.app.R;

import appLogic.AppConstant;
import appLogic.UserInfo;
import appLogic.Message;
import common.DateUtils;
import common.ExpressionUtils;
import common.ImageLoaderManager;

public class MessageAdapter extends BaseAdapter {
    private final static String TAG = "MessageAdapter";

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    private static final int MESSAGE_TYPE_SENT_FILE = 10;
    private static final int MESSAGE_TYPE_RECV_FILE = 11;
    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;

    public static final String IMAGE_DIR = "chat/image/";
    public static final String VOICE_DIR = "chat/audio/";
    public static final String VIDEO_DIR = "chat/video";

    private UserInfo friend;
    private LayoutInflater inflater;
    private Activity activity;

    private Context context;

    private List<Message> messages;

    public MessageAdapter(Context context, String id, List<Message> messages) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (Activity) context;

        friend = AppConstant.userManager.getUser(id);
        this.messages = messages;
    }

    /**
     * 获取item数
     */
    public int getCount() {
        return messages.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        if(position < 0 || position >= messages.size())
            return null;

        return messages.get(position);
    }

    /**
     * 获取item类型
     */
    public int getItemViewType(int position) {
        return -1;// invalid
    }

    public int getViewTypeCount() {
        return 14;
    }

    private View setMessageViewData(int position, View view, Message message) {
        ImageView headerView = (ImageView)view.findViewById(R.id.iv_userhead);

        String ownerId = AppConstant.meInfo.id;
        if(message.direction == Message.Direction.RECEIVE)
            ownerId = friend.id;

        AppConstant.imageLoaderManager.loadImage(headerView, ownerId, friend.imageUrl, ImageLoaderManager.CacheMode.Memory);

        TextView textView = (TextView) view.findViewById(R.id.tv_chatcontent);
        Spannable span = ExpressionUtils.getSmiledText(context, message.body);
        textView.setText(span, TextView.BufferType.SPANNABLE);

        TextView timeView = (TextView) view.findViewById(R.id.tv_timestamp);

        if(position == 0 || messageTimeGap(position) > 1000 * 60 * 3) {
            timeView.setVisibility(View.VISIBLE);
            timeView.setText(DateUtils.getDateTimeString(message.time));
        } else {
            timeView.setVisibility(View.GONE);
        }

        TextView userNameView =(TextView) view.findViewById(R.id.tv_userid);
        if(userNameView != null)
            userNameView.setText(friend.nickName);

        return view;
    }

    public View getView(int position, View view, ViewGroup parent) {
       Message message = (Message)getItem(position);

       view = message.direction == Message.Direction.SEND ?
            inflater.inflate(R.layout.row_sent_message, null) : inflater.inflate(R.layout.row_received_message, null);

        setMessageViewData(position, view, message);

        if (message.direction == Message.Direction.SEND) {
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.pb_sending);
            if (message.status == Message.MessageStatus.INPROGRESS) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }

            ImageView statusView = (ImageView) view.findViewById(R.id.msg_status);
            if (message.status == Message.MessageStatus.FAIL) {
                statusView.setVisibility(View.VISIBLE);
            } else {
                statusView.setVisibility(View.GONE);
            }
        }

        return view;
    }

    /**
     * 文本消息
     * @param holder
     * @param position
     */
    private void handleTextMessage(ViewHolder holder, int position) {

    }

    /**
     * 语音通话记录
     *
     * @param holder
     * @param position
     */
    private void handleVoiceCallMessage(ViewHolder holder,
                                        final int position) {
    }

    /**
     * 图片消息
     *
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleImageMessage(final ViewHolder holder, final int position, View convertView) {
        holder.pb.setTag(position);
        // holder.iv.setOnLongClickListener(new OnLongClickListener() {
        // @Override
        // public boolean onLongClick(View v) {
        // activity.startActivityForResult((new Intent(activity,
        // ContextMenu.class)).putExtra("position", position)
        // .putExtra("type", EMMessage.Type.IMAGE.ordinal()),
        // ChatActivity.REQUEST_CODE_CONTEXT_MENU);
        // return true;
        // }
        // });
    }

    /**
     * 视频消息
     *
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleVideoMessage(final ViewHolder holder, final int position, View convertView) {
    }

    /**
     * 语音消息
     *
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleVoiceMessage(final ViewHolder holder, final int position, View convertView) {
    }

    /**
     * 处理位置消息
     *
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleLocationMessage(final ViewHolder holder, final int position, View convertView) {
        TextView locationView = ((TextView) convertView
                .findViewById(R.id.tv_location));
    }

    /**
     * 发送消息
     *
     * @param holder
     */
    public void sendMsgInBackground(final ViewHolder holder) {
        holder.staus_iv.setVisibility(View.GONE);
        holder.pb.setVisibility(View.VISIBLE);

        final long start = System.currentTimeMillis();
    }

    /*
     * chat sdk will automatic download thumbnail image for the image message we
     * need to register callback show the download progress
     */
    private void showDownloadImageProgress(final ViewHolder holder) {
        System.err.println("!!! show download image progress");
    }

    /*
     * send message with new sdk
     */
    private void sendPictureMessage(final ViewHolder holder) {

    }

    /**
     * 更新ui上消息发送状态
     *
     * @param holder
     */
    private void updateSendedView(final ViewHolder holder) {
    }

    private boolean showImageView(final String thumbernailPath,
                                  final ImageView iv, final String localFullSizePath,
                                  String remoteDir) {
        return true;
    }

    private void showVideoThumbView(String localThumb, ImageView iv,String thumbnailUrl) {

    }

    public static class ViewHolder {
        ImageView iv;
        TextView tv;
        ProgressBar pb;
        ImageView staus_iv;
        ImageView head_iv;
        TextView tv_userId;
        ImageView playBtn;
        TextView timeLength;
        TextView size;
        LinearLayout container_status_btn;
        LinearLayout ll_container;
        ImageView iv_read_status;
        // 显示已读回执状态
        TextView tv_ack;
        // 显示送达回执状态
        TextView tv_delivered;

        TextView tv_file_name;
        TextView tv_file_size;
        TextView tv_file_download_state;
    }

    /*
     * 点击地图消息listener
     */
    class MapClickListener implements View.OnClickListener {

        String address;

        public MapClickListener(String address) {
            this.address = address;

        }

        @Override
        public void onClick(View v) {
        }
    }

    private long messageTimeGap(int position){
        Message message1 = (Message) getItem(position);
        Message message2 = (Message) getItem(position - 1);

        if(message1 == null || message2 == null)
            return 0;

        return message1.time - message2.time;
    }
}