package io.kiva.kernel.ai;

import io.kiva.kernel.chat.OnReplyListener;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.user.User;

/**
 * @author kiva
 */

public class AIUser extends User {
    public AIUser(String name) {
        super(name);
    }

    public AIUser(String name, String sign) {
        super(name, sign);
    }

    public AIUser(String name, OnReplyListener replyListener) {
        super(name, replyListener);
    }

    @Override
    public void onNewMessage(IMessage message) {
    }
}
