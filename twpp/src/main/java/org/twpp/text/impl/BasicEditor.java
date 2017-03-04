package org.twpp.text.impl;

import android.graphics.Typeface;
import android.view.View;

import org.twpp.text.IEditor;
import org.twpp.text.Language;
import org.twpp.text.impl.editor.ui.internal.EditWidget;
import org.twpp.text.listener.OnAutoCompletionListener;
import org.twpp.text.listener.OnEditActionListener;
import org.twpp.text.skin.Skin;

public class BasicEditor implements IEditor {

    private EditWidget editWidget;

    public BasicEditor(EditWidget editWidget) {
        this.editWidget = editWidget;
    }

    @Override
    public void setText(String text) {
        editWidget.setText(text);
    }

    @Override
    public void undo() {
        editWidget.undo();
    }

    @Override
    public void redo() {
        editWidget.redo();
    }

    @Override
    public boolean canUndo() {
        return editWidget.canUndo();
    }

    @Override
    public boolean canRedo() {
        return editWidget.canRedo();
    }

    @Override
    public void moveCursor(int pos) {
        editWidget.moveCursor(pos);
    }

    @Override
    public void moveCursorUp() {
        editWidget.moveCursorUp();
    }

    @Override
    public void moveCursorDown() {
        editWidget.moveCursorDown();
    }

    @Override
    public void moveCursorLeft() {
        editWidget.moveCursorLeft();
    }

    @Override
    public void moveCursorRight() {
        editWidget.moveCursorRight();
    }

    @Override
    public int getSelectionStart() {
        return editWidget.getSelectionStart();
    }

    @Override
    public int getSelectionEnd() {
        return editWidget.getSelectionEnd();
    }

    @Override
    public int getCursorPosition() {
        return editWidget.getCursorPosition();
    }

    @Override
    public void setSelection(int start, int end) {
        editWidget.setSelectionRange(start, end);
    }

    @Override
    public void selectAll() {
        editWidget.selectAll();
    }

    @Override
    public boolean isSelectText() {
        return editWidget.isSelectText();
    }

    @Override
    public void setSelectionMode() {
        editWidget.setToEditMode();
    }

    @Override
    public void setUnSelectionMode() {
        editWidget.setToViewMode();
    }

    @Override
    public void append(String appendText) {
        editWidget.append(appendText);
    }

    @Override
    public void setEditable(boolean editable) {
        if (editable) {
            setSelectionMode();
        }else {
            setUnSelectionMode();
        }
    }

    @Override
    public String getText() {
        return editWidget.getText();
    }

    @Override
    public String subString(int start, int end) {
        return new String(editWidget.getDoc().subSequence(start, end));
    }

    @Override
    public void insert(int pos, String content) {
        editWidget.insert(pos,content);
    }

    @Override
    public void delete(int pos, int num) {
        editWidget.delete(pos,num);
    }

    @Override
    public void replace(int start, int end, String replace) {
        int delta = end - start;
        editWidget.delete(start, delta);
        editWidget.insert(start - delta, replace);
    }

    @Override
    public View getEditView() {
        return editWidget;
    }

    @Override
    public void setOnAutoCompletionListener(OnAutoCompletionListener onAutoCompletionListener) {
        editWidget.setOnAutoCompletionListener(onAutoCompletionListener);
    }

    @Override
    public void setOnEditActionListener(OnEditActionListener onEditActionListener) {
        editWidget.setOnEditActionListener(onEditActionListener);
    }

    @Override
    public OnAutoCompletionListener getOnAutoCompletionListener() {
        return editWidget.getOnAutoCompletionListener();
    }

    @Override
    public OnEditActionListener getOnEditActionListener() {
        return editWidget.getOnEditActionListener();
    }

    @Override
    public int getLength() {
        return editWidget.length();
    }

    @Override
    public int getCursorLine() {
        return editWidget.getCursorLine();
    }

    @Override
    public int getLineHeight() {
        return editWidget.lineHeight();
    }

    @Override
    public void setLanguage(Language language) {
        editWidget.setLanguage(language);
    }

    @Override
    public int coordToCharIndex(int x, int y) {
        return editWidget.coordToCharIndex(x, y);
    }

    @Override
    public int coordToCharIndexStrict(int x, int y) {
        return editWidget.coordToCharIndexStrict(x, y);
    }

    @Override
    public void refreshHighlight() {
        editWidget.refreshSpans();
        editWidget.updateLeftPadding();
        editWidget.invalidate();
    }

    @Override
    public void setTextSize(float textSize) {
        editWidget.setTextSize(textSize);
    }

    @Override
    public float getTextSize() {
        return editWidget.getTextSize();
    }

    @Override
    public void setTabSpaces(int space) {
        editWidget.setTabSpaces(space);
    }

    @Override
    public void setSkin(Skin skin) {
        editWidget.setSkin(skin);
    }

    @Override
    public Skin getSkin() {
        return editWidget.getSkin();
    }

    @Override
    public void setTypeface(Typeface typeface) {
        editWidget.setTypeface(typeface);
    }
}
