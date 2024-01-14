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
    String lastTypeRecieved = "";
    public Lexical(String inputFilePath, Parser parser) {
        this.parser = parser;
        try {
            String fileContent = removeComments(inputFilePath);
            identifierTable = new HashMap<>();
            preservedKeys.add("int");
            preservedKeys.add("float");
            preservedKeys.add("String");

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
            parser.receiveToken("$");
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
            boolean inStringLiteral = false;
            int lineNumber = 1;
            int prevChar = -1;
            int currentChar;

            while ((currentChar = inputFileStream.read()) != -1) {
                // Check for line endings
                if (currentChar == '\n') {
                    lineNumber++;
                    if (inStringLiteral) {
                        throw new IOException("Unexpected end of line in string literal at line " + lineNumber);
                    }
                    inSingleLineComment = false;
                }

                
                // Process string literals
                if (!inSingleLineComment && !inMultiLineComment && currentChar == '"' && prevChar != '\\') {
                    inStringLiteral = !inStringLiteral;

                }
                
                
                
                if (inSingleLineComment) {
                    // Skip processing for single line comments
                } else if (inMultiLineComment) {
                    if (prevChar == '*' && currentChar == '/') {
                        inMultiLineComment = false;
                    }
                } else {
                    if (prevChar == '/' && currentChar == '/') {
                        inSingleLineComment = true;
                    } else if (prevChar == '/' && currentChar == '*') {
                        inMultiLineComment = true;
                    } else {
                        if (prevChar != -1 && !inMultiLineComment  && (char)prevChar != '/') {
  
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
//    	System.out.println(token);
    	if (preservedKeys.contains(token)) {
    			lastTypeRecieved = token;
            return true;
        } 
    	else if (processStringLiterals(token)) {
            return true;
        }
    	else if (processIdentifier(token)) {
    		return true;
        } else if (processNumbersDigits(token)) {
            return true;
        } else if (processDelimiters(token)) {
            parser.receiveToken(token);
            return true;
        }
        return false;
    }


    private boolean processIdentifier(String input) {
        String identifierRegex = "\\b[a-zA-Z][a-zA-Z0-9]*\\b";
        Pattern pattern = Pattern.compile(identifierRegex);
        Matcher matcher = pattern.matcher(input);
        
        if (matcher.matches()) {

        		if (!preservedKeys.contains(input)) {

            	String temp = "Identifier " + lastTypeRecieved;

    			if (lastTypeRecieved == "" && !identifierTable.containsKey(input)) {
    				throw new IllegalArgumentException("No type for identifier " + input);
    			}
            	
                identifierTable.putIfAbsent(input, temp);
	            }
        		
        		if (lastTypeRecieved != "") {
        			//dont tokenize if just declaring a var
        		}
        		else if(lastTypeRecieved != "String") {
	            	parser.receiveToken("i");
	            }
	            if(lastTypeRecieved != "") {
	            	lastTypeRecieved = "";
	        	}
            return true;
        }
        return false;
    }


    private boolean processNumbersDigits(String input) {
        String numRegex = "\\b\\d+(\\.\\d+)?(E[+-]?\\d+)?\\b";
        Pattern pattern = Pattern.compile(numRegex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String number = matcher.group();
            if (number.contains(".")) {
                identifierTable.put(number, "float");
            } else {
                identifierTable.put(number, "int");
            }
            parser.receiveToken("i");
            return true;
        }
        return false;
    }

    private boolean processDelimiters(String input) {
        String[] delimiters = {"(", ")", "[", "]", ";", ":", ".", ",", "*", "-", "+", "/", "<", "=", ">", "!=",
                               "<=", ">="};
        for (String delimiter : delimiters) {
            if (input.equals(delimiter)) {
//                parser.receiveToken(delimiter); // Pass the delimiter as is
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
        } else if (token.equals("!") && nextToken.equals("=")) {
            return "!=";
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
    
    private boolean processStringLiterals(String input) {
        if (input == null || input.length() < 2) {
            return false;
        }

        // Remove escaped quotes for counting
        String withoutEscapedQuotes = input.replace("\\\"", "");
        long quoteCount = withoutEscapedQuotes.chars().filter(ch -> ch == '"').count();

        if (quoteCount % 2 != 0) {
            throw new IllegalArgumentException("String not properly closed with double quotes.");
        }

        if (input.startsWith("\"") && input.endsWith("\"")) {
            // Process and replace escape sequences
            String processedString = input
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"");

            System.out.print("A string found of ");
            System.out.println(processedString); 
//            parser.receiveToken("i"); // no need to pass the string
//            return true;
        }

        return false;
    


//        String stringRegex = "'(?:\\\\[nt]|\\\\'|[^'\\\\])*'";
//        Pattern pattern = Pattern.compile(stringRegex);
//        Matcher matcher = pattern.matcher(input);
//
//        while (matcher.find()) {
//            String stringLiteral = matcher.group();
//            identifierTable.put(stringLiteral, "string");
////            parser.receiveToken(stringLiteral); // Pass the string literal as is
//            return true;
//        }
//        return false;
    }


}

