package appLogic;

import android.content.Context;

import com.fanxin.database.ConversationTable;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Long on 5/26/2015.
 */
public class ConversationManager {
    public List<Conversation> conversations;

    private HashMap<String, Integer> indexMap;

    private int totalUnreadCount = 0;

    ConversationTable conversationTable = null;

    public ConversationManager(Context context) {
        conversationTable = new ConversationTable(context);
        indexMap = new HashMap<>();

        refresh();
    }

    public void addOrReplaceConversation(Message message) {
        Conversation conversation = new Conversation();
        conversation.friendId = message.friendId;
        conversation.body = message.body;
        conversation.time = message.time;
        conversation.isSent = message.isSent;

        deleteConversation(conversation.friendId);

        conversations.add(conversation);
        indexMap.put(conversation.friendId, conversations.size() - 1);

        conversationTable.replaceConversation(conversation);
    }

    public void refresh() {
        conversations = conversationTable.getConversations();

        for(int idx = 0; idx < conversations.size(); idx ++){
            indexMap.put(conversations.get(idx).friendId, idx);
        }
    }

    public void deleteConversation(String friendId){
        if(indexMap.containsKey(friendId)){
            int idx = indexMap.get(friendId);
            conversations.remove(idx);
            indexMap.remove(friendId);
        }
    }
}
