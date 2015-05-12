package com.fanxin.app.adapter;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.fanxin.app.Constant;
import com.fanxin.app.R;
import com.fanxin.app.activity.FXAlertDialog;
import com.fanxin.app.activity.ShowBigImage;
import com.fanxin.app.fx.ChatActivity;
import com.fanxin.app.utils.ImageCache;
import com.fanxin.app.utils.ImageUtils;
import com.fanxin.app.utils.SmileUtils;

import appLogic.MeInfo;
import common.AsyncImageLoader;

@SuppressLint({ "SdCardPath", "InflateParams" })
public class MessageAdapter extends BaseAdapter {
    private final static String TAG = "msg";

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

    private String username;
    private LayoutInflater inflater;
    private Activity activity;

    private Context context;

    private Map<String, Timer> timers = new Hashtable<String, Timer>();

    public MessageAdapter(Context context, String username) {
        this.username = username;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (Activity) context;
    }

    // public void setUser(String user) {
    // this.user = user;
    // }

    /**
     * 获取item数
     */
    public int getCount() {
        return 0;
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        notifyDataSetChanged();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getItem(int position) {
        return null;
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

    private View createViewByMessage(int position) {
        return null;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        return convertView;
    }

    /**
     * 文本消息
     * @param holder
     * @param position
     */
    private void handleTextMessage(ViewHolder holder,
                                   final int position) {

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

    /**
     * load image into image view
     *
     * @param thumbernailPath
     * @param iv
     * @return the image exists or not
     */
    private boolean showImageView(final String thumbernailPath,
                                  final ImageView iv, final String localFullSizePath,
                                  String remoteDir) {
        // String imagename =
        // localFullSizePath.substring(localFullSizePath.lastIndexOf("/") + 1,
        // localFullSizePath.length());
        // final String remote = remoteDir != null ? remoteDir+imagename :
        // imagename;
        final String remote = remoteDir;
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            iv.setClickable(true);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.err.println("image view on click");
                    Intent intent = new Intent(activity, ShowBigImage.class);
                    File file = new File(localFullSizePath);
                    if (file.exists()) {
                        Uri uri = Uri.fromFile(file);
                        intent.putExtra("uri", uri);
                        System.err
                                .println("here need to check why download everytime");
                    } else {

                    }
                    activity.startActivity(intent);
                }
            });
            return true;
        } else {
            return true;
        }

    }

    /**
     * 展示视频缩略图
     *
     * @param localThumb   本地缩略图路径
     * @param iv
     * @param thumbnailUrl 远程缩略图路径
     */
    private void showVideoThumbView(String localThumb, ImageView iv,
                                    String thumbnailUrl) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(localThumb);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            iv.setClickable(true);
            iv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                }
            });

        } else {
        }

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
}