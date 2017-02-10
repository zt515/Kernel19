package io.kiva.kernel.impl;

import io.kiva.kernel.model.MessageFrom;

/**
 * @author kiva
 */

public class MessageBuilder {
    public static TextMessage text(MessageFrom from, String text) {
        return new TextMessage(from, new TextMessageData(text));
    }

    public static EmoticonMessage emoticon(MessageFrom from, int emojiId) {
        return new EmoticonMessage(from, new EmoticonMessageData(EmoticonMessage.getEmoji(emojiId)));
    }
}
