package com.example.blind.welove;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import common.Util;
import logic.PersonalInfo;

/**
 * Created by tiazh on 3/28/2015.
 */
public class FragmentContact extends Fragment {
    private ListView m_favoriteListView = null;
    private ListView m_recentListView = null;
    List<HashMap<String, Object>> m_favoriteListItem = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> m_recentListItem = new ArrayList<HashMap<String, Object>>();

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.frame_contact, container, false);

        m_favoriteListView = (ListView)view.findViewById(R.id.id_listview_favorite_contacts);
        m_recentListView = (ListView)view.findViewById(R.id.id_listview_recent_contacts);

        List<PersonalInfo> persons = PersonalInfo.getPersonalInfos(Util.getAssertInputStream(this.getResources().getAssets(), "info/contacts.xml"));

        for (int idx = 0; idx < persons.size(); idx ++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id_contact_view_image", R.drawable.defaultimage);
            map.put("id_contact_view_text", persons.get(idx).name);

            m_favoriteListItem.add(map);
        }

        SimpleAdapter favoriteListAdapter = new SimpleAdapter(this.getActivity(), m_favoriteListItem, R.layout.contact_view,
                new String[] {"id_contact_view_image", "id_contact_view_text"},
                new int[] {R.id.id_contact_view_image, R.id.id_contact_view_text});

        m_favoriteListView.setAdapter(favoriteListAdapter);

        return view;
    }
}
