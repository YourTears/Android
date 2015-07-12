package chat;

import appLogic.Message;

/**
 * Created by Long on 7/12/2015.
 */
public interface SendMessageCallback {
    public void done(Message message);
}
