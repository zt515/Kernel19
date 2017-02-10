package io.kiva.kernel.chat;

public class History
{
    public static MessageHolder loadHistory() {
        return new MessageHolder();
    }
    
    public static void saveHistory(MessageHolder holder) {}
}
    
