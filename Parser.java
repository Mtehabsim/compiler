package compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
    private Stack<String> stack = new Stack<>();
//    E  -> TE'
//    E' -> +TE' | -TE' | none
//    T  -> FT'
//    T' -> *FT' | /FT' | %FT' | none
//    F  -> ++F | --F | +F | -F | P
//    P  -> id | (E)
    private List<String> errorMessages = new ArrayList<>();

    private String[][] table = {
    	    {"TA", "TA", "TA", "TA", "TA", null, null, null, "TA", null, null},
    	    {null, "+TA", "-TA", null, null, null, null, null,null, "", ""},
    	    {"FB", "FB", "FB", "FB", "FB", null, null, null, "FB", null, null},
    	    {null, "", "", null, null, "*FB", "/FB", "%FB",null, "", ""},
    	    {"P", "+F", "-F", "++F", "--F", null, null, null, "P", null, null},
    	    {"i", null, null, null, null, null, null, null, "(E)", null, null}
    	};
    	private String[] nonTerminals = {"E", "A", "T", "B", "F", "P"};
    	private String[] terminals = {"i", "+", "-", "++", "--", "*", "/", "%","(", ")", "$"};


    private String currentToken;
    private boolean endOfInputFlag = false;

    public Parser() {
        stack.push("$");
        stack.push("E");
    }

    public void receiveToken(String token) {
        currentToken = token;
        System.out.print("TOKEN is ");
        System.out.println(token);
        processToken();
    }

    public void endOfInput() {
        endOfInputFlag = true;
        processToken();
    }

    private void processToken() {
        while (!stack.isEmpty() && (currentToken != null || endOfInputFlag)) {
            String top = stack.peek();
            System.out.println(top);
            if (isNonTerminal(top)) {
                String rule = getRule(top, currentToken);
                if (rule != null) {
                    stack.pop(); // Pop the non-terminal
                    pushRule(rule); // Push the rule onto the stack
                } else {
                    error("No rule for non-terminal " + top + " with terminal " + currentToken);
                }
            } else if (isTerminal(top)) {
                if (top.equals(currentToken)) {
                    stack.pop(); // Matched terminal
                    currentToken = null; // Reset current token to receive the next one
                } else {
                    error("Mismatched terminal. Expected: " + top + ", got: " + currentToken);
                }
            } else {
                error("Unexpected symbol on stack: " + top);
            }
        }

        if (endOfInputFlag && stack.isEmpty()) {
            System.out.println("Input is Accepted by LL1");
        } else if (endOfInputFlag) {
            System.out.println("Input is not Accepted by LL1");
        }
    }

    // ... (implement isNonTerminal, isTerminal, getRule, pushRule, error, etc.) ...

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


    private String getRule(String non, String term) {
        int row = getNonTerminalIndex(non);
        int column = getTerminalIndex(term);
        System.out.print(" Index is ");
        System.out.print(row);
        System.out.println(" ");
        System.out.print(column);
        if (row == -1 || column == -1) {
            return null; // Invalid non-terminal or terminal
        }

        String rule = table[row][column];
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
        // Splitting the rule into its components.
        // Assuming that each symbol in the rule is separated by a space.
    	System.out.print("RULE is ");
    	System.out.println(rule);
        

                for(int j=rule.length()-1;j>=0;j--)
                {
                  char ch=rule.charAt(j);
                  String str=String.valueOf(ch);
                  System.out.print("Pushed to stack ");
                  System.out.println(str);
                  stack.push(str);
                }

    }


    private void error(String message) {
//        System.out.println(message);
        errorMessages.add(message);
    }
    
    public void reportErrors() {
        if (errorMessages.isEmpty()) {
            System.out.println("File Compiled Successfully.");
        } else {
            System.out.println("Syntax errors found:");
            for (String errorMsg : errorMessages) {
                System.out.println(errorMsg);
            }
        }
    }
}
