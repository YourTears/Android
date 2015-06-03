package appLogic;

import android.content.Context;

import com.fanxin.adapter.MessageAdapter;
import com.fanxin.database.MessageTable;
import com.fanxin.database.UnreadMessageTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Long on 5/24/2015.
 */
public class MessageManager {
    public String friendId;
    public List<Message> messages;
    public long lastMessageTime;
    public MessageAdapter adapter;

    private MessageTable messageTable = null;

    public MessageManager(Context context, String friendId) {
        this.friendId = friendId;
        messages = new ArrayList<>();
        lastMessageTime = (new Date()).getTime();

        messageTable = MessageTable.getInstance(context);
        messages = messageTable.getMessages(friendId, (new Date()).getTime());

        adapter = new MessageAdapter(context, friendId, messages);
    }

    public void addMessage(Message message) {
        messages.add(message);
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
        message.isSent = true;

        return message;
    }
}
