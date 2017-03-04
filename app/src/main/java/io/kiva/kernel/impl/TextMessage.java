package io.kiva.kernel.impl;

import io.kiva.kernel.model.MessageData;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.model.MessageType;

public class TextMessage extends BaseMessage {
    private TextMessageData data;

    public TextMessage(MessageFrom from) {
        super(MessageType.TYPE_TEXT, from);
    }

    public TextMessage(MessageFrom from, TextMessageData textMessageData) {
        this(from);
        setData(textMessageData);
    }

    public void setData(TextMessageData data) {
        this.data = data;
    }

    @Override
    public MessageType getType() {
        return MessageType.TYPE_TEXT;
    }

    @Override
    public <T extends MessageData> T getData() {
        return (T) data;
    }

}
