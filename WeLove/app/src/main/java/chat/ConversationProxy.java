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
    private MessageAgent messageAgent;
    private AVIMConversation conversation = null;
    private MessageManager messageManager = null;
    private static ChatManager chatManager = ChatManager.getInstance();

    public ConversationProxy(UserInfo user, MessageManager messageManager){
        chatManager.fetchConversationWithUserId(user.externalId, new AVIMConversationCreatedCallback() {
            @Override
            public void done(AVIMConversation avimConversation, AVException e) {
                chatManager.registerConversation(avimConversation);
                conversation = avimConversation;
                messageAgent = new MessageAgent(conversation);
            }
        });

        this.messageManager = messageManager;
    }

    public static void connectChatServer() {
        chatManager.openClientWithSelfId(AppConstant.meInfo.externalId, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVException e) {
                int a = 2;
            }
        });
    }

    public boolean isConnected(){
        return chatManager.isConnect();
    }

    public void sendMessage(Message message){
        if(!isConnected())
            return;

        AVIMTextMessage avimTextMessage = null;
        if(messageAgent != null) {
            message.externalId = messageAgent.sendText(message.body);
        }
    }

    public void handleMessageEvent(MessageEvent messageEvent) {
        AVIMTypedMessage avimTypedMessage = messageEvent.getMessage();
        if(avimTypedMessage == null)
            return;

        Message message = null;

        if (avimTypedMessage.getConversationId().equals(conversation
                .getConversationId())) {
            if (messageEvent.getType() == MessageEvent.Type.Come) {
                if(AppConstant.userManager.containExternalId(avimTypedMessage.getFrom())){
                    message = convertMessage(avimTypedMessage);
                }
            } else if (messageEvent.getType() == MessageEvent.Type.Receipt) {
                message = messageManager.getMessageByExternal(avimTypedMessage.getMessageId());
                if(message != null)
                    message.status = convertMessageStatus(avimTypedMessage.getMessageStatus());
            }
        }

        if(message != null){
            messageManager.addOrReplaceMessage(message);
        }
    }

    private Message convertMessage(AVIMTypedMessage avimTypedMessage) {
        Message message = null;

        switch (AVIMReservedMessageType.getAVIMReservedMessageType(avimTypedMessage.getMessageType())) {
            case TextMessageType:
                message = new Message();
                AVIMTextMessage textMessage = (AVIMTextMessage) avimTypedMessage;
                message.body = textMessage.getText();
        }

        if (message != null) {
            message.id = UUID.randomUUID();
            message.externalId = avimTypedMessage.getMessageId();
            message.friendId = AppConstant.userManager.getUserByExternal(avimTypedMessage.getFrom()).id;
            message.isRead = true;
            message.time = avimTypedMessage.getTimestamp();
            message.direction = Message.Direction.RECEIVE;
            message.status = convertMessageStatus(avimTypedMessage.getMessageStatus());
        }

        return message;
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

            default:
                status = Message.MessageStatus.SUCCEED;
        }

        return status;
    }
}