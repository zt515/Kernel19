package org.twpp.text.impl.editor.highlight.language.bash;

import org.twpp.text.Language;
import org.twpp.text.impl.editor.highlight.language.TokenConverter;
import org.twpp.text.lexer.Flag;
import org.twpp.text.lexer.Span;

import java.io.StringReader;
import java.util.List;

public class BashLang extends Language {

    static BashLang DEFAULT = new BashLang();

    public static BashLang getDefault(){
        return DEFAULT;
    }

    @Override
    public List<Span> tokenize(Flag flag, String needToLex) {
        return TokenConverter.makeSpans(flag,new BashLexer(new StringReader(needToLex)));
    }
}
