package io.kiva.kernel.model;

public interface IMessage
{
    public MessageType getType();
    public MessageFrom getFrom();
    public <T extends MessageData> T getData();
}

