package org.twpp.text.impl.editor.highlight.language.lua;

import org.twpp.text.Language;
import org.twpp.text.impl.editor.highlight.language.TokenConverter;
import org.twpp.text.lexer.Flag;
import org.twpp.text.lexer.Span;

import java.io.StringReader;
import java.util.List;

public class LuaLang extends Language {

    private static Language LUA = new LuaLang();


    public static Language getDefault() {
        return LUA;
    }


    @Override
    public List<Span> tokenize(Flag flag, String needToLex) {
        return TokenConverter.makeSpans(flag, new LuaLexer(new StringReader(needToLex)));
    }
}
