package io.kiva.kernel.impl;
import android.graphics.Bitmap;
import io.kiva.kernel.model.MessageData;

public class ImageMessageData extends MessageData
{
    Bitmap data;

    public ImageMessageData(Bitmap data)
    {
        this.data = data;
    }

    public void setData(Bitmap data)
    {
        this.data = data;
    }

    public Bitmap getData()
    {
        return data;
    }
}
