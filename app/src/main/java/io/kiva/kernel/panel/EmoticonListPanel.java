package io.kiva.kernel.panel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;

import io.kiva.kernel.R;
import io.kiva.kernel.chat.OnMessageListener;
import io.kiva.kernel.impl.EmoticonMessage;
import io.kiva.kernel.impl.MessageBuilder;
import io.kiva.kernel.model.MessageFrom;

/**
 * @author kiva
 */

public class EmoticonListPanel implements Panel {
    private View view;
    private OnMessageListener receiver;

    public EmoticonListPanel(OnMessageListener receiver) {
        this.receiver = receiver;
    }

    @Override
    public View getView(Context context) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.panel_emoticon, null, false);
            GridView gridView = (GridView) view.findViewById(R.id.emoticon_list);
            IconAdapter iconAdapter = new IconAdapter(context);
            gridView.setAdapter(iconAdapter);
        }

        return view;
    }

    private class IconAdapter extends BaseAdapter {
        private Context context;

        public IconAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return EmoticonMessage.getEmojiCount();
        }

        @Override
        public Object getItem(int i) {
            return EmoticonMessage.getEmoji(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ImageButton imageView;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.emoticon_item, null, false);
                imageView = (ImageButton) view.findViewById(R.id.emoticon_image);
                view.setTag(imageView);
            } else {
                imageView = (ImageButton) view.getTag();
            }

            int resId = (int) getItem(position);
            imageView.setImageResource(resId);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (receiver != null) {
                        receiver.onNewMessage(MessageBuilder.emoticon(MessageFrom.FROM_ME, position));
                    }
                }
            });

            return view;
        }
    }
}
