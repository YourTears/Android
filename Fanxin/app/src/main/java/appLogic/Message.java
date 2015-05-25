package appLogic;

import java.util.UUID;

/**
 * Created by Long on 5/17/2015.
 */
public class Message {
    public UUID id = null;
    public String friendId = null;
    public Direction direction;
    public String body = null;
    public long time = 0;
    public boolean isSent = false;
    public boolean isRead = true;
    public MessageType type;

    public Message() {

    }


    public Message(UUID id, String friendId, Direction direction, String body, long time, MessageType type, boolean isSent) {
        this.id = id;
        this.friendId = friendId;
        this.direction = direction;
        this.body = body;
        this.time = time;
        this.type = type;
        this.isSent = isSent;
    }

    public enum Direction {
        SEND,
        RECEIVE
    }

    public enum MessageType {
        TEXT,
        IMAGE,
        AUDIO,
        VIDEO
    }
}
