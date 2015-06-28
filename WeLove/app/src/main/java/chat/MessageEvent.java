package chat;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;

/**
 * Created by Long on 15/6/28.
 */
public class MessageEvent {
  public enum Type {
    Come, Receipt
  }

  private AVIMTypedMessage message;
  private Type type;

  public MessageEvent(AVIMTypedMessage message, Type type) {
    this.message = message;
    this.type = type;
  }

  public AVIMTypedMessage getMessage() {
    return message;
  }

  public Type getType() {
    return type;
  }
}
