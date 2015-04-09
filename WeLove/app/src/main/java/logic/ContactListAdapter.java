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

import java.util.List;

import common.AsyncImageLoader;
import logic.PersonalInfo;

/**
 * Created by tiazh on 4/9/2015.
 */
public class ContactListAdapter extends BaseAdapter {

    private LayoutInflater m_Inflater;
    private List<PersonalInfo> m_data;
    private int m_resource;

    public ContactListAdapter(Context context, List<PersonalInfo> data, int resource)
    {
        m_Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_data = data;
        m_resource = resource;
    }

    public int getCount()
    {
        return m_data.size();
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if(view == null)
        {
            view = m_Inflater.inflate(m_resource, parent, false);
        }

        PersonalInfo info = m_data.get(position);

        ImageView imageView =(ImageView)view.findViewById(R.id.id_contact_view_image);
        TextView textView = (TextView)view.findViewById(R.id.id_contact_view_text);

        textView.setText(info.name);
        imageView.setImageDrawable(Constant.defaultImageDrawable);
        imageView.setTag(info);

        AsyncImageLoader imageLoader = new AsyncImageLoader(imageView);
        imageLoader.execute(info.imageUrl, info.getImageLocalPath());

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
