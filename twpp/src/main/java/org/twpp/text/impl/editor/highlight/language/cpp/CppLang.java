package org.twpp.text.impl.editor.highlight.language.cpp;

import org.twpp.text.Language;
import org.twpp.text.impl.editor.highlight.language.TokenConverter;
import org.twpp.text.lexer.Flag;
import org.twpp.text.lexer.Span;

import java.io.StringReader;
import java.util.List;

public class CppLang extends Language {

    static CppLang SINGLE = new CppLang();

    public static CppLang getDefault(){
        return SINGLE;
    }

    @Override
    public List<Span> tokenize(Flag flag, String needToLex) {
        return TokenConverter.makeSpans(flag,new CppLexer(new StringReader(needToLex)));
    }
}
