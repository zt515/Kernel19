package io.kiva.kernel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import io.kiva.kernel.adapter.MessageAdapter;
import io.kiva.kernel.ai.AIKernel19;
import io.kiva.kernel.chat.ChatManager;
import io.kiva.kernel.chat.History;
import io.kiva.kernel.panel.EmoticonListPanel;
import io.kiva.kernel.panel.MusicPanel;
import io.kiva.kernel.panel.PanelManager;
import io.kiva.kernel.panel.VoicePanel;
import io.kiva.kernel.user.User;
import io.kiva.kernel.utils.ImeKit;
import io.kiva.kernel.utils.UIKit;

public class MainActivity extends Activity {
    private ListView msgList;
    private ChatManager chatManager;
    private PanelManager panelManager;
    private EditText input;

    private Runnable showPanelRunnable = new Runnable() {
        @Override
        public void run() {
            panelManager.show();
        }
    };

    private OnClickListener panelActionListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String tag = (String) view.getTag();
            ImeKit.hideIme(MainActivity.this, input);
            UIKit.get().postDelayed(showPanelRunnable, 250);
            panelManager.switchToPanel(tag);
        }
    };

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
        input = (EditText) findViewById(R.id.mainInput);
        final LinearLayout container = (LinearLayout) findViewById(R.id.mainBottomPanel);

        panelManager = new PanelManager(container);
        panelManager.addPanel(getString(R.string.app_emoticon), new EmoticonListPanel(chatManager));
        panelManager.addPanel(getString(R.string.app_music), new MusicPanel());
        panelManager.addPanel(getString(R.string.app_voice), new VoicePanel());

        findViewById(R.id.mainSendVoice).setOnClickListener(panelActionListener);
        findViewById(R.id.mainSendEmoticon).setOnClickListener(panelActionListener);
        findViewById(R.id.mainShareMusic).setOnClickListener(panelActionListener);

        findViewById(R.id.mainSendPhoto).setOnClickListener(view -> {
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera, 20020823);
        });

        input.setOnClickListener(view -> panelManager.dismiss());

        ImeKit.listenImeEvent(findViewById(R.id.mainRootLayout), open -> {
            int h = ImeKit.getImeHeight();
            if (h != 0) {
                ViewGroup.LayoutParams params = container.getLayoutParams();
                params.height = h;
                container.setLayoutParams(params);
            }
        });

        findViewById(R.id.mainSend).setOnClickListener(p1 -> {
            String text = input.getText().toString();
            if (text.isEmpty()) {
                return;
            }

            input.setText(null);

            if (text.startsWith("@")) {
                String code = text.substring(1).trim();
                chatManager.sendCodeMessage(code);
            } else {
                chatManager.sendTextMessage(text);
            }

            msgList.smoothScrollToPosition(msgList.getCount() - 1);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20020823 && resultCode == Activity.RESULT_OK && null != data) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            chatManager.sendImageMessage(bitmap);
        }
    }

    @Override
    public void onBackPressed() {
        if (panelManager.isShowing()) {
            panelManager.dismiss();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatManager.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        panelManager.dismiss();
    }
}
