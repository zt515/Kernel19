package io.kiva.kernel.adapter;
import android.view.View;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ImageView;
import io.kiva.kernel.R;
import io.kiva.kernel.model.IMessage;
import io.kiva.kernel.model.MessageFrom;

public class ViewHolder
{
    public View root;
    public LinearLayout content;
    public ImageView icon;
    
    public ViewHolder(View root, MessageFrom from) {
        this.root = root;
        
        content = (LinearLayout) root.findViewById(from == MessageFrom.FROM_ME ? R.id.bubble_content_me : R.id.bubble_content_other);
        icon = (ImageView) root.findViewById(from == MessageFrom.FROM_ME ? R.id.bubble_icon_me : R.id.bubble_icon_other);
    }
    
    
    public LinearLayout getContent()
    {
        return content;
    }

    public ImageView getIcon()
    {
        return icon;
    }
    
    public void onBind(IMessage message) {
        
    }
    
    public View getRootView() {
        return root;
    }
}
