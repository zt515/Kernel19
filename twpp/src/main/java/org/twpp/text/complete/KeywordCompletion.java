package org.twpp.text.complete;

public class KeywordCompletion extends TextCompletion {

    public KeywordCompletion(String title) {
        this(title, "Keyword");
    }

    public KeywordCompletion(String title, String description) {
        super(title, description, title, CompleteType.KEYWORD);
    }
}
