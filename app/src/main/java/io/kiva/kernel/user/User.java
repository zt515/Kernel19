package io.kiva.kernel.user;

import io.kiva.kernel.chat.OnMessageListener;
import io.kiva.kernel.chat.OnReplyListener;

public abstract class User implements OnMessageListener {
    private OnReplyListener replyListener;
    private String name;
    private String sign;

    public User(String name) {
        this.name = name;
    }

    public User(String name, String sign) {
        this.name = name;
        this.sign = sign;
    }

    public User(String name, OnReplyListener replyListener) {
        this.name = name;
        this.replyListener = replyListener;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setReplyListener(OnReplyListener replyListener) {
        this.replyListener = replyListener;
    }

    public OnReplyListener getReplyListener() {
        return replyListener;
    }
}
