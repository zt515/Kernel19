package io.kiva.kernel.chat;

import android.graphics.Bitmap;

import io.kiva.kernel.adapter.BaseMessageAdapter;
import io.kiva.kernel.impl.CodeMessage;
import io.kiva.kernel.impl.EmoticonMessage;
import io.kiva.kernel.impl.EmoticonMessageData;
import io.kiva.kernel.impl.ImageMessage;
import io.kiva.kernel.impl.MessageBuilder;
import io.kiva.kernel.impl.TextMessage;
import io.kiva.kernel.impl.TextMessageData;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.user.User;

public class ChatManager implements OnReplyListener, OnMessageListener {
    private MessageHolder messageHolder;
    private BaseMessageAdapter adapter;
    private User chatUser;
    private OnMessageListener messageListener;

    public ChatManager(MessageHolder messageHolder) {
        this.messageHolder = messageHolder;
    }

    public void setChatUser(User chatUser) {
        if (this.chatUser != null) {
            this.chatUser.setReplyListener(null);
        }

        this.chatUser = chatUser;
        this.messageListener = chatUser;
        this.chatUser.setReplyListener(this);
    }

    private void addMessageList(IMessage msg) {
        messageHolder.add(msg);
        notifyDataChanged();
    }

    public void sendMessage(final IMessage msg) {
        addMessageList(msg);
        if (messageListener != null) {
            messageListener.onNewMessage(msg);
        } else {
            addMessageList(MessageBuilder.text(MessageFrom.FROM_OTHER, "[暂无可用回复]"));
        }
    }

    public void sendTextMessage(String text) {
        TextMessage m = MessageBuilder.text(MessageFrom.FROM_ME, text);
        sendMessage(m);
    }

    public void sendCodeMessage(String code) {
        CodeMessage m = MessageBuilder.code(MessageFrom.FROM_ME, code);
        sendMessage(m);
    }

    public void sendImageMessage(Bitmap bitmap) {
        ImageMessage m = MessageBuilder.image(MessageFrom.FROM_ME, bitmap);
        sendMessage(m);
    }

    public void sendEmoticonMessage(int emojiId) {
        EmoticonMessage m = MessageBuilder.emoticon(MessageFrom.FROM_ME, emojiId);
        sendMessage(m);
    }

    @Override
    public void onNewReply(IMessage reply) {
        addMessageList(reply);
    }

    @Override
    public void onNewMessage(IMessage message) {
        sendMessage(message);
    }

    public void notifyDataChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void attachViewAdapter(BaseMessageAdapter adapter) {
        this.adapter = adapter;
        if (adapter != null) {
            adapter.setData(messageHolder);
        }
    }

    public void setMessageHolder(MessageHolder messageHolder) {
        this.messageHolder = messageHolder;
    }

    public MessageHolder getMessageHolder() {
        return messageHolder;
    }

    public void onDestroy() {
        History.saveHistory(getMessageHolder());
    }
}
