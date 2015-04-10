package com.example.blind.welove;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private View view = null;
    private ListView m_contactListView = null;
    private List<PersonalInfo> m_favoriteListItem = new ArrayList<PersonalInfo>();
    private List<PersonalInfo> m_recentListItem = new ArrayList<PersonalInfo>();

    private ContactListAdapter m_contactListAdapter = null;

    private boolean m_dataInitialized = false;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(m_dataInitialized == false) {
            List<PersonalInfo> persons = PersonalInfo.getPersonalInfos(Util.getAssertInputStream(this.getResources().getAssets(), "info/contacts.xml"));

            m_recentListItem = persons;
            m_contactListAdapter = new ContactListAdapter(this.getActivity(), m_favoriteListItem, m_recentListItem, R.layout.contact_view, R.layout.contact_view_title);

            m_dataInitialized = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if(view == null) {
            view = inflater.inflate(R.layout.frame_contact, container, false);

            m_contactListView = (ListView) view.findViewById(R.id.id_listview_contacts);

            m_contactListView.setAdapter(m_contactListAdapter);
            m_contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    ListView listView = (ListView)parent;
                    ContactListAdapter adapter = (ContactListAdapter)listView.getAdapter();

                    Object object = adapter.getItem(position);
                    if(object == null)
                        return true;

                    PersonalInfo info = (PersonalInfo) object;
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle(info.name).setMessage("Move to Favorite").show();
                    return true;
                }
            });
        }

        return view;
    }
}
