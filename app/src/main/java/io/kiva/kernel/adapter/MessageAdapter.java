package io.kiva.kernel.adapter;

import android.content.Context;
import io.kiva.kernel.R;
import io.kiva.kernel.chat.MessageHolder;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.model.MessageType;

public class MessageAdapter extends BaseMessageAdapter
{
    public MessageAdapter(Context context, MessageHolder data)
    {
        super(context, data);
    }

    public MessageAdapter(Context context)
    {
        super(context);
    }

    @Override
    protected MessageFrom getMessageFrom(int position)
    {
        return ((IMessage) getItem(position)).getFrom();
    }

    @Override
    protected MessageType getMessageType(int position)
    {
        return ((IMessage) getItem(position)).getType();
    }

    @Override
    protected ViewHolder onCreateViewHolder(Context context, MessageType type, MessageFrom from)
    {
        int resId = 0;
        switch (from) {
            case FROM_ME: resId = R.layout.bubble_me; break;
            case FROM_OTHER: resId = R.layout.bubble_other; break;
        }
        
        ViewHolder vh = null;
        
        if (resId != 0) {
            switch (type) {
                case TYPE_TEXT:
                    vh = new TextMessageViewHolder(context, resId, from);
                    break;
                case TYPE_IMAGE:
                    vh = new ImageMessageViewHolder(context, resId, from);
                    break;
                case TYPE_EMOTICON:
                    vh = new EmoticonMessageViewHolder(context, resId, from);
                    break;
            }
        }
        
        return vh;
    }
}
