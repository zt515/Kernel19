package org.twpp.text.listener;

public interface OnEditActionListener {
    void onPaste(String text);
    void onInsert(int pos);
    void onDelete(int pos, int nums);
    void onUpdateCursor();
}
