package io.kiva.kernel.ai;

import java.util.Random;

import io.kiva.kernel.chat.OnReplyListener;
import io.kiva.kernel.impl.EmoticonMessage;
import io.kiva.kernel.impl.MessageBuilder;
import io.kiva.kernel.impl.TextMessage;
import io.kiva.kernel.impl.TextMessageData;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.model.MessageType;
import io.kiva.kernel.utils.UIKit;

/**
 * @author kiva
 */

public class AIKernel19 extends AIUser {
    public AIKernel19() {
        super("Kernel.19", "你好，我是十九，你的私人内核。");
    }

    @Override
    public void onNewMessage(IMessage message) {
        simulateReply(message);
    }

    private void simulateReply(final IMessage message) {
        final OnReplyListener listener = getReplyListener();
        if (listener == null) {
            return;
        }

        Random random = new Random();
        long delay = random.nextLong() % 500;
        while (delay < 200) {
            delay += 200;
        }

        final MessageType type = message.getType();
        final boolean isText = random.nextBoolean();

        final int emojiId = Math.abs(random.nextInt() % EmoticonMessage.getEmojiCount());

        UIKit.get().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (type == MessageType.TYPE_TEXT) {
                    TextMessageData data = ((TextMessage) message).getData();
                    String text = data.getData();
                    if (text.contains("我爱你")) {
                        listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, "我也爱你！"));
                        return;
                    } else if (text.contains("晚安")) {
                        listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, "晚安～"));
                        return;
                    } else if (text.contains("睡觉")) {
                        listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, "你睡我就睡 2333"));
                        return;
                    } else if (text.contains("19") || text.contains("十九")) {
                        listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, "十九在这里啦！"));
                        return;
                    }
                }

                if (type == MessageType.TYPE_IMAGE) {
                    listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, "十九觉得很好看"));
                    listener.onNewReply(MessageBuilder.emoticon(MessageFrom.FROM_OTHER,
                            EmoticonMessage.EMOJI_SURPRISED));
                } else if (type == MessageType.TYPE_EMOTICON) {
                    listener.onNewReply(MessageBuilder.emoticon(MessageFrom.FROM_OTHER, emojiId));

                } else if (isText) {
                    String text = "十九是傻瓜";
                    listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, text));
                } else {
                    listener.onNewReply(MessageBuilder.emoticon(MessageFrom.FROM_OTHER, emojiId));
                }
            }
        }, delay);
    }
}
