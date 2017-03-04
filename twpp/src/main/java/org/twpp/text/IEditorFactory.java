package org.twpp.text;

import android.content.Context;

public interface IEditorFactory {
    IEditor createEditor(Context context);
    Language getLanguage(String langName);
}
