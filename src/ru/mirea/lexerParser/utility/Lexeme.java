package ru.mirea.lexerParser.utility;


import java.util.regex.Pattern;

public class Lexeme {

    private String type;
    private Pattern pattern;
    private int priority;

    public Lexeme(String type, Pattern pattern, int priority) {
        this.type = type;
        this.pattern = pattern;
        this.priority = priority;
    }

    public Lexeme(String type, Pattern pattern) {
        this.type = type;
        this.pattern = pattern;
    }

    public String getType() {
        return type;
    }

    public Pattern getPattern() {
        return pattern;
    }

    int getPriority() {
        return priority;
    }
}