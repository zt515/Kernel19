package io.kiva.kernel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.kiva.kernel.R;
import io.kiva.kernel.model.MessageFrom;

public class BubbleViewHolder extends ViewHolder
{
    public BubbleViewHolder(Context ctx, int resId, MessageFrom from) {
        super(LayoutInflater.from(ctx).inflate(resId, null, false), from);
    }
    
    public void setBubble(View v, MessageFrom from) {
        switch (from) {
            case FROM_ME:
                v.setBackgroundResource(R.drawable.me_bg);
                break;
            case FROM_OTHER:
                v.setBackgroundResource(R.drawable.k19_bg);
                break;
        }
    }
}
