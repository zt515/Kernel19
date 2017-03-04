package org.twpp.text.impl.editor.highlight.language.xml;

import org.twpp.text.Language;
import org.twpp.text.impl.editor.highlight.language.TokenConverter;
import org.twpp.text.lexer.Flag;
import org.twpp.text.lexer.Span;

import java.io.StringReader;
import java.util.List;

public class XmlLang extends Language {
    static XmlLang SINGLE = new XmlLang();

    public static XmlLang getDefault(){
        return SINGLE;
    }

    @Override
    public List<Span> tokenize(Flag flag, String needToLex) {
        return TokenConverter.makeSpans(flag,new XmlLexer(new StringReader(needToLex)));
    }
}
