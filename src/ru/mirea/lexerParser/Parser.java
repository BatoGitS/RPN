package ru.mirea.lexerParser;

import ru.mirea.lexerParser.utility.Token;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Parser {

    private Queue<Token> tokens = new LinkedList<>();
    private HashSet<String> varSet = new HashSet<>();
    private HashSet<String> set = new HashSet<>();

    private Token next() {
        assert tokens.peek() != null;
        return tokens.peek();
    }

    private boolean next(String type) {
        assert tokens.peek() != null;
        return tokens.peek().getType().equals(type);
    }

    public String parse(List<Token> tokens) {
        this.tokens = new LinkedList<>(tokens);
        lang();
        return "Syntax is OK";
    }

    private void lang() {
        while (next() != null)
            expr();
    }

    private void expr() {
        if (next("VAR")) {
            String value = next().getValue();
            tokens.poll();
            set_expr(value);
        } else if (next("INIT")) {
            tokens.poll();
            if (!set.contains(next().getValue())) {
                if (!varSet.contains(next().getValue()))
                    assign_expr();
                else
                    throw new IllegalArgumentException("Error. Variable '" +
                            next().getValue() + "' is already defined for a primitive type");
            } else
                throw new IllegalArgumentException("Error. Variable '" +
                        next().getValue() + "' is already defined for a set.");
        } else if (next("WHILE")) {
            tokens.poll();
            while_expr();
        } else
            throw new IllegalArgumentException("Error. Expected VAR, INIT or WHILE, but " +
                    next().getType() + " was found.");
    }

    private void set_expr(String value) {
        if (next("TYPE")) {
            tokens.poll();
            if (next("SET")) {
                if (!set.contains(value)) {
                    if (!varSet.contains(value)) {
                        set.add(value);
                        tokens.poll();
                        if (next("EOL")) {
                            tokens.poll();
                        } else
                            throw new IllegalArgumentException("Error. Expected EOL, but " +
                                    next().getType() + " was found.");
                    } else
                        throw new IllegalArgumentException("Error. Variable '" +
                                value + "' is already defined for a primitive type.");
                } else throw new IllegalArgumentException("Error. Variable '" +
                        value + "' is already defined for a set.");
            } else
                throw new IllegalArgumentException("Error. Expected SET, but " +
                        next().getType() + " was found.");

        } else if (set.contains(value) && (next("ADD") || next("REMOVE") || next("CONTAINS"))) {
            tokens.poll();
            value();
            if (next("EOL"))
                tokens.poll();
            else
                throw new IllegalArgumentException("Error. Expected EOL, but " +
                        next().getType() + " was found.");
        } else
                throw new IllegalArgumentException("Error. Set '" +
                        value + "' was not initialized.");
    }

    private void while_expr() {
            condition();
            body();
    }

    private void condition() {
        if (next("LPAR")) {
            tokens.poll();
            log_expr();
            if (next("RPAR")) {
                tokens.poll();
            } else
                throw new IllegalArgumentException("Error. Expected RPAR, but " +
                        next().getType() + " was found.");
        } else
            throw new IllegalArgumentException("Error. Expected LPAR, but " +
                    next().getType() + " was found.");
    }

    private void log_expr() {
        value();
        if (next("LOGOP")) {
            tokens.poll();
            value();
        } else
            throw new IllegalArgumentException("Error. Expected LOGOP, but " +
                    next().getType() + " was found.");
    }

    private void body() {
        if (next("LBRACE")) {
            tokens.poll();
            body_expr();
            if (next("RBRACE")) {
                tokens.poll();
            } else
                throw new IllegalArgumentException("Error. Expected RBRACE, but " +
                        next().getType() + " was found.");
        } else
            throw new IllegalArgumentException("Error. Expected LBRACE, but " +
                    next().getType() + " was found.");
    }

    private void body_expr() {
        while (!next("RBRACE")) {
            if (!set.contains(next().getValue())) {
                if (varSet.contains(next().getValue()))
                    assign_expr();
                else
                    throw new IllegalArgumentException("Error. Variable '" +
                            next().getValue() + "' was not initialized.");
            } else
                throw new IllegalArgumentException("Error. Variable '" +
                        next().getValue() + "' is defined for a set.");
        }
    }

    private void assign_expr() {
        if (next("VAR")) {
            varSet.add(next().getValue());
            tokens.poll();
            if (next("ASSIGN")) {
                tokens.poll();
                value_expr();
            } else
                throw new IllegalArgumentException("Error. Expected ASSIGN, but " +
                        next().getType() + " was found.");
        } else
            throw new IllegalArgumentException("Error. Expected VAR, but " +
                    next().getType() + " was found.");
    }

    private void value_expr() {
        value();
        while (!next("EOL")) {
            if (next("ADDSUB") || next("MULDIV")) {
                tokens.poll();
                value();
            } else
                throw new IllegalArgumentException("Error. Expected ADDSUB or MULDIV, but " +
                        next().getType() + " was found.");
        }
        tokens.poll();
    }

    private void value() {
        if (next("VAR") || next("NUM"))
            tokens.poll();
        else
            throw new IllegalArgumentException("Error. Expected VAR or NUM, but " +
                    next().getType() + " was found.");
    }
}