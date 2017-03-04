package io.kiva.kernel.impl;

import io.kiva.kernel.model.MessageData;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.model.MessageType;

/**
 * @author kiva
 */

public class CodeMessage extends TextMessage {

    public CodeMessage(MessageFrom from) {
        super(from);
    }

    public CodeMessage(MessageFrom from, TextMessageData textMessageData) {
        super(from, textMessageData);
    }

    @Override
    public MessageType getType() {
        return MessageType.TYPE_CODE;
    }

    @Override
    public <T extends MessageData> T getData() {
        return super.getData();
    }
}
