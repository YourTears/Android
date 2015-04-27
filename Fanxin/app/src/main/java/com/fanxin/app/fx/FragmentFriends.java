package com.fanxin.app.fx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanxin.app.Constant;
import com.fanxin.app.R;
import com.fanxin.app.fx.others.ContactAdapter;
import com.fanxin.app.widget.Sidebar;

import appLogic.FriendInfo;
import appLogic.Gender;

/**
 * 联系人列表页
 * 
 */
@SuppressLint("InflateParams")
public class FragmentFriends extends Fragment {
    private ContactAdapter adapter;
    private List<FriendInfo> contactList;
    private ListView listView;
    private boolean hidden;
    private Sidebar sidebar;
 
    private List<String> blackList;
    private TextView tv_unread;
    private TextView tv_total;
    private LayoutInflater infalter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.fragment_contactlist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false))
            return;
        
        listView = (ListView) getView().findViewById(R.id.list);
       
        // 黑名单列表
        blackList = new ArrayList<String>();
        contactList = new ArrayList<FriendInfo>();
        // 获取设置contactlist
        getContactList();
        infalter=LayoutInflater.from(getActivity());
        View headView = infalter.inflate(R.layout.item_contact_list_header,
                null);
        listView.addHeaderView(headView);
        View footerView = infalter.inflate(R.layout.item_contact_list_footer,
                null);
        listView.addFooterView(footerView);
        sidebar = (Sidebar) getView().findViewById(R.id.sidebar);
        sidebar.setListView(listView);
        tv_unread = (TextView) headView.findViewById(R.id.tv_unread);
        if(((MainActivity)getActivity()).unreadAddressLable.getVisibility()==View.VISIBLE){
            tv_unread.setVisibility(View.VISIBLE);
            tv_unread.setText(((MainActivity)getActivity()).unreadAddressLable.getText());
            
        }else{
            tv_unread.setVisibility(View.GONE);
        }
        
        tv_total = (TextView) footerView.findViewById(R.id.tv_total);
        // 设置adapter
        adapter = new ContactAdapter(getActivity(), R.layout.item_contact_list,
                contactList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if(position!=0&&position!=contactList.size()+1){

                    FriendInfo user=contactList.get(position-1);
                    String username = user.sys_id;
                    startActivity(new Intent(getActivity(), UserInfoActivity.class)
                    .putExtra("hxid", username).putExtra("nick", user.nickName ).putExtra("avatar", user.imageUrl ).putExtra("sex", user.gender.toString() ));
                }
               

            }
        });
       
        tv_total.setText(String.valueOf(contactList.size())+"位联系人");
      
        RelativeLayout re_newfriends=(RelativeLayout) headView.findViewById(R.id.re_newfriends);
        RelativeLayout re_chatroom=(RelativeLayout) headView.findViewById(R.id.re_chatroom);
        re_newfriends.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),NewFriendsActivity.class)); 
                
            }
            
        });
        re_chatroom.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ChatRoomActivity.class)); 
            }
            
        });

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    
    
    // 刷新ui
    public void refresh() {
        try {
            // 可能会在子线程中调到这方法
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    getContactList();
                    adapter.notifyDataSetChanged();
                    tv_total.setText(String.valueOf(contactList.size())+"位联系人");
                    if(((MainActivity)getActivity()).unreadAddressLable.getVisibility()==View.VISIBLE){
                        tv_unread.setVisibility(View.VISIBLE);
                        tv_unread.setText(((MainActivity)getActivity()).unreadAddressLable.getText());
                        
                    }else{
                        tv_unread.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    private void getContactList() {
        contactList.clear();

        FriendInfo fi1 = new FriendInfo();
        fi1.gender = Gender.female;
        fi1.id = "feifei";
        fi1.name = "Fei Fei";
        fi1.imageUrl = "http://static-jkxy.qiniudn.com/eoeandroid%2Fferweima.jpg";

        contactList.add(fi1);
    }

    @SuppressLint("DefaultLocale")
    public class PinyinComparator implements Comparator<FriendInfo> {

        @SuppressLint("DefaultLocale")
        @Override
        public int compare(FriendInfo o1, FriendInfo o2) {
            // TODO Auto-generated method stub
            String py1 = o1.name;
            String py2 = o2.name;
            // 判断是否为空""
            if (isEmpty(py1) && isEmpty(py2))
                return 0;
            if (isEmpty(py1))
                return -1;
            if (isEmpty(py2))
                return 1;

            return py1.compareTo(py2);
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }

    }
}
