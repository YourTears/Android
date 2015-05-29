package com.fanxin.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanxin.app.R;

import appLogic.AppConstant;
import appLogic.FriendInfo;

public class ContactAdapter extends BaseAdapter {
    private List<FriendInfo> friends;
    private LayoutInflater layoutInflater;
    private Map<String, View> views = null;

	public ContactAdapter(Context context, List<FriendInfo> objects) {
        friends = objects;
        layoutInflater = LayoutInflater.from(context);
        views = new HashMap<String, View>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendInfo friend = getItem(position);

        if(views.containsKey(friend.id))
            return views.get(friend.id);

        View view = layoutInflater.inflate(R.layout.item_contact_list, parent, false);
        view.setTag(friend.id);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_avatar);
        TextView nameTextView = (TextView) view.findViewById(R.id.tv_name);

        nameTextView.setText(friend.name);

//        AsyncImageLoader imageLoader = new AsyncImageLoader(imageView, true);
//        imageLoader.execute(friend.imageUrl, ImageManager.getImageLocalPath(friend.imageUrl, friend.id));
        AppConstant.imageLoaderManager.loadImage(imageView, friend.id, friend.imageUrl);

        views.put(friend.id, view);
        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public FriendInfo getItem(int position) {
        if(position >= 0 && position < friends.size())
            return friends.get(position);
        return null;
    }

    @Override
    public int getCount() {
        return friends.size();
    }
}
