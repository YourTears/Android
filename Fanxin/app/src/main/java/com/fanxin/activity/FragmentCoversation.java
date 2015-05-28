package com.fanxin.activity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanxin.app.Constant;
import com.fanxin.app.R;
import com.fanxin.adapter.ConversationAdapter;

import appLogic.AppConstant;

public class FragmentCoversation extends Fragment {

    private boolean hidden;
    private ListView listView;
    private ConversationAdapter adapter;

    public RelativeLayout errorItem;
    public TextView errorText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if(AppConstant.conversationView == null)
            AppConstant.conversationView = inflater.inflate(R.layout.fragment_home, container, false);

        return AppConstant.conversationView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false))
            return;
        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);

        listView = (ListView) getView().findViewById(R.id.conversation_list);
        adapter = new ConversationAdapter(getActivity(), AppConstant.conversationManager.conversations);
        listView.setAdapter(adapter);
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        AppConstant.conversationManager.refresh();
        adapter.notifyDataSetChanged();
    }

    /**
     * 获取所有会话
     *
     * @return +
     */
    private List<Object> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, Object> conversations = null;

        List<Object> list = new ArrayList<Object>();
        List<Object> topList1 = new ArrayList<Object>();

        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     */
    private void sortConversationByLastChatTime(
            List<Object> conversationList) {
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