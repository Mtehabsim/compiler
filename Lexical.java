package compiler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexical {
    HashMap<String, String> identifierTable;
    Set<String> preservedKeys = new HashSet<>();
    Parser parser;

    public Lexical(String inputFilePath, Parser parser) {
        this.parser = parser;
        try {
            String fileContent = removeComments(inputFilePath);
            identifierTable = new HashMap<>();
            preservedKeys.add("int");
            preservedKeys.add("char");
            preservedKeys.add("float");
            preservedKeys.add("double");
            preservedKeys.add(";");
            preservedKeys.add("\\)");

            StringTokenizer tokenizer = new StringTokenizer(fileContent, " \t\n\r\f", true);

            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.trim().isEmpty()) continue;

                String secondToken = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
                if (secondToken.trim().isEmpty()) {
                    secondToken = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
                }

                String compoundToken = processCompoundToken(token, secondToken);
                if (compoundToken != null) {
                    parser.receiveToken(compoundToken);
                    continue;
                }

                processIndividualToken(token);
                if (!secondToken.isEmpty() && compoundToken == null) {
                    processIndividualToken(secondToken);
                }
            }

            printSymbolTable();
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }


    private static String removeComments(String inputFilePath) throws IOException {
        StringBuilder outputContent = new StringBuilder();
        try (FileInputStream inputFileStream = new FileInputStream(inputFilePath)) {
            boolean inSingleLineComment = false;
            boolean inMultiLineComment = false;
            int prevChar = -1;
            int currentChar;

            while ((currentChar = inputFileStream.read()) != -1) {
                if (inSingleLineComment) {
                    if (currentChar == '\n') {
                        inSingleLineComment = false;
                    }
                } else if (inMultiLineComment) {
                    if (prevChar == '*' && currentChar == '/') {
                        inMultiLineComment = false;
                        prevChar = -1;
                        continue;
                    }
                } else {
                    if (prevChar == '/' && currentChar == '/') {
                        inSingleLineComment = true;
                    } else if (prevChar == '/' && currentChar == '*') {
                        inMultiLineComment = true;
                    } else {
                        if (prevChar != -1 && !inSingleLineComment && !inMultiLineComment) {
                            outputContent.append((char) prevChar);
                        }
                    }
                }
                prevChar = currentChar;
            }

            if (prevChar != -1 && !inSingleLineComment && !inMultiLineComment) {
                outputContent.append((char) prevChar);
            }
        }
        return outputContent.toString();
    }

    private boolean processIndividualToken(String token) {
        if (processIdentifier(token)) {
            parser.receiveToken("i"); // Pass "i" for identifiers
            return true;
        } else if (processNumbersDigits(token)) {
            parser.receiveToken(token); // Pass the token as is for numbers
            return true;
        } else if (processDelimiters(token)) {
            parser.receiveToken(token); // Pass the token as is for delimiters
            return true;
        }
        return false;
    }


    private boolean processIdentifier(String input) {
        String identifierRegex = "\\b[a-zA-Z][a-zA-Z0-9]*\\b";
        Pattern pattern = Pattern.compile(identifierRegex);
        Matcher matcher = pattern.matcher(input);

        boolean found = false;
        while (matcher.find()) {
            String identifier = matcher.group();
            if (!identifierTable.containsKey(identifier) && !preservedKeys.contains(identifier)) {
                identifierTable.put(identifier, "identifier");
                found = true;
            }
        }
        return found;
    }

    private boolean processNumbersDigits(String input) {
        String numRegex = "\\b\\d+(\\.\\d+)?(E[+-]?\\d+)?\\b";
        Pattern pattern = Pattern.compile(numRegex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            identifierTable.put(matcher.group(), "number");
            return true;
        }
        return false;
    }

    private boolean processDelimiters(String input) {
        String[] delimiters = {"\"", ";", "-", "+", "/", "<", "=", ">", "<=", ">="};
        for (String delimiter : delimiters) {
            if (input.contains(delimiter)) {
                return true;
            }
        }
        return false;
    }

    private String processCompoundToken(String token, String nextToken) {
        if (token.equals(">") && nextToken.equals("=")) {
            return ">=";
        } else if (token.equals("<") && nextToken.equals("=")) {
            return "<=";
        }
        // Add more conditions for other compound tokens if needed
        return null;
    }

    public void printSymbolTable() {
        System.out.println("\nSymbol Table:\n");
        for (String identifier : identifierTable.keySet()) {
            String type = identifierTable.get(identifier);
            System.out.println(identifier + ": " + type);
        }
    }

}

