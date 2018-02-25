package io.kiva.kernel.impl;
import io.kiva.kernel.model.MessageData;

public class EmoticonMessageData extends MessageData
{
    private int resId;
    private int emojiId;
    
    public EmoticonMessageData(int emojiId) {
        this.emojiId = emojiId;
        this.resId = EmoticonMessage.getEmoji(emojiId);
    }
    
    public int getData() {
        return resId;
    }

    @Override
    public String toString() {
        return String.valueOf(emojiId);
    }
}
