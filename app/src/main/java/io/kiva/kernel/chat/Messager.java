package io.kiva.kernel.chat;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.adapter.BaseMessageAdapter;
import io.kiva.kernel.impl.TextMessage;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.impl.TextMessageData;
import io.kiva.kernel.utils.UIKit;
import java.util.Random;
import io.kiva.kernel.impl.ImageMessageData;
import android.graphics.BitmapFactory;
import io.kiva.kernel.impl.ImageMessage;
import android.content.Context;
import io.kiva.kernel.R;
import io.kiva.kernel.impl.EmoticonMessage;
import io.kiva.kernel.impl.EmoticonMessageData;

public class Messager
{
    public static final int[] EMOJI = {
        R.drawable.emoji_0,
        R.drawable.emoji_1,
        R.drawable.emoji_2,
        R.drawable.emoji_3,
        R.drawable.emoji_4,
        R.drawable.emoji_5,
        R.drawable.emoji_6,
    };
    
    private Context context;
    private MessageHolder messageHolder;
    private BaseMessageAdapter adapter;

    public Messager(Context ctx, MessageHolder messageHolder)
    {
        this.context = ctx;
        this.messageHolder = messageHolder;
    }

    public void sendMessage(IMessage msg)
    {
        messageHolder.add(msg);
        notifyDataChanged();
    }
    
    public void sendMessageFromMe(final IMessage msg) {
        sendMessage(msg);
        simulateReply(msg);
    }

    private void simulateReply(final IMessage msg)
    {
        // TODO Improve this method in User class.
        Random random = new Random();
        long delay = random.nextLong() % 500;
        while (delay < 200) {
            delay += 200;
        }
        
        final boolean isText = random.nextBoolean();
        final int index = Math.abs(random.nextInt() % 6);
        
        UIKit.get().postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    if (isText) {
                        replyTextMessage("十九是傻瓜,十九最讨厌你儿子了,十九想和你玩\n你好可爱.");
                    } else {
                        ImageMessageData data = new ImageMessageData(BitmapFactory.decodeResource(context.getResources(), EMOJI[index]));
                        ImageMessage m = new ImageMessage(MessageFrom.FROM_OTHER);
                        m.setData(data);
                        sendMessage(m);
                        
                        /*
                        EmoticonMessageData data = new EmoticonMessageData(EMOJI[index]);
                        EmoticonMessage m = new EmoticonMessage(MessageFrom.FROM_OTHER);
                        m.setData(data);
                        sendMessage(m);
                        */
                    }
                }

            }, delay);
    }

    public void sendTextMessage(String text)
    {
        TextMessage m = new TextMessage(MessageFrom.FROM_ME);
        m.setData(new TextMessageData(text));
        sendMessageFromMe(m);
    }
    
    public void replyTextMessage(String text) {
        TextMessage m = new TextMessage(MessageFrom.FROM_OTHER);
        m.setData(new TextMessageData(text));
        sendMessage(m);
    }
    
    public void notifyDataChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void attachViewAdapter(BaseMessageAdapter adapter)
    {
        this.adapter = adapter;
        if (adapter != null) {
            adapter.setData(messageHolder);
        }
    }

    public void setMessageHolder(MessageHolder messageHolder)
    {
        this.messageHolder = messageHolder;
    }

    public MessageHolder getMessageHolder()
    {
        return messageHolder;
    }
}
