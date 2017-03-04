package org.twpp.text.lexer;

public final class Span {
    private int offset;
    private TokenType tokenType;
    private int color;

    public static final Span FIRST_TOKEN = new Span(0,TokenType.NORMAL);

    public Span(int offset, TokenType color) {
        this.offset = offset;
        tokenType = color;
    }

    public Span(int _offset, int color) {
        this.offset = _offset;
        this.color = color;
        this.tokenType = TokenType.NOT_USE;
    }

    public final int getOffset() {
        return offset;
    }

    public final void setOffset(int value) {
        offset = value;
    }

    public final TokenType getTokenType() {
        return tokenType;
    }

    public final void setTokenType(TokenType value) {
        tokenType = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
