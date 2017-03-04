package io.kiva.kernel.adapter;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.View;
import io.kiva.kernel.chat.MessageHolder;
import android.content.Context;
import io.kiva.kernel.model.MessageType;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.model.IMessage;

public class BaseMessageAdapter extends BaseAdapter
{
    private MessageHolder data;
    private Context context;

    public BaseMessageAdapter(Context context, MessageHolder data)
    {
        this.data = data;
        this.context = context;
    }

    public BaseMessageAdapter(Context context)
    {
        this.context = context;
    }

    public void setData(MessageHolder data)
    {
        this.data = data;
    }

    public MessageHolder getData()
    {
        return data;
    }
    
    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public Object getItem(int p1)
    {
        return data.get(p1);
    }

    @Override
    public long getItemId(int p1)
    {
        return p1;
    }

    @Override
    public View getView(int p1, View p2, ViewGroup p3)
    {
        /*
        ViewHolder vh = null;
        if (p2 == null) {
            vh = onCreateViewHolder(context, getMessageType(p1), getMessageFrom(p1));
            
        } else {
            vh = (ViewHolder) p2.getTag();
        }
        */
        ViewHolder vh = onCreateViewHolder(context, getMessageType(p1), getMessageFrom(p1));
        
        if (vh != null && (p2 = vh.getRootView()) != null) {
            p2.setTag(vh);
            onBindViewHolder(p1, vh);
        }
        
        return p2;
    }
    
    protected MessageFrom getMessageFrom(int position) {
        return null;
    }
    
    protected MessageType getMessageType(int position) {
        return null;
    }
    
    protected ViewHolder onCreateViewHolder(Context context, MessageType type, MessageFrom from) {
        return null;
    }

    private void onBindViewHolder(int position, ViewHolder vh) {
        IMessage msg = (IMessage) getItem(position);
        vh.onBind(msg);
    }
}
