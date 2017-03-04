package org.twpp.text.complete;

import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 * 因为图标资源不在接口里面,所以这里写一个接口隐藏具体实现.
 */
public interface IDefaultCompleteApply {

    void applyView(AutoCompletion.CompleteType type, TextView title, TextView desc, ImageView imageView);
}
