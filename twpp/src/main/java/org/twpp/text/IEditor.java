package org.twpp.text;

import android.graphics.Typeface;
import android.view.View;

import org.twpp.text.listener.OnAutoCompletionListener;
import org.twpp.text.listener.OnEditActionListener;
import org.twpp.text.skin.Skin;

public interface IEditor {
    /**
     * 设置编辑器文本内容
     * @param text 文本内容
     */
    void setText(String text);

    void undo();

    void redo();

    boolean canUndo();

    boolean canRedo();

    void moveCursor(int pos);

    void moveCursorUp();

    void moveCursorDown();

    void moveCursorLeft();

    void moveCursorRight();

    int getSelectionStart();

    int getSelectionEnd();

    int getCursorPosition();

    void setSelection(int start, int end);

    void selectAll();

    boolean isSelectText();

    void setSelectionMode();

    void setUnSelectionMode();

    void append(String appendText);

    void setEditable(boolean editable);

    String getText();

    String subString(int start, int end);

    void insert(int pos, String content);

    void delete(int pos, int num);

    void replace(int start, int end, String replace);

    View getEditView();

    void setOnAutoCompletionListener(OnAutoCompletionListener onAutoCompletionListener);

    void setOnEditActionListener(OnEditActionListener onEditActionListener);

    OnAutoCompletionListener getOnAutoCompletionListener();

    OnEditActionListener getOnEditActionListener();

    int getLength();

    int getCursorLine();

    int getLineHeight();

    void setLanguage(Language language);

    int coordToCharIndex(int x, int y);

    int coordToCharIndexStrict(int x, int y);

    void refreshHighlight();

    void setTextSize(float textSize);

    float getTextSize();

    void setTabSpaces(int space);

    void setSkin(Skin skin);

    Skin getSkin();

    void setTypeface(Typeface typeface);

    void setHighlightCurrentLine(boolean is);

    void switchToEditMode();

    void switchToViewMode();
}
