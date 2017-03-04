package org.twpp.text.impl.editor.highlight.language.c;

import org.twpp.text.Language;
import org.twpp.text.impl.editor.highlight.language.TokenConverter;
import org.twpp.text.lexer.Flag;
import org.twpp.text.lexer.Span;

import java.io.StringReader;
import java.util.List;

public class CLang extends Language {

    private static Language C = new CLang();


    public static Language getDefault() {
        return C;
    }
    @Override
    public List<Span> tokenize(Flag flag, String needToLex) {
        return TokenConverter.makeSpans(flag, new CLexer(new StringReader(needToLex)));
    }
}

