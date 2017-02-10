package io.kiva.kernel.chat;

import io.kiva.kernel.model.IMessage;

/**
 * @author kiva
 */

public interface OnMessageListener {
    void onNewMessage(IMessage message);
}
