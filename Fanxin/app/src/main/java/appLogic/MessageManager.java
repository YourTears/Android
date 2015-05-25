package appLogic;

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
    public int unreadCount;
    public long endTime;

    UnreadMessageTable unreadMessageTable = null;

    public MessageManager(String friendId)
    {
        this.friendId = friendId;
        messages = new ArrayList<>();
        endTime = (new Date()).getTime();

        //unreadCount = Mess
    }

    public void addMessage(Message message)
    {
        messages.add(message);
        if(message.isRead == false)
            unreadCount ++;


    }
}
