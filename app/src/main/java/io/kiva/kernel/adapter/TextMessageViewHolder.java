package io.kiva.kernel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.kiva.kernel.R;
import io.kiva.kernel.impl.TextMessage;
import io.kiva.kernel.impl.TextMessageData;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.model.MessageFrom;
import android.graphics.Color;

class TextMessageViewHolder extends BubbleViewHolder
{
    private TextView text;

    TextMessageViewHolder(Context ctx, int resId, MessageFrom from) {
        super(ctx, resId, from);
        
        View content = LayoutInflater.from(ctx).inflate(R.layout.text_message, null, false);
        text = (TextView) content.findViewById(R.id.text_message);
        if (from == MessageFrom.FROM_ME) {
            text.setTextColor(Color.WHITE);
        }
        setBubble(text, from);
        getContent().addView(content, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onBind(IMessage message)
    {
        TextMessage m = (TextMessage) message;
        TextMessageData data = m.getData();
        text.setText(data.getData());
    }
}
