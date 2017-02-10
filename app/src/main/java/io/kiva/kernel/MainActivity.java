package io.kiva.kernel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import io.kiva.kernel.adapter.MessageAdapter;
import io.kiva.kernel.ai.AIKernel19;
import io.kiva.kernel.chat.ChatManager;
import io.kiva.kernel.chat.History;
import io.kiva.kernel.user.User;

public class MainActivity extends Activity {
    private ListView msgList;
    private ChatManager chatManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initChat();
        initMessageList();
        initWidget();
    }

    private void initChat() {
        AIKernel19 chatUser = new AIKernel19();
        initTitleText(chatUser);

        chatManager = new ChatManager(History.loadHistory());
        chatManager.setChatUser(chatUser);
    }

    private void initWidget() {
        final EditText input = (EditText) findViewById(R.id.mainInput);

        findViewById(R.id.mainSend).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                String text = input.getText().toString();
                if (!text.isEmpty()) {
                    input.setText(null);
                    chatManager.sendTextMessage(text);
                    msgList.smoothScrollToPosition(msgList.getCount() - 1);
                }
            }
        });
    }

    private void initMessageList() {
        MessageAdapter adapter = new MessageAdapter(this);
        chatManager.attachViewAdapter(adapter);

        msgList = (ListView) findViewById(R.id.msgList);
        msgList.setAdapter(adapter);
    }

    public void initTitleText(User user) {
        try {
            TextView tv = (TextView) findViewById(R.id.userSignText);
            tv.setText(user.getSign());

            tv = (TextView) findViewById(R.id.userNameText);
            tv.setText(user.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatManager.onDestroy();
    }
}
