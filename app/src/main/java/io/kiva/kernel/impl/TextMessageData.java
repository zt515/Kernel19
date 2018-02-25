package io.kiva.kernel.impl;
import io.kiva.kernel.model.MessageData;

public class TextMessageData extends MessageData
{
    private String data;

    public TextMessageData(String data)
    {
        this.data = data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public String getData()
    {
        return data;
    }

    @Override
    public String toString() {
        return getData();
    }
}
