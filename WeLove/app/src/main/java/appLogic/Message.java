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
    public MessageStatus status = MessageStatus.INPROGRESS;
    public boolean isRead = true;
    public MessageType type;

    public Message() {

    }

    public Message(UUID id, String friendId, Direction direction, String body, long time, MessageType type) {
        this.id = id;
        this.friendId = friendId;
        this.direction = direction;
        this.body = body;
        this.time = time;
        this.type = type;
    }

    public static MessageStatus parseStatus(int status){
        switch (status)
        {
            case 0:
                return MessageStatus.SUCCEED;
            case 1:
                return MessageStatus.INPROGRESS;
            case 2:
                return MessageStatus.FAIL;

            default:
                return MessageStatus.SUCCEED;
        }
    }

    public static int parseStatus(MessageStatus status){
        switch (status){
            case SUCCEED:
                return 0;
            case INPROGRESS:
                return 1;
            case FAIL:
                return 2;

            default:
                return 0;
        }
    }

    public static Direction parseDirection(int direction){
        switch (direction){
            case 0:
                return Direction.SEND;
            case 1:
                return Direction.RECEIVE;

            default:
                return Direction.SEND;
        }
    }

    public static int parseDirection(Direction direction){
        switch (direction){
            case SEND:
                return 0;
            case RECEIVE:
                return 1;

            default:
                return 0;
        }
    }

    public static MessageType parseType(int type){
        switch (type){
            case 0:
                return MessageType.TEXT;
            case 1:
                return MessageType.IMAGE;
            case 2:
                return MessageType.AUDIO;
            case 3:
                return MessageType.AUDIO;

            default:
                return MessageType.TEXT;
        }
    }

    public static int parseType(MessageType type){
        switch (type){
            case TEXT:
                return 0;
            case IMAGE:
                return 1;
            case AUDIO:
                return 2;
            case VIDEO:
                return 3;

            default:
                return 0;
        }
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

    public enum MessageStatus {
        SUCCEED,
        INPROGRESS,
        FAIL
    }
}
