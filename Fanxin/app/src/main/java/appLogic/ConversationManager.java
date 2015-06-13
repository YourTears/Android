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
    private HashMap<String, Integer> indexMap;
    public ConversationAdapter adapter;

    public int totalUnreadCount = 0;

    ConversationTable conversationTable = null;
    MessageTable messageTable = null;

    public ConversationManager(Context context) {
        conversationTable = ConversationTable.getInstance(context);
        messageTable = MessageTable.getInstance(context);

        indexMap = new HashMap<>();
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
        indexMap.put(conversation.friendId, 0);

        conversationTable.replaceConversation(conversation);

        adapter.notifyDataSetChanged();
    }

    public synchronized void refresh() {
        conversations = conversationTable.getConversations();

        for(int idx = 0; idx < conversations.size(); idx ++){
            indexMap.put(conversations.get(idx).friendId, idx);
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
        if(indexMap.containsKey(friendId)){
            int idx = indexMap.get(friendId);
            conversations.remove(idx);
            indexMap.remove(friendId);
        }
    }
}
