package logic;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blind.welove.Constant;
import com.example.blind.welove.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.AsyncImageLoader;
import logic.PersonalInfo;

/**
 * Created by tiazh on 4/9/2015.
 */
public class ContactListAdapter extends BaseAdapter {

    private LayoutInflater m_Inflater;
    private List<PersonalInfo> m_favorite, m_recent;
    private FavoriteInfo m_favoriteInfo;
    private HashMap<String, View> m_view;
    private int m_resource;
    private int m_tilteResource;

    public ContactListAdapter(Context context, List<PersonalInfo> data, FavoriteInfo favoriteInfo,int resource, int titleResource)
    {
        m_Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_favoriteInfo = favoriteInfo;
        m_resource = resource;
        m_tilteResource = titleResource;
        m_view = new HashMap<String, View>();

        m_favorite = new ArrayList<PersonalInfo>();
        m_recent = new ArrayList<PersonalInfo>();

        for(int i = 0; i < data.size(); i ++)
        {
            PersonalInfo info = data.get(i);
            if(favoriteInfo.isFavorite(info.id))
                m_favorite.add(info);
            else
                m_recent.add(info);
        }
    }

    public int getCount()
    {
        return m_favorite.size() + m_recent.size() + 2;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        String hashKey = getItemKey(position);

        if(m_view.containsKey(hashKey))
            return m_view.get(hashKey);

        View view = null;
        if(position == 0 || position == m_favorite.size() + 1) {
            view = m_Inflater.inflate(m_tilteResource, parent, false);

            TextView textView = (TextView) view.findViewById(R.id.id_contact_view_title);

            if(position == 0)
                textView.setText("Favorite");
            else
                textView.setText("Recent");
        }
        else {
            view = m_Inflater.inflate(m_resource, parent, false);

            ImageView imageView = (ImageView) view.findViewById(R.id.id_contact_view_image);
            TextView textView = (TextView) view.findViewById(R.id.id_contact_view_text);

            PersonalInfo info = (PersonalInfo)getItem(position);

            textView.setText(info.name);
            imageView.setImageDrawable(Constant.defaultImageDrawable);

            AsyncImageLoader imageLoader = new AsyncImageLoader(imageView);
            imageLoader.execute(info.imageUrl, info.getImageLocalPath());
        }

        m_view.put(hashKey, view);

        return view;
    }

    public long getItemId(int position)
    {
        return position;
    }

    public Object getItem(int position) {
        if(position == 0 || position == m_favorite.size() + 1)
            return null;

        if(position <= m_favorite.size())
            return m_favorite.get(position - 1);

        return m_recent.get(position - m_favorite.size() - 2);
    }

    public boolean isInFavorite(int position)
    {
        if(position > 0 && position <= m_favorite.size())
            return true;
        return false;
    }

    public void changeFavoriteSetting(int position)
    {
        if(isInFavorite(position))
            moveAwayFromFavorite(position);
        else
            moveToFavorite(position);
    }

    private String getItemKey(int position) {
        if(position == 0)
            return "Favorite";

        if(position == m_favorite.size() + 1)
            return "Recent";

        return ((PersonalInfo)getItem(position)).id;
    }

    private void moveToFavorite(int position)
    {
        int index = position - m_favorite.size() - 2;
        if(index >= 0 && index < m_recent.size())
        {
            m_favoriteInfo.addToFavorite(m_recent.get(index).id);

            m_favorite.add(m_recent.get(index));
            m_recent.remove(index);
        }
    }

    private void moveAwayFromFavorite(int position)
    {
        int index = position - 1;
        if(index >= 0 && index < m_favorite.size())
        {
            m_favoriteInfo.removeFromFavorite(m_favorite.get(index).id);

            m_recent.add(m_favorite.get(index));
            m_favorite.remove(index);
        }
    }
}
