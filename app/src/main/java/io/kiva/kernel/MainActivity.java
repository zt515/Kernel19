package io.kiva.kernel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.twpp.text.IEditor;

import java.io.IOException;

import cc.kernel19.wetalk.WetalkClient;
import io.kiva.kernel.adapter.MessageAdapter;
import io.kiva.kernel.ai.AIKernel19;
import io.kiva.kernel.chat.ChatManager;
import io.kiva.kernel.chat.History;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.panel.EmoticonListPanel;
import io.kiva.kernel.panel.MusicPanel;
import io.kiva.kernel.panel.PanelManager;
import io.kiva.kernel.panel.VoicePanel;
import io.kiva.kernel.user.User;
import io.kiva.kernel.utils.EditorKit;
import io.kiva.kernel.utils.ImeKit;
import io.kiva.kernel.utils.UIKit;
import io.kiva.kernel.wetalk.WetalkDataConverter;
import io.kiva.kernel.wetalk.WetalkUser;

public class MainActivity extends Activity {
    private ListView msgList;
    private ChatManager chatManager;
    private PanelManager panelManager;
    private EditText input;

    private User chatUser;
    private String initCode;
    private WetalkClient wetalkClient;
    private SharedPreferences sharedPreferences;

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

        initOnlineChat();
        initMessageList();
        initWidget();
    }

    private void initOnlineChat() {
        WetalkUser user = new WetalkUser(this, (message -> {
            if (wetalkClient != null) {
                wetalkClient.sendText(WetalkDataConverter.convertToText(message));
            }
        }));
        initTitleText(user);

        this.chatUser = user;
        this.wetalkClient = new WetalkClient(2);
        this.chatManager = new ChatManager(History.loadHistory());
        this.chatManager.setChatUser(user);

        new Thread(() -> {
            try {
                wetalkClient.connect();
                wetalkClient.start(data
                        -> UIKit.get().post(()
                        -> user.getReplyListener()
                        .onNewReply(WetalkDataConverter.convertToMessage(MessageFrom.FROM_OTHER, data))));
            } catch (Throwable e) {
                e.printStackTrace(System.err);
            }
        }).start();

    }

    private void initOfflineChat() {
        AIKernel19 kernel19 = new AIKernel19(this);
        this.chatUser = kernel19;

        initTitleText(kernel19);

        chatManager = new ChatManager(History.loadHistory());
        chatManager.setChatUser(kernel19);

        sharedPreferences = getSharedPreferences("ai", MODE_PRIVATE);
        initCode = sharedPreferences.getString("initCode", AIKernel19.DEFAULT_INIT_CODE);
        kernel19.setInitCode(initCode);
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
        findViewById(R.id.mainSettings).setOnClickListener(view -> {
            IEditor editor = EditorKit.createEditor(MainActivity.this);
            editor.setText(initCode);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("编辑运行环境")
                    .setView(editor.getEditView())
                    .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                        initCode = editor.getText();
                        sharedPreferences.edit().putString("initCode", initCode).apply();
                        if (chatUser instanceof AIKernel19) {
                            ((AIKernel19) chatUser).setInitCode(initCode);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });
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

    public void initTitleText(io.kiva.kernel.user.User user) {
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
        if (wetalkClient != null) {
            wetalkClient.close();
        }
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
