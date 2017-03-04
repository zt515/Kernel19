package org.twpp.text.impl.editor.highlight.language.js;

import org.twpp.text.Language;
import org.twpp.text.impl.editor.highlight.language.TokenConverter;
import org.twpp.text.lexer.Flag;
import org.twpp.text.lexer.Span;

import java.io.StringReader;
import java.util.List;

public class JavaScriptLang extends Language {
    static JavaScriptLang DEFAULT = new JavaScriptLang();

    public static JavaScriptLang getDefault(){
        return DEFAULT;
    }

    @Override
    public List<Span> tokenize(Flag flag, String needToLex) {
        return TokenConverter.makeSpans(flag,new JavaScriptLexer(new StringReader(needToLex)));
    }
}
