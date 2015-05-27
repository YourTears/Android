package appLogic;

/**
 * Created by Long on 5/26/2015.
 */
public class Conversation {
    public String friendId = null;
    public String body = null;
    public long time = 0;
    public boolean isSent = false;
    public int unreadCount = 0;

    public Conversation() {

    }


    public Conversation(String friendId, String body, long time, boolean isSent, int unreadCount) {
        this.friendId = friendId;
        this.body = body;
        this.time = time;
        this.isSent = isSent;
        this.unreadCount = unreadCount;
    }
}