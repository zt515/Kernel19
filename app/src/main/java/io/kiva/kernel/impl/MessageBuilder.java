package io.kiva.kernel.impl;

import android.graphics.Bitmap;

import io.kiva.kernel.model.MessageFrom;

/**
 * @author kiva
 */

public class MessageBuilder {
    public static TextMessage text(MessageFrom from, String text) {
        return new TextMessage(from, new TextMessageData(text));
    }

    public static CodeMessage code(MessageFrom from, String code) {
        return new CodeMessage(from, new TextMessageData(code));
    }

    public static EmoticonMessage emoticon(MessageFrom from, int emojiId) {
        return new EmoticonMessage(from, new EmoticonMessageData(emojiId));
    }

    public static ImageMessage image(MessageFrom from, Bitmap image) {
        return new ImageMessage(from, new ImageMessageData(image));
    }
}
