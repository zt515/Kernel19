package io.kiva.kernel.wetalk;

import io.kiva.kernel.impl.MessageBuilder;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.model.MessageFrom;

/**
 * @author kiva
 */

public class WetalkDataConverter {
    public static IMessage convertToMessage(MessageFrom from, String data) {
        if (data.startsWith("emoj:")) {
            int emojiId = Integer.parseInt(data.substring(data.indexOf(':') + 1));
            return MessageBuilder.emoticon(from, emojiId);
        } else if (data.startsWith("text:")) {
            String text = data.substring(data.indexOf(':') + 1);
            return MessageBuilder.text(from, text);
        }
        return MessageBuilder.text(from, data);
    }

    public static String convertToText(IMessage message) {
        StringBuilder builder = new StringBuilder();
        switch (message.getType()) {
            case TYPE_EMOTICON:
                builder.append("emoj:");
                break;
            case TYPE_TEXT:
                builder.append("text:");
                break;
        }
        builder.append(message.toString());
        return builder.toString();
    }
}
