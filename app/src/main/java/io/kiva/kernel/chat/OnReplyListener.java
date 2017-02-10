package io.kiva.kernel.chat;

import io.kiva.kernel.model.IMessage;

/**
 * @author kiva
 */

public interface OnReplyListener {
    void onNewReply(IMessage reply);
}
