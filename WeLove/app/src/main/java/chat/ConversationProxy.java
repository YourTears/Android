package chat;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import java.util.List;
import java.util.UUID;

import appLogic.AppConstant;
import appLogic.Message;
import appLogic.UserInfo;
import chat.leanchatlib.controller.ChatManager;
import chat.leanchatlib.controller.MessageAgent;

/**
 * Created by Long on 6/22/2015.
 */
public class ConversationProxy {
    private ChatManager chatManager = ChatManager.getInstance();
    private boolean connected = false;

    public ConversationProxy(){
        connectChatServer();
    }

    public void connectChatServer() {
        chatManager.openClientWithSelfId(AppConstant.meInfo.chatId, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVException e) {
                if (e == null) {
                    connected = true;
                }
            }
        });
    }

    public boolean isConnected(){
        return connected;
    }

    public void sendMessage(String chatId, final Message message, final SendMessageCallback sendMessageCallback){
        if(!isConnected())
            return;

        AVIMTypedMessage typedMessage = convertMessage(message);
        if(typedMessage != null){
            chatManager.sendMessage(chatId, typedMessage, new MessageAgent.SendCallback() {
                @Override
                public void onError(AVIMTypedMessage typedMessage, Exception e) {
                    message.status = Message.MessageStatus.FAIL;
                    sendMessageCallback.done(message);
                }

                @Override
                public void onSuccess(AVIMTypedMessage typedMessage) {
                    message.status = Message.MessageStatus.SUCCEED;
                    sendMessageCallback.done(message);
                }
            });
        }
    }

    public Message getMessageByEvent(MessageEvent messageEvent) {
        AVIMTypedMessage avimTypedMessage = messageEvent.getMessage();
        if (avimTypedMessage == null)
            return null;

        Message message = null;

        if (messageEvent.getType() == MessageEvent.Type.Come) {
            message = convertMessage(avimTypedMessage);
        } else if (messageEvent.getType() == MessageEvent.Type.Receipt) {
//            message = AppConstant.messageTable.getMessageByExternalId(avimTypedMessage.getMessageId());
//            if (message != null)
//                message.status = convertMessageStatus(avimTypedMessage.getMessageStatus());
        }

        return message;
    }

    private Message convertMessage(AVIMTypedMessage avimTypedMessage) {
        UserInfo user = AppConstant.userManager.getUserByExternal(avimTypedMessage.getFrom());
        if(user == null)
            return null;

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
            message.friendId = user.id;
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

        if(typedMessage != null){
            typedMessage.setMessageId(message.externalId);
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

    public static void initApp(Context context){
        AVOSCloud.initialize(context, "g71g5iqo3dpe3o09mwho925607kdsuy4foevpcwnqfny8bkr",
                "98s54ea6v0lbixzpbb2x339cfn4dhx19zoixixmurwwic1cd");
    }
}