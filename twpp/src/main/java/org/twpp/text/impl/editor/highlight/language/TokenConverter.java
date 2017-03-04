package org.twpp.text.impl.editor.highlight.language;

import org.twpp.text.lexer.Flag;
import org.twpp.text.lexer.Span;
import org.twpp.text.lexer.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TokenConverter {

    /**
     * @param lexer 词法分析器
     * @return 词法分析结果
     */
    public static List<Span> makeSpans(Flag flag, DefaultJFlexLexer lexer){
        List<Span> spans = new ArrayList<Span>(50);
        Token token;
        try {
            while (((token = lexer.yylex()) != null) && !flag.isSet()) {
                switch (token.type) {
                    case KEYWORD2:
                    case KEYWORD:
                        spans.add(new Span(token.start, org.twpp.text.lexer.TokenType.KEYWORD));
                        break;
                    case OPERATOR:
                        spans.add(new Span(token.start, TokenType.KEYWORD));
                        break;
                    case STRING2:
                    case STRING:
                        spans.add(new Span(token.start, TokenType.SYMBOL));
                        break;
                    case NUMBER:
                        spans.add(new Span(token.start, TokenType.SYMBOL));
                        break;
                    case TYPE3:
                    case TYPE2:
                    case TYPE:
                        spans.add(new Span(token.start, TokenType.TYPE));
                        break;
                    case IDENTIFIER:
                        spans.add(new Span(token.start, TokenType.NORMAL));
                        break;
                    case COMMENT2:
                    case COMMENT:
                        spans.add(new Span(token.start, TokenType.COMMENT));
                        break;
                    default:
                        spans.add(new Span(token.start, TokenType.NORMAL));
                        break;
                }

            }
        } catch (Throwable ignored) {}

        try {
            lexer.yyclose();
        } catch (IOException ignored) {}

        if (spans.size() == 0) {
            spans.add(Span.FIRST_TOKEN);
        }
        return spans;

    }



}
