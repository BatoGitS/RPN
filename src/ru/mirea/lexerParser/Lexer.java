package ru.mirea.lexerParser;

import ru.mirea.lexerParser.utility.Lexeme;
import ru.mirea.lexerParser.utility.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private List<Lexeme> lexemes = new ArrayList<>();

    public Lexer() {

        lexemes.add(new Lexeme("TYPE", Pattern.compile("type?")));
        lexemes.add(new Lexeme("SET", Pattern.compile("set?")));
        lexemes.add(new Lexeme("ADD", Pattern.compile("add?")));
        lexemes.add(new Lexeme("REMOVE", Pattern.compile("remove?")));
        lexemes.add(new Lexeme("CONTAINS", Pattern.compile("contains?")));

        lexemes.add(new Lexeme("INIT", Pattern.compile("int?")));

        lexemes.add(new Lexeme("WHILE", Pattern.compile("while?")));
        lexemes.add(new Lexeme("LBRACE", Pattern.compile("\\{")));
        lexemes.add(new Lexeme("RBRACE", Pattern.compile("}")));
        lexemes.add(new Lexeme("EOL", Pattern.compile(";")));
        lexemes.add(new Lexeme("WS", Pattern.compile("[\t\f\r\n]")));

        lexemes.add(new Lexeme("VAR", Pattern.compile("[a-zA-Z]+")));
        lexemes.add(new Lexeme("NUM", Pattern.compile("0|([1-9][0-9]*)")));

        lexemes.add(new Lexeme("LPAR", Pattern.compile("\\(")));
        lexemes.add(new Lexeme("RPAR", Pattern.compile("\\)")));
        lexemes.add(new Lexeme("ASSIGN", Pattern.compile("="), 1));
        lexemes.add(new Lexeme("LOGOP", Pattern.compile("(==|<=|>=|<|>|!=)"), 2));
        lexemes.add(new Lexeme("ADDSUB", Pattern.compile("[+-]"), 3));
        lexemes.add(new Lexeme("MULDIV", Pattern.compile("[*/]"), 4));
    }

    private Pattern tokenPatterns(){
        StringBuilder tokenBuffer = new StringBuilder();
        for (Lexeme token : lexemes)
            tokenBuffer.append(String.format("|(?<%s>%s)", token.getType(), token.getPattern()));
        return Pattern.compile(tokenBuffer.substring(1));
    }

    public List<Token> process(String s) {
        List<Token> list = new ArrayList<>();
        
        Matcher matcher = tokenPatterns().matcher(s);
        while (matcher.find()) {
            if (matcher.group("WS") != null)
                continue;
            for (Lexeme lexeme : lexemes) {
                if (matcher.group(lexeme.getType()) != null)
                    list.add(new Token(lexeme, matcher.group(lexeme.getType())));
            }
        }
        return list;
    }
}
