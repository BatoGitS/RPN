package ru.mirea.lexerParser.utility;

public class Token {
    private String type;
    private String value;
    private int priority;

    public Token(Lexeme lexeme, String value) {
        this.type = lexeme.getType();
        this.value = value;
        this.priority = lexeme.getPriority();
    }

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }


    public Token(String type, String value, int priority) {
        this.type = type;
        this.value = value;
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getPriority() {
        return priority;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", priority=" + priority +
                '}';
    }
}
