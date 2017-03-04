package io.kiva.kernel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.twpp.text.IEditor;
import org.twpp.text.impl.EditorFactory;
import org.twpp.text.impl.editor.highlight.language.java.JavaLang;
import org.twpp.text.skin.LightSkin;

import io.kiva.kernel.R;
import io.kiva.kernel.impl.CodeMessage;
import io.kiva.kernel.impl.TextMessageData;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.utils.EditorKit;

class CodeMessageViewHolder extends BubbleViewHolder {
    private IEditor editor;

    CodeMessageViewHolder(Context context, int resId, MessageFrom from) {
        super(context, resId, from);

        @SuppressLint("InflateParams")
        View content = LayoutInflater.from(context).inflate(R.layout.code_message, null, false);
        FrameLayout container = (FrameLayout) content.findViewById(R.id.code_container);
        setBubble(container, from);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        editor = EditorKit.createEditor(context);
        editor.switchToViewMode();

        container.addView(editor.getEditView());
        getContent().addView(content, layoutParams);
    }

    @Override
    public void onBind(IMessage message) {
        CodeMessage m = (CodeMessage) message;
        TextMessageData data = m.getData();
        editor.setText(data.getData());
    }
}
