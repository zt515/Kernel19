package org.twpp.text.impl;

import android.content.Context;

import org.twpp.text.IEditor;
import org.twpp.text.IEditorFactory;
import org.twpp.text.Language;
import org.twpp.text.impl.editor.highlight.language.bash.BashLang;
import org.twpp.text.impl.editor.highlight.language.c.CLang;
import org.twpp.text.impl.editor.highlight.language.cpp.CppLang;
import org.twpp.text.impl.editor.highlight.language.java.JavaLang;
import org.twpp.text.impl.editor.highlight.language.js.JavaScriptLang;
import org.twpp.text.impl.editor.highlight.language.xml.XmlLang;
import org.twpp.text.impl.editor.ui.internal.EditWidget;

public class EditorFactory implements IEditorFactory {
    @Override
    public IEditor createEditor(Context context) {
        return new BasicEditor(new EditWidget(context));
    }

    @Override
    public Language getLanguage(String langName) {
        if (langName != null) {
            if ("java".equals(langName)) {
                return JavaLang.getDefault();
            }
            if ("cpp".equals(langName)) {
                return CppLang.getDefault();
            }
            if ("js".equals(langName)) {
                return JavaScriptLang.getDefault();
            }
            if ("c".equals(langName)) {
                return CLang.getDefault();
            }
            if ("sh".equals(langName)) {
                return BashLang.getDefault();
            }
            if ("xml".equals(langName)) {
                return XmlLang.getDefault();
            }
        }
        return null;
    }
}
