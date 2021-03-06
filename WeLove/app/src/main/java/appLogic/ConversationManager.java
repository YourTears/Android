package appLogic;

import android.content.Context;

import com.welove.adapter.ConversationAdapter;
import com.welove.database.ConversationTable;
import com.welove.database.MessageTable;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Long on 5/26/2015.
 */
public class ConversationManager {
    public ConversationAdapter adapter;

    private List<Conversation> conversations;
    private HashMap<String, Conversation> map;
    private int totalUnreadCount = 0;
    private boolean needRefresh = false;

    ConversationTable conversationTable = null;

    public ConversationManager(Context context) {
        conversationTable = ConversationTable.getInstance(context);

        map = new HashMap<>();
        refreshData();
        adapter = new ConversationAdapter(context, conversations);
    }

    public synchronized void addOrReplaceConversation(Message message) {
        Conversation conversation = null;

        if(map.containsKey(message.friendId)) {
            conversation = map.get(message.friendId);
            conversations.remove(conversation);
        }
        else{
            conversation = new Conversation();
            map.put(message.friendId, conversation);
        }

        conversation.friendId = message.friendId;
        conversation.body = message.body;
        conversation.time = message.time;
        //conversation.status = message.status;

        if(!message.isRead) {
            conversation.unreadCount ++;
            addUnreadCount(1);
        }

        conversations.add(0, conversation);

        conversationTable.replaceConversation(conversation);

        needRefresh = true;
    }

    public synchronized void refreshData() {
        conversations = conversationTable.getConversations();

        totalUnreadCount = 0;

        for(int idx = conversations.size() - 1; idx >= 0; idx --){
            Conversation conversation = conversations.get(idx);
            if(AppConstant.userManager.containFriend(conversation.friendId)){
                map.put(conversation.friendId, conversation);
                addUnreadCount(conversation.unreadCount);
            } else{
                conversationTable.deleteConversation(conversation.friendId);
                AppConstant.messageTable.deleteMessages(conversation.friendId);
                conversations.remove(idx);
            }
        }

        needRefresh = true;
    }

    public synchronized void refreshView(){
        if(needRefresh) {
            adapter.notifyDataSetChanged();
        }

        needRefresh = false;
    }

    public synchronized void deleteConversation(String friendId){
        if(map.containsKey(friendId)){
            Conversation conversation = map.get(friendId);
            conversations.remove(conversation);
            map.remove(friendId);

            conversationTable.deleteConversation(friendId);

            addUnreadCount(-conversation.unreadCount);

            AppConstant.messageTable.deleteMessages(friendId);

            needRefresh = true;
        }
    }

    public synchronized void saveConversations(){
        for(Conversation conversation : conversations){
            conversationTable.replaceConversation(conversation);
        }
    }

    public void userInfoUpdated(String userId){
        if(needRefresh)
            return;

        for(Conversation conversation : conversations){
            if(conversation.friendId.equals(userId)){
                needRefresh = true;
                return;
            }
        }
    }

    public int getUnreadCount(){
        return totalUnreadCount;
    }

    private synchronized void addUnreadCount(int count){
        totalUnreadCount += count;

        // Something is wrong
        if(totalUnreadCount < 0)
            totalUnreadCount = 0;
    }

    public void cleanUnread(String friendId){
        if(!map.containsKey(friendId)){
            return;
        }

        Conversation conversation = map.get(friendId);

        int unread = conversation.unreadCount;

        if(unread != 0){
            conversation.unreadCount = 0;
            conversationTable.replaceConversation(conversation);

            addUnreadCount(-unread);
            needRefresh = true;
        }
    }
}
