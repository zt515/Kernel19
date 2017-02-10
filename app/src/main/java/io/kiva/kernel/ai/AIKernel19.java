package io.kiva.kernel.ai;

import java.util.Random;

import io.kiva.kernel.chat.OnReplyListener;
import io.kiva.kernel.impl.EmoticonMessage;
import io.kiva.kernel.impl.MessageBuilder;
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

    private void simulateReply(IMessage message) {
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
                if (type == MessageType.TYPE_IMAGE) {
                    listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, "十九觉得很好看"));
                    listener.onNewReply(MessageBuilder.emoticon(MessageFrom.FROM_OTHER,
                            EmoticonMessage.EMOJI_SURPRISED));
                } else if (type == MessageType.TYPE_EMOTICON) {
                    listener.onNewReply(MessageBuilder.emoticon(MessageFrom.FROM_OTHER, emojiId));

                } else if (isText) {
                    String text = "十九是傻瓜,十九最讨厌你儿子了,十九想和你玩\n你好可爱.";
                    listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, text));
                } else {
                    listener.onNewReply(MessageBuilder.emoticon(MessageFrom.FROM_OTHER, emojiId));
                }
            }
        }, delay);
    }
}
