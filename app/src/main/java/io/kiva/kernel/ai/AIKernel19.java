package io.kiva.kernel.ai;

import android.app.AlertDialog;
import android.content.Context;

import com.krine.extension.IKrineLinkable;
import com.krine.extension.annotations.ExtensionConfig;
import com.krine.extension.annotations.KrineMethod;
import com.krine.interpreter.KrineInterpreter;

import java.util.Random;

import io.kiva.kernel.ai.code.CodeRunner;
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
    public static final String DEFAULT_INIT_CODE = "";

    private Context context;
    private String initCode;
    private Class[] nativeInterfaces;

    public AIKernel19(Context context) {
        super("Kernel.19", "你好，我是十九，你的私人内核。");
        this.context = context;
        nativeInterfaces = new Class[]{Kernel19Module.class};
    }

    @Override
    public void onNewMessage(IMessage message) {
        simulateReply(message);
    }

    public void setInitCode(String initCode) {
        this.initCode = initCode;
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
        final boolean randomReplyText = random.nextBoolean();

        final int emojiId = Math.abs(random.nextInt() % EmoticonMessage.getEmojiCount());

        UIKit.get().postDelayed(() -> {
            if (type == MessageType.TYPE_IMAGE) {
                listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, "十九觉得很好看"));
                listener.onNewReply(MessageBuilder.emoticon(MessageFrom.FROM_OTHER,
                        EmoticonMessage.EMOJI_SURPRISED));

            } else if (type == MessageType.TYPE_EMOTICON) {
                listener.onNewReply(MessageBuilder.emoticon(MessageFrom.FROM_OTHER, emojiId));

            } else if (type == MessageType.TYPE_CODE) {
                TextMessageData data = ((TextMessage) message).getData();
                String code = data.getData();
                CodeRunner.runCode(initCode, code, nativeInterfaces, (output) ->
                        UIKit.get().post(() -> {
                            if (!output.isEmpty()) {
                                listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, output));
                            }
                        }));

            } else {
                TextMessageData data = ((TextMessage) message).getData();
                String text = data.getData();

                if (text.contains("晚安")) {
                    listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, "晚安～"));
                } else if (text.contains("睡觉")) {
                    listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, "你睡我就睡 2333"));
                } else if (text.contains("19") || text.contains("十九")) {
                    listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, "十九在这里啦！"));
                } else if (randomReplyText) {
                    listener.onNewReply(MessageBuilder.text(MessageFrom.FROM_OTHER, "十九是傻瓜"));
                } else {
                    listener.onNewReply(MessageBuilder.emoticon(MessageFrom.FROM_OTHER, emojiId));
                }
            }
        }, delay);
    }

    @ExtensionConfig
    public class Kernel19Module implements IKrineLinkable {
        public Kernel19Module() {
        }

        @KrineMethod
        public void showDialog(String title, String content) {
            UIKit.get().post(() -> new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(content)
                    .setPositiveButton(android.R.string.yes, null)
                    .show());
        }

        @Override
        public void bindInterpreter(KrineInterpreter krineInterpreter) {
        }
    }
}
