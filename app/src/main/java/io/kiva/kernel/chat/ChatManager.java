package io.kiva.kernel.chat;

import io.kiva.kernel.adapter.BaseMessageAdapter;
import io.kiva.kernel.impl.EmoticonMessage;
import io.kiva.kernel.impl.EmoticonMessageData;
import io.kiva.kernel.impl.MessageBuilder;
import io.kiva.kernel.impl.TextMessage;
import io.kiva.kernel.impl.TextMessageData;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.user.User;

public class ChatManager implements OnReplyListener {
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
        TextMessage m = new TextMessage(MessageFrom.FROM_ME);
        m.setData(new TextMessageData(text));
        sendMessage(m);
    }

    public void sendEmoticonMessage(int resId) {
        EmoticonMessageData data = new EmoticonMessageData(resId);
        EmoticonMessage m = new EmoticonMessage(MessageFrom.FROM_OTHER);
        m.setData(data);
        sendMessage(m);
    }

    @Override
    public void onNewReply(IMessage reply) {
        addMessageList(reply);
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
