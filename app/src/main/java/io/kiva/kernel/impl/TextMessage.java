package io.kiva.kernel.impl;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.model.MessageType;
import io.kiva.kernel.model.MessageData;
import io.kiva.kernel.model.MessageFrom;

public class TextMessage extends BaseMessage
{
    private TextMessageData data;
    
    public TextMessage(MessageFrom from)
    {
        super(MessageType.TYPE_TEXT, from);
    }

    public TextMessage(MessageFrom from, TextMessageData textMessageData) {
        this(from);
        setData(textMessageData);
    }

    public void setData(TextMessageData data)
    {
        this.data = data;
    }
    
    @Override
    public MessageType getType()
    {
        // TODO: Implement this method
        return MessageType.TYPE_TEXT;
    }

    @Override
    public <T extends MessageData> T getData()
    {
        // TODO: Implement this method
        return (T) data;
    }
    
}
