package chat;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.welove.activity.ChatActivity;

import java.util.UUID;

import appLogic.AppConstant;
import appLogic.Message;
import appLogic.MessageManager;
import appLogic.UserInfo;
import chat.leanchatlib.controller.ChatManager;
import chat.leanchatlib.controller.MessageAgent;

/**
 * Created by Long on 6/22/2015.
 */
public class ConversationProxy {
    private static ChatManager chatManager = ChatManager.getInstance();

    public ConversationProxy(){
    }

    public static void connectChatServer() {
        chatManager.openClientWithSelfId(AppConstant.meInfo.externalId, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVException e) {
            }
        });
    }

    public boolean isConnected(){
        return chatManager.isConnect();
    }

    public void sendMessage(String externalId, Message message){
        if(!isConnected())
            return;

        chatManager.sendMessage(externalId, convertMessage(message));
    }

    public Message getMessageByEvent(MessageEvent messageEvent) {
        AVIMTypedMessage avimTypedMessage = messageEvent.getMessage();
        if (avimTypedMessage == null)
            return null;

        Message message = null;

        if (messageEvent.getType() == MessageEvent.Type.Come) {
            message = convertMessage(avimTypedMessage);
        } else if (messageEvent.getType() == MessageEvent.Type.Receipt) {
            message = AppConstant.messageTable.getMessageByExternalId(avimTypedMessage.getMessageId());
            if (message != null)
                message.status = convertMessageStatus(avimTypedMessage.getMessageStatus());
        }

        return message;
    }

    private Message convertMessage(AVIMTypedMessage avimTypedMessage) {
        Message message = null;

        switch (AVIMReservedMessageType.getAVIMReservedMessageType(avimTypedMessage.getMessageType())) {
            case TextMessageType:
                message = new Message();
                AVIMTextMessage textMessage = (AVIMTextMessage) avimTypedMessage;
                message.body = textMessage.getText();
                message.type = Message.MessageType.TEXT;
        }

        if (message != null) {
            message.id = UUID.randomUUID();
            message.externalId = avimTypedMessage.getMessageId();
            message.friendId = AppConstant.userManager.getUserByExternal(avimTypedMessage.getFrom()).id;
            message.isRead = false;
            message.time = avimTypedMessage.getTimestamp();
            message.direction = Message.Direction.RECEIVE;
            message.status = convertMessageStatus(avimTypedMessage.getMessageStatus());
        }

        return message;
    }

    private AVIMTypedMessage convertMessage(Message message) {
        AVIMTypedMessage typedMessage = null;

        switch (message.type){
            case TEXT:
                AVIMTextMessage textMessage = new AVIMTextMessage();
                textMessage.setText(message.body);
                textMessage.setTimestamp(message.time);
                typedMessage = textMessage;
                break;
        }

        return typedMessage;
    }

    private Message.MessageStatus convertMessageStatus(AVIMMessage.AVIMMessageStatus avimMessageStatus){
        Message.MessageStatus status = Message.MessageStatus.SUCCEED;

        switch (avimMessageStatus) {
            case AVIMMessageStatusFailed:
                status = Message.MessageStatus.FAIL;
                break;

            case AVIMMessageStatusSent:
                status = Message.MessageStatus.SUCCEED;
                break;

            case AVIMMessageStatusSending:
                status = Message.MessageStatus.INPROGRESS;
                break;

            default:
                status = Message.MessageStatus.SUCCEED;
        }

        return status;
    }
}