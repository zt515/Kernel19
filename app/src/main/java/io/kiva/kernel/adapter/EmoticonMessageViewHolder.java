package io.kiva.kernel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import io.kiva.kernel.R;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.impl.EmoticonMessage;
import io.kiva.kernel.impl.EmoticonMessageData;

class EmoticonMessageViewHolder extends BubbleViewHolder
{
    private ImageView image;
    EmoticonMessageViewHolder(Context ctx, int resId, MessageFrom from) {
        super(ctx, resId, from);
        View content = LayoutInflater.from(ctx).inflate(R.layout.image_message, null, false);
        image = (ImageView) content.findViewById(R.id.image_message);

        setBubble(image, from);
        getContent().addView(content, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        
    }

    @Override
    public void onBind(IMessage message)
    {
        EmoticonMessage m = (EmoticonMessage) message;
        EmoticonMessageData data = m.getData();
        image.setImageResource(data.getData());
    }
}
