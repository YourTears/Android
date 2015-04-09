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

import common.Util;
import logic.ContactListAdapter;
import logic.PersonalInfo;

/**
 * Created by tiazh on 3/28/2015.
 */
public class FragmentContact extends Fragment {
    private ListView m_favoriteListView = null;
    private ListView m_recentListView = null;
    private List<PersonalInfo> m_favoriteListItem = new ArrayList<PersonalInfo>();
    private List<PersonalInfo> m_recentListItem = new ArrayList<PersonalInfo>();

    private ContactListAdapter m_favoriteListAdapter = null;

    private boolean m_dataInitialized = false;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(m_dataInitialized == false) {
            List<PersonalInfo> persons = PersonalInfo.getPersonalInfos(Util.getAssertInputStream(this.getResources().getAssets(), "info/contacts.xml"));

            m_favoriteListItem = persons;
            m_favoriteListAdapter = new ContactListAdapter(this.getActivity(), m_favoriteListItem, R.layout.contact_view);

            m_dataInitialized = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.frame_contact, container, false);

        m_favoriteListView = (ListView) view.findViewById(R.id.id_listview_favorite_contacts);
        m_recentListView = (ListView) view.findViewById(R.id.id_listview_recent_contacts);

        m_favoriteListView.setAdapter(m_favoriteListAdapter);

        return view;
    }
}
