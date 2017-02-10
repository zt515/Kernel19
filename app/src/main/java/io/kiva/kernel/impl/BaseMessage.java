package io.kiva.kernel.impl;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.model.MessageType;
import io.kiva.kernel.model.MessageData;

public abstract class BaseMessage implements IMessage
{
    private MessageType type;
    private MessageFrom from;

    public BaseMessage(MessageType type, MessageFrom from)
    {
        this.type = type;
        this.from = from;
    }

    public void setType(MessageType type)
    {
        this.type = type;
    }

    public void setFrom(MessageFrom from)
    {
        this.from = from;
    }

    @Override
    public MessageType getType()
    {
        // TODO: Implement this method
        return type;
    }

    @Override
    public MessageFrom getFrom()
    {
        // TODO: Implement this method
        return from;
    }

    @Override
    public abstract <T extends MessageData> T getData();
}
