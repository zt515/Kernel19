package org.twpp.text;

import org.twpp.text.adapter.TokenizeAdapter;
import org.twpp.text.lexer.Flag;
import org.twpp.text.lexer.Span;

import java.util.List;

public class Language implements TokenizeAdapter {

    public final static char EOF = '\uFFFF';
    public final static char NULL_CHAR = '\u0000';
    public final static char NEWLINE = '\n';
    public final static char BACKSPACE = '\b';
    public final static char TAB = '\t';

    @Override
    public List<Span> tokenize(Flag flag, String needToLex) {
        return DEFAULT_SPANS;
    }

    public boolean isWhitespace(char c) {
        return (c == ' ' || c == '\n' || c == '\t' ||
                c == '\r' || c == '\f' || c == EOF);
    }

    public boolean isSentenceTerminator(char c) {
        return (c == '.');
    }

    public String getLanguageName(){
        return "Unknown";
    }
}
