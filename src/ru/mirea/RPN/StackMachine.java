package ru.mirea.RPN;


import ru.mirea.customHashSet.cHashSet;
import ru.mirea.lexerParser.utility.Token;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class StackMachine {
    private Stack<Token> stack = new Stack<>();
    private HashMap<String, Integer> varName = new HashMap<>();
    private HashSet<String> setName = new HashSet<>();
    private HashMap<String, cHashSet> hashMap = new HashMap<>();

    private String getOperand() {
        if (stack.peek().getType().equals("VAR"))
            return String.valueOf(varName.get(stack.pop().getValue()));
        else
            return stack.pop().getValue();
    }

    public void calculate(List<Token> tokens) {
        System.out.println("VAR and SET table:");
        stackCalculate(tokens);

        System.out.println();

        System.out.println("Final VAR table:");
        System.out.println(varName);

        System.out.println();

        System.out.println("Final SET table:");
        System.out.println(hashMap);
    }

    private void stackCalculate(List<Token> tokens) {

        label:
        for (int i = 0; i < tokens.size(); i++) {
            switch (tokens.get(i).getType()) {
                case "NUM":
                case "SET":
                    stack.push(tokens.get(i));
                    break;
                case "VAR":
                    String varValue = tokens.get(i).getValue();
                    for (int var_i = i; var_i < tokens.size(); var_i++) {
                        if (tokens.get(var_i).getType().equals("NEWASSIGN")) {
                            if (varName.containsKey(varValue))
                                stack.push(tokens.get(i));
                            else {
                                varName.put(varValue, 0);
                                stack.push(tokens.get(i));
                            }
                            break;
                        } else if (tokens.get(var_i).getType().equals("TYPE")) {
                            if (setName.contains(varValue))
                                stack.push(tokens.get(i));
                            else {
                                setName.add(varValue);
                                stack.push(tokens.get(i));
                            }
                            break;
                        } else if (setName.contains(varValue)) {
                            stack.push(tokens.get(i));
                            break;
                        }
                    }
                    break;
                case "LOGOP":
                    String logOpValue = tokens.get(i).getValue();
                    boolean condition = logOp_rpn(getOperand(), getOperand(), logOpValue);
                    for (Token tr_token: tokens) {
                        if (tr_token.getType().equals("!F")) {
                            tr_token.setValue(String.valueOf(condition));
                            break;
                        }
                    }
                    if (!condition)
                        break label;
                    break;
                case "LPAR":
                case "RPAR":
                case "MULDIV":
                case "ADDSUB":
                    String opValue = tokens.get(i).getValue();
                    op_rpn(getOperand(), getOperand(), opValue);
                    break;
                case "NEWASSIGN":
                    int value = Integer.parseInt(stack.pop().getValue());
                    String var = stack.pop().getValue();
                    varName.put(var, value);
                    System.out.println(varName);
                    break;
                case "TYPE":
                    String type = tokens.get(i).getValue();
                    String set = stack.peek().getValue();
                    stack.pop();
                    String hashSetNewKey = stack.peek().getValue();
                    stack.pop();
                    hashMap.put(hashSetNewKey, new cHashSet());
                    System.out.println("\"" + hashSetNewKey + " " + type + " " + set + "\" -> "
                            + hashSetNewKey + "=" + hashMap.get(hashSetNewKey));
                    break;
                case "ADD":
                case "REMOVE":
                case "CONTAINS":
                    String hashSetOp = tokens.get(i).getValue();
                    String hashSetValue = stack.pop().getValue();
                    String hashSetKey = stack.pop().getValue();
                    cHashSet hashSet = hashMap.get(hashSetKey);
                    set_rpn(hashSetValue, hashSetKey, hashSetOp, hashSet);
                    break;
                case "EOL":
                    break;
                case "END":
                    int cycle_begin = 0;
                    int cycle_end = 0;
                    boolean conditionF = false;
                    for (Token tr_token: tokens) {
                        if (tr_token.getType().equals("p"))
                            cycle_end = Integer.parseInt(tr_token.getValue());
                        if (tr_token.getType().equals("!F")) {
                            cycle_begin = tokens.indexOf(tr_token);
                            conditionF = Boolean.getBoolean(tr_token.getValue());
                            break;
                        }
                    }
                    if (conditionF)
                        stackCalculate(tokens.subList(cycle_begin, cycle_end));
                    else
                        break;
            }
        }
    }

    private void set_rpn(String b, String a, String value, cHashSet hashSet) {
        switch (value) {
            case "add":
                hashSet.add(b);
                System.out.println("\"" + a + " " + value + " " + b + "\" -> " +
                        a + "=" + hashMap.get(a));
                break;
            case "remove":
                hashSet.remove(b);
                System.out.println("\"" + a + " " + value + " " + b + "\" -> " +
                        a + "=" + hashMap.get(a));
                break;
            case "contains":
                System.out.println("\"" + a + " " + value + " " + b + "\" -> " +
                        hashSet.contains(b));
                break;
        }
    }

    private boolean logOp_rpn(String b, String a, String value) {
        int arg1 = Integer.parseInt(a);
        int arg2 = Integer.parseInt(b);

        boolean log_result = false;
        switch (value) {
            case "==":
                log_result = arg1 == arg2;
                break;
            case "!=":
                log_result = arg1 != arg2;
                break;
            case "<":
                log_result = arg1 < arg2;
                break;
            case ">":
                log_result = arg1 > arg2;
                break;
            case "<=":
                log_result = arg1 <= arg2;
                break;
            case ">=":
                log_result = arg1 >= arg2;
                break;
        }
        return log_result;
    }

    private void op_rpn(String b, String a, String value) {
        int arg1 = Integer.parseInt(a);
        int arg2 = Integer.parseInt(b);
        int result;
        switch (value) {
            case "+":
                result = arg1 + arg2;
                stack.push(new Token("RESULT", String.valueOf(result)));
                break;
            case "-":
                result = arg1 - arg2;
                stack.push(new Token("RESULT", String.valueOf(result)));
                break;
            case "/":
                result = arg1 / arg2;
                stack.push(new Token("RESULT", String.valueOf(result)));
                break;
            case "*":
                result = arg1 * arg2;
                stack.push(new Token("RESULT", String.valueOf(result)));
                break;
            default:
                break;
        }
    }
}
