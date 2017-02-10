package io.kiva.kernel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import io.kiva.kernel.R;
import io.kiva.kernel.impl.ImageMessage;
import io.kiva.kernel.impl.ImageMessageData;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.model.MessageFrom;

public class ImageMessageViewHolder extends BubbleViewHolder
 {
    protected ImageView image;

    public ImageMessageViewHolder(Context ctx, int resId, MessageFrom from) {
        super(ctx, resId, from);
        
        View content = LayoutInflater.from(ctx).inflate(R.layout.image_message, null, false);
        image = (ImageView) content.findViewById(R.id.image_message);
        
        setBubble(image, from);
        getContent().addView(content, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onBind(IMessage message)
    {
        ImageMessage m = (ImageMessage) message;
        ImageMessageData data = m.getData();
        image.setImageBitmap(data.getData());
    }
}
