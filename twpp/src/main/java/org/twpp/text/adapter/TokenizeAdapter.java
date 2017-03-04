package org.twpp.text.adapter;


import org.twpp.text.lexer.Flag;
import org.twpp.text.lexer.Span;
import org.twpp.text.lexer.TokenType;

import java.util.Collections;
import java.util.List;

public interface TokenizeAdapter {


    static List<Span> DEFAULT_SPANS = Collections.singletonList(new Span(0, TokenType.NORMAL));

    /**
     * 词法分析
     * @param flag 是否需要终止解析
     * @param needToLex 需要词法分析的文本副本
     */
    List<Span> tokenize(Flag flag, String needToLex);

}
