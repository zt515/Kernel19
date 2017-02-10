package io.kiva.kernel.impl;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.model.MessageData;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.model.MessageType;

public class EmoticonMessage extends BaseMessage
{
    private EmoticonMessageData data;

    public EmoticonMessage(MessageFrom from)
    {
        super(MessageType.TYPE_EMOTICON, from);
    }

    public void setData(EmoticonMessageData data)
    {
        this.data = data;
    }

    @Override
    public MessageType getType()
    {
        // TODO: Implement this method
        return MessageType.TYPE_IMAGE;
    }

    @Override
    public <T extends MessageData> T getData()
    {
        // TODO: Implement this method
        return (T) data;
    }
}
