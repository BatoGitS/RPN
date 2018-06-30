package ru.mirea.RPN;

import ru.mirea.lexerParser.utility.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RPN {
    private Stack<Token> operators = new Stack<>();

    public List<Token> toRPN(List<Token> tokens) {
        List<Token> output = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++) {
            switch (tokens.get(i).getType()) {
                case "INIT":
                    i = rpn_assign_expr(i, tokens, output);
                    break;
                case "VAR":
                    output.add(tokens.get(i));
                    label1:
                    for (int set_i = i + 1; set_i < tokens.size(); set_i++) {
                        switch (tokens.get(set_i).getType()) {
                            case "TYPE":
                                operators.push(tokens.get(set_i));
                                break;
                            case "VAR":
                            case "NUM":
                            case "SET":
                                output.add(tokens.get(set_i));
                                break;
                            case "ADD":
                            case "REMOVE":
                            case "CONTAINS":
                                operators.push(tokens.get(set_i));
                                break;
                            case "EOL":
                                while (!operators.empty())
                                    output.add(operators.pop());
                                output.add(new Token("EOL", "EOL", 0));
                                i = set_i;
                                break label1;
                        }
                    }
                    break;
                case "WHILE":
                    for (int condition_i = i + 1; condition_i < tokens.size(); condition_i++) {
                        if (tokens.get(condition_i).getType().equals("VAR") ||
                                tokens.get(condition_i).getType().equals("NUM") ||
                                tokens.get(condition_i).getType().equals("LPAR")) {
                            switch (tokens.get(condition_i).getType()) {
                                case "VAR":
                                case "NUM":
                                    output.add(tokens.get(condition_i));
                                    break;
                                case "LPAR":
                                    operators.push(tokens.get(condition_i));
                                    break;
                            }
                        } else if (tokens.get(condition_i).getType().equals("RPAR")) {
                            while (!operators.peek().getType().equals("LPAR"))
                                output.add(operators.pop());
                            output.add(new Token("p", "p1"));
                            output.add(new Token("!F", "!F"));
                            operators.pop();
                            i = condition_i;
                            break;
                        } else if (!operators.empty()) {
                            while (tokens.get(condition_i).getPriority() <= operators.peek().getPriority())
                                output.add(operators.pop());
                            operators.push(tokens.get(condition_i));
                        } else if (tokens.size() - 1 == tokens.indexOf(tokens.get(condition_i))) {
                            while (!operators.empty())
                                output.add(operators.pop());
                        } else
                            operators.push(tokens.get(condition_i));
                    }
                    break;
                case "LBRACE":
                    for (int sub_br = i + 1; sub_br < tokens.size(); sub_br++) {
                        if (tokens.get(sub_br).getType().equals("RBRACE")) {
                            output.add(new Token("END", "END"));
                            int endIndex = 0;
                            for (Token end : output) {
                                if (end.getType().equals("END")) {
                                    endIndex = output.indexOf(end);
                                    break;
                                }
                            }
                            for (Token end : output) {
                                if (end.getType().equals("p")) {
                                    end.setValue(String.valueOf(endIndex));
                                    break;
                                }
                            }
                            i = sub_br;
                            break;
                        } else {
                            sub_br = rpn_assign_expr(sub_br, tokens, output);
                            i = sub_br;
                        }
                    }
                    break;
            }
        }
        return output;
    }

    private int rpn_assign_expr(int index, List<Token> tokens, List<Token> output) {
        int subEnd = 0;
        for (int sub_i = index; sub_i < tokens.size(); sub_i++) {
            if (tokens.get(sub_i).getType().equals("EOL")) {
                subEnd = tokens.indexOf(tokens.get(sub_i)) + 1;
                break;
            }
        }
        List<Token> subList = tokens.subList(index, subEnd);
        for (Token token : subList) {
            switch (token.getType()) {
                case "VAR":
                case "NUM":
                    output.add(token);
                    break;
                case "LPAR":
                case "RPAR":
                case "MULDIV":
                case "ADDSUB":
                case "ASSIGN":
                    if (token.getType().equals("LPAR"))
                        operators.push(token);
                    else if (token.getType().equals("RPAR")) {
                        while (!operators.peek().getType().equals("LPAR"))
                            output.add(operators.pop());
                        operators.pop();
                    } else if (!operators.empty()) {
                        while (token.getPriority() <= operators.peek().getPriority())
                            output.add(operators.pop());
                        operators.push(token);
                    } else if (token.getType().equals("ASSIGN"))
                        operators.push(new Token("NEWASSIGN", ":=", 1));
                    else
                        operators.push(token);
                    break;
            }
            if (token.getType().equals("EOL")) {
                while (!operators.empty())
                    output.add(operators.pop());
                output.add(new Token("EOL", "EOL", 0));
                break;
            }
        }
        return subEnd - 1;
    }
}