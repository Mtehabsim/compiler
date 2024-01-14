package compiler;

import java.util.Stack;

public class Parser {
    private Stack<String> stack = new Stack<>();
    private String[][] table = {
            {"TA","TA",null,null,null,null,null,null},
            {null,null,"","+TB","-TB",null,null,""}  ,
            {"FB","FB",null,null,null,null,null,null}  ,
            {null,null,"","","","*FB","/FB",""}  ,
            {"i","(E)",null,null,null,null,null,null}  ,
        };
    private String[] nonTerminals = {"E", "A", "T", "B", "F"};
    private String[] terminals = {"i", "(", ")", "+", "-", "*", "/", "$"};

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
        System.out.println(message);
        throw new RuntimeException(message);
    }
}
