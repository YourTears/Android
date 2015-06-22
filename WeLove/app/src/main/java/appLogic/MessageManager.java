package appLogic;

import android.content.Context;

import com.welove.adapter.MessageAdapter;
import com.welove.database.MessageTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Long on 5/24/2015.
 */
public class MessageManager {
    public String friendId;
    public List<Message> messages;
    public Map<UUID, Message> map;
    public Map<String, Message> externalMap;
    public long lastMessageTime;
    public MessageAdapter adapter;

    private MessageTable messageTable = null;

    public MessageManager(Context context, String friendId) {
        this.friendId = friendId;
        messages = new ArrayList<>();
        lastMessageTime = (new Date()).getTime();

        messageTable = MessageTable.getInstance(context);
        messages = messageTable.getMessages(friendId, (new Date()).getTime());

        map = new HashMap<>();
        externalMap = new HashMap<>();

        adapter = new MessageAdapter(context, friendId, messages);
    }

    public Message getMessageByExternal(String externalId){
        if(externalMap.containsKey(externalId))
            return externalMap.get(externalId);

        return null;
    }

    public synchronized void addOrReplaceMessage(Message message) {
        if(!map.containsKey(message.id)) {
            map.put(message.id, message);
            externalMap.put(message.externalId, message);

            messages.add(message);
        }

        messageTable.insertMessage(message);
        adapter.notifyDataSetChanged();
    }

    public Message getLastMessage(){
        int size = messages.size();
        if(size > 0)
            return messages.get(size - 1);

        Message message = new Message();
        message.friendId = friendId;
        message.body = "";
        message.time = (new Date()).getTime();

        return message;
    }
}
