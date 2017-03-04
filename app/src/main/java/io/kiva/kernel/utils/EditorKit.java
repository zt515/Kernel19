package io.kiva.kernel.utils;

import android.content.Context;
import android.graphics.Typeface;

import org.twpp.text.IEditor;
import org.twpp.text.impl.EditorFactory;
import org.twpp.text.impl.editor.highlight.language.java.JavaLang;
import org.twpp.text.skin.LightSkin;

/**
 * @author kiva
 */

public class EditorKit {
    public static IEditor createEditor(Context context) {
        IEditor editor = new EditorFactory().createEditor(context);
        editor.setSkin(LightSkin.getInstance());
        editor.setTabSpaces(4);
        editor.setLanguage(JavaLang.getDefault());
        editor.setTextSize(40f);
        editor.setHighlightCurrentLine(true);
        editor.setTypeface(Typeface.MONOSPACE);
        return editor;
    }
}
