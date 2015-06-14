package appLogic;

import android.content.Context;

import com.fanxin.adapter.ConversationAdapter;
import com.fanxin.database.ConversationTable;
import com.fanxin.database.MessageTable;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Long on 5/26/2015.
 */
public class ConversationManager {
    private List<Conversation> conversations;
    private HashMap<String, Conversation> map;
    public ConversationAdapter adapter;

    public int totalUnreadCount = 0;

    ConversationTable conversationTable = null;
    MessageTable messageTable = null;

    public ConversationManager(Context context) {
        conversationTable = ConversationTable.getInstance(context);
        messageTable = MessageTable.getInstance(context);

        map = new HashMap<>();
        refresh();
        adapter = new ConversationAdapter(context, conversations);
    }

    public synchronized void addOrReplaceConversation(Message message) {
        Conversation conversation = new Conversation();
        conversation.friendId = message.friendId;
        conversation.body = message.body;
        conversation.time = message.time;
        //conversation.status = message.status;

        deleteConversationInternal(conversation.friendId);

        conversations.add(0, conversation);
        map.put(conversation.friendId, conversation);

        conversationTable.replaceConversation(conversation);

        adapter.notifyDataSetChanged();
    }

    public synchronized void refresh() {
        conversations = conversationTable.getConversations();

        for(int idx = conversations.size() - 1; idx >= 0; idx --){
            Conversation conversation = conversations.get(idx);
            if(AppConstant.userManager.containFriend(conversation.friendId)){
                map.put(conversation.friendId, conversation);
            } else{
                conversationTable.deleteConversation(conversation.friendId);
                messageTable.deleteMessages(conversation.friendId);
                conversations.remove(idx);
            }
        }
    }

    public synchronized void deleteConversation(String friendId){
        deleteConversationInternal(friendId);
        conversationTable.deleteConversation(friendId);
        messageTable.deleteMessages(friendId);
        adapter.notifyDataSetChanged();
    }

    public synchronized void saveConversations(){
        for(Conversation conversation : conversations){
            conversationTable.replaceConversation(conversation);
        }
    }

    private void deleteConversationInternal(String friendId){
        if(map.containsKey(friendId)){
            conversations.remove(map.get(friendId));
            map.remove(friendId);
        }
    }
}
