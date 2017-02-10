package io.kiva.kernel.impl;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.model.MessageType;
import io.kiva.kernel.model.MessageData;
import io.kiva.kernel.model.MessageFrom;

public class ImageMessage extends BaseMessage
{
    private ImageMessageData data;
    
    public ImageMessage(MessageFrom from)
    {
        super(MessageType.TYPE_IMAGE, from);
    }

    public ImageMessage(MessageFrom from, ImageMessageData imageMessageData) {
        super(MessageType.TYPE_IMAGE, from);
        setData(imageMessageData);
    }

    public void setData(ImageMessageData data)
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
