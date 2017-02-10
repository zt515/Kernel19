package io.kiva.kernel.impl;
import io.kiva.kernel.model.MessageData;

public class EmoticonMessageData extends MessageData
{
    private int resId;
    
    public EmoticonMessageData(int resId) {
        this.resId = resId;
    }
    
    public int getData() {
        return resId;
    }
}
