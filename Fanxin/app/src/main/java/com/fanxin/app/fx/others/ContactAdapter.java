package com.fanxin.app.fx.others;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.fanxin.app.Constant;
import com.fanxin.app.R;

import appLogic.AppConstant;
import appLogic.FriendInfo;
import appLogic.ImageManager;
import common.AsyncImageLoader;

/**
 * 简单的好友Adapter实现
 * 
 */
public class ContactAdapter extends ArrayAdapter<FriendInfo>  {

    List<String> list;
    List<FriendInfo> userList;
    private LayoutInflater layoutInflater;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private int res;

    @SuppressLint("SdCardPath")
	public ContactAdapter(Context context, int resource, List<FriendInfo> objects) {
        super(context, resource, objects);
        this.res = resource;
        this.userList = objects;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(res, null);
        }

        ImageView iv_avatar = (ImageView) convertView
                .findViewById(R.id.iv_avatar);

        TextView nameTextview = (TextView) convertView
                .findViewById(R.id.tv_name);
        View view_temp = (View) convertView.findViewById(R.id.view_temp);
        FriendInfo friend = getItem(position);

        convertView.setTag(friend.id);
        // 设置nick，demo里不涉及到完整user，用username代替nick显示

        String header = friend.name;
        String usernick = friend.name;
        String useravatar = friend.imageUrl;

        // 显示申请与通知item

        nameTextview.setText(usernick);
        iv_avatar.setImageResource(R.drawable.default_useravatar);

        AsyncImageLoader imageLoader = new AsyncImageLoader(iv_avatar, true);
        imageLoader.execute(friend.imageUrl,
                ImageManager.getImageLocalPath(friend.imageUrl, friend.id));

        return convertView;
    }

    @Override
    public FriendInfo getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}
