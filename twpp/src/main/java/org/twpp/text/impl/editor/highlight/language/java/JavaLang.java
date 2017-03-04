package org.twpp.text.impl.editor.highlight.language.java;


import org.twpp.text.Language;
import org.twpp.text.impl.editor.highlight.language.TokenConverter;
import org.twpp.text.lexer.Flag;
import org.twpp.text.lexer.Span;

import java.io.StringReader;
import java.util.List;

public class JavaLang extends Language {

    static JavaLang DEFAULT = new JavaLang();

    public static JavaLang getDefault(){
        return DEFAULT;
    }


    @Override
    public List<Span> tokenize(Flag flag, String needToLex) {
        return TokenConverter.makeSpans(flag,new JavaLexer(new StringReader(needToLex)));
    }
}
