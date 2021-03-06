package chat.leanchatlib.controller;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import java.io.File;
import java.io.IOException;

import chat.leanchatlib.utils.PathUtils;
import chat.leanchatlib.utils.PhotoUtils;
import chat.leanchatlib.utils.Utils;

/**
 * Created by lzw on 14/11/23.
 */
public class MessageAgent {
  private AVIMConversation conversation;
  private ChatManager chatManager;

  public MessageAgent(AVIMConversation conversation) {
    this.conversation = conversation;
    chatManager = ChatManager.getInstance();
  }

  private void sendMsg(final AVIMTypedMessage msg, final String originPath, final SendCallback callback) {
    if (!chatManager.isConnect()) {
      Utils.log("im not connect");
    }
    conversation.sendMessage(msg, AVIMConversation.RECEIPT_MESSAGE_FLAG, new AVIMConversationCallback() {
      @Override
      public void done(AVException e) {
        if (e == null && originPath != null) {
          File tmpFile = new File(originPath);
          File newFile = new File(PathUtils.getChatFilePath(msg.getMessageId()));
          boolean result = tmpFile.renameTo(newFile);
          if (!result) {
            throw new IllegalStateException("move file failed, can't use local cache");
          }
        }
        if (callback != null) {
          if (e != null) {
            callback.onError(msg, e);
          } else {
            callback.onSuccess(msg);
          }
        }
      }
    });
  }

  public void sendMessage(AVIMTypedMessage msg, final SendCallback sendCallback){
    sendMsg(msg, null, sendCallback);
  }

  public void resendMessage(final AVIMTypedMessage msg, final SendCallback sendCallback) {
    conversation.sendMessage(msg, AVIMConversation.RECEIPT_MESSAGE_FLAG, new AVIMConversationCallback() {
      @Override
      public void done(AVException e) {
        if (e != null) {
          sendCallback.onError(msg, e);
        } else {
          sendCallback.onSuccess(msg);
        }
      }
    });
  }

  public String sendText(String content, final SendCallback sendCallback) {
    AVIMTextMessage textMsg = new AVIMTextMessage();
    textMsg.setText(content);
    sendMsg(textMsg, null, sendCallback);

      return textMsg.getMessageId();
  }

  public void sendImage(String imagePath, final SendCallback sendCallback) {
    final String newPath = PathUtils.getChatFilePath(Utils.uuid());
    PhotoUtils.compressImage(imagePath, newPath);
    try {
      AVIMImageMessage imageMsg = new AVIMImageMessage(newPath);
      sendMsg(imageMsg, newPath, sendCallback);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendLocation(double latitude, double longitude, String address, final SendCallback sendCallback) {
    AVIMLocationMessage locationMsg = new AVIMLocationMessage();
    AVGeoPoint geoPoint = new AVGeoPoint(latitude, longitude);
    locationMsg.setLocation(geoPoint);
    locationMsg.setText(address);
    sendMsg(locationMsg, null, sendCallback);
  }

  public void sendAudio(String audioPath, final SendCallback sendCallback) {
    try {
      AVIMAudioMessage audioMsg = new AVIMAudioMessage(audioPath);
      sendMsg(audioMsg, audioPath, sendCallback);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public interface SendCallback {

    void onError(AVIMTypedMessage message, Exception e);

    void onSuccess(AVIMTypedMessage message);

  }
}
