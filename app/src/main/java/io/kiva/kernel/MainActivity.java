package io.kiva.kernel;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ListView;
import io.kiva.kernel.chat.MessageHolder;
import io.kiva.kernel.chat.History;
import io.kiva.kernel.adapter.MessageAdapter;
import io.kiva.kernel.chat.Messager;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity 
{
    private ListView msgList;
    private Messager messager;
    private MessageAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initTitleText();
        initMessageList();
        initWidget();
    }

    private void initWidget()
    {
        final EditText input = (EditText) findViewById(R.id.mainInput);
        
        findViewById(R.id.mainSend).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1)
                {
                    String text = input.getText().toString();
                    if (!text.isEmpty()) {
                        input.setText(null);
                        messager.sendTextMessage(text);
                        msgList.smoothScrollToPosition(msgList.getCount() - 1);
                    }
                }
        });
    }

    private void initMessageList() {
        messager = new Messager(this, History.loadHistory());
        adapter = new MessageAdapter(this);
        messager.attachViewAdapter(adapter);
        
        msgList = (ListView) findViewById(R.id.msgList);
        msgList.setAdapter(adapter);
    }
    
    public void initTitleText() {
        try {
            TextView tv = (TextView) findViewById(R.id.versionText);
            tv.setText("你好，我是十九，你的私人内核。");
            // String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            //tv.setText(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
