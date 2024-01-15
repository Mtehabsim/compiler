package compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
    private Stack<String> stack = new Stack<>();
    private boolean interpreter;
//    E  -> TE'
//    E' -> +TE' | -TE' | none
//    T  -> FT'
//    T' -> *FT' | /FT' | %FT' | none
//    F  -> ++F | --F | +F | -F | P
//    P  -> id | (E)
    private List<String> errorMessages = new ArrayList<>();
    private int lineNumber;
    private String[][] table = {
    	    {"TA", "TA", "TA", "TA", "TA", null, null, null, "TA", "S", "S"}, // S is synch
    	    {null, "+TA", "-TA", null, null, null, null, null,null, "", ""},
    	    {"FB", "FB", "FB", "FB", "FB", null, null, null, "FB", "S", "S"},
    	    {null, "", "", null, null, "*FB", "/FB", "%FB",null, "", ""},
    	    {"P", "+F", "-F", "++F", "--F", "S", "S", "S", "P", "S", "S"},
    	    {"i", "S", "S", null, null, "S", "S", "S", "(E)", "S", "S"}
    	};
    	private String[] nonTerminals = {"E", "A", "T", "B", "F", "P"};
    	private String[] terminals = {"i", "+", "-", "++", "--", "*", "/", "%","(", ")", "$"};


    private String currentToken;
    private boolean endOfInputFlag = false;

    public Parser(boolean interpreter) {
    	this.interpreter = interpreter;
        stack.push("$");
        stack.push("E");
    }

    public void receiveToken(String token, int line) throws Exception {
        currentToken = token;
        lineNumber = line;
        processToken();
    }

    public void endOfInput() throws Exception {
        endOfInputFlag = true;
        processToken();
    }

    private void processToken() throws Exception {
        while (!stack.isEmpty() && (currentToken != null || endOfInputFlag)) {
            String top = stack.peek();
            if (isNonTerminal(top)) {
                String rule = getRule(top, currentToken);
                if (rule != null) {
                    stack.pop(); // Pop the non-terminal
                    pushRule(rule); // Push the rule onto the stack
                } else {
                	error("Not expected to recieve " + currentToken);
//                    System.out.println("No rule for non-terminal " + top + " with terminal " + currentToken);
                    currentToken = null;
                    continue;
                }
            } else if (isTerminal(top)) {
                if (top.equals(currentToken)) {
                    stack.pop(); // Matched terminal
                    currentToken = null; // Reset current token to receive the next one
                } else {
                    error("Character not Expected: " + currentToken);
                    System.out.println("Rule 3 of recovery mode");
                    stack.pop(); //rule 3
                }
            }else if(top.equals("S")) {
            	System.out.println("Rule 2 of recovery mode");
            	stack.pop();
            }
            else {
                error("Unexpected symbol on stack: " + top);
                
            }
        }

        if (endOfInputFlag && stack.isEmpty()) {
            System.out.println("Input is Accepted by LL1");
        } else if (endOfInputFlag) {
            System.out.println("Input is not Accepted by LL1");
        }
    }

    private boolean isTerminal(String s)
    {
      for(int i=0;i<this.terminals.length;i++)
      {
       if(s.equals(this.terminals[i]))
       {return true; }
      }
      return false;
    }

    private boolean isNonTerminal(String s) {
        for (String nonTerminal : nonTerminals) {
            if (s.equals(nonTerminal)) {
                return true;
            }
        }
        return false;
    }


    private String getRule(String non, String term) throws Exception {
        int row = getNonTerminalIndex(non);
        int column = getTerminalIndex(term);
        System.out.print(" Index is ");
        System.out.print(row);
        System.out.print(" ");
        System.out.println(column);
        if (row == -1 || column == -1) {
            return null; // Invalid non-terminal or terminal
        }

        String rule = table[row][column];
        System.out.println(rule);
        if (rule == null) {
            error("No rule for non-terminal " + non + " with terminal " + term);
        }
        return rule;
    }

    private int getNonTerminalIndex(String non) {
        for (int i = 0; i < nonTerminals.length; i++) {
            if (non.equals(nonTerminals[i])) {
                return i;
            }
        }
        return -1; // Non-terminal not found
    }

    private int getTerminalIndex(String term) {
        for (int i = 0; i < terminals.length; i++) {
            if (term.equals(terminals[i])) {
                return i;
            }
        }
        return -1; // Terminal not found
    }

    private void pushRule(String rule) {
        int i = rule.length() - 1;
        while (i >= 0) {
            String str;

            if (i > 0 && isTwoCharToken(rule.substring(i - 1, i + 1))) { // ++ or -- add to stack as one unit
                str = rule.substring(i - 1, i + 1);
                i--; // we checked two so skip
            } else {
                str = String.valueOf(rule.charAt(i)); // only one character
            }
            stack.push(str);
            i--;
//          System.out.print("Pushed to stack: ");
//          System.out.println(str);
        }
    }

    private boolean isTwoCharToken(String token) {
    	if ("++".equals(token)) {
            return true;
        }
    	if ("--".equals(token)) {
            return true;
        }
        return false;
    }



    private void error(String message) throws Exception {
    	if(interpreter) {
    		throw new Exception(message + " on line " + lineNumber);
    	}
        errorMessages.add(message + " on line " + lineNumber);
    }
    
    public String reportErrors() {
    	String all = "";
        
            System.out.println("Syntax errors found:");
            for (String errorMsg : errorMessages) {
                all += errorMsg;
                all += "\n";
            }
            return all;
        }
    
}
