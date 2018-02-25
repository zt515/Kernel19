package io.kiva.kernel.wetalk;

import android.content.Context;

import io.kiva.kernel.chat.OnMessageListener;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.user.User;

/**
 * @author kiva
 */

public class WetalkUser extends User {
    private Context context;
    private OnMessageListener forward;

    public WetalkUser(Context context, OnMessageListener forward) {
        super("Kernel.19", "你好，我是十九，你的私人内核。");
        this.context = context;
        this.forward = forward;
    }

    @Override
    public void onNewMessage(IMessage message) {
        if (forward != null) {
            forward.onNewMessage(message);
        }
    }
}
