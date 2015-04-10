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
import java.util.List;

import common.AsyncImageLoader;
import logic.PersonalInfo;

/**
 * Created by tiazh on 4/9/2015.
 */
public class ContactListAdapter extends BaseAdapter {

    private LayoutInflater m_Inflater;
    private List<PersonalInfo> m_data;
    private List<View> m_view;
    private int m_resource;
    private int m_tilteResource;

    public ContactListAdapter(Context context, List<PersonalInfo> data, int resource, int titleResource)
    {
        m_Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_data = data;
        m_resource = resource;
        m_tilteResource = titleResource;
        m_view = new ArrayList<View>();
    }

    public int getCount()
    {
        return m_data.size() + 2;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(m_view.size() > position)
            return m_view.get(position);

        View view = null;
        if(position == 0 || position == m_data.size() + 1) {
            view = m_Inflater.inflate(m_tilteResource, parent, false);

            TextView textView = (TextView) view.findViewById(R.id.id_contact_view_title);

            if(position == 0)
                textView.setText("Favorite");
            else
                textView.setText("Recent");
        }
        else {
            view = m_Inflater.inflate(m_resource, parent, false);

            PersonalInfo info = m_data.get(position - 1);
            view.setTag(info);

            ImageView imageView = (ImageView) view.findViewById(R.id.id_contact_view_image);
            TextView textView = (TextView) view.findViewById(R.id.id_contact_view_text);

            textView.setText(info.name);
            imageView.setImageDrawable(Constant.defaultImageDrawable);

            AsyncImageLoader imageLoader = new AsyncImageLoader(imageView);
            imageLoader.execute(info.imageUrl, info.getImageLocalPath());
        }

        if(m_view.size() == position)
            m_view.add(view);

        return view;
    }

    public long getItemId(int position)
    {
        return position;
    }

    public Object getItem(int position) {
        return position;
    }
}
