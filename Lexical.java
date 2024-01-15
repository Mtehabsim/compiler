package compiler;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Lexical {
    private List<String> errorMessages = new ArrayList<>(); // Store errors if compiler
    HashMap<String, String> identifierTable; // Symbol table
    Set<String> preservedKeys = new HashSet<>(); // known words
    Parser parser;
    String lastTypeRecieved = ""; // type of identifiers 
    int lineNumber = 1; 
    boolean interpreter = false;
    public Lexical(String inputFilePath, Parser parser, boolean interpreter) throws Exception {
        this.parser = parser;
        this.interpreter = interpreter;
        try {
        	
            String fileContent = removeComments(inputFilePath);
            System.out.println(fileContent);
            identifierTable = new HashMap<>();
            preservedKeys.add("int");
            preservedKeys.add("float");
            preservedKeys.add("String");

            StringTokenizer tokenizer = new StringTokenizer(fileContent, " \t\n\r\f", true);

            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.equals("\n")) {
                    lineNumber++; 
                    continue;
                }
                if (token.trim().isEmpty()) continue;

                String secondToken = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
                if (secondToken.equals("\n")) {
                    lineNumber++; 
                    continue;
                }
                if (secondToken.trim().isEmpty()) {
                    secondToken = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
                }
                
                String compoundToken = processCompoundToken(token, secondToken);
                if (compoundToken != null) {
                    parser.receiveToken(compoundToken, lineNumber);
                    continue;
                }

                processIndividualToken(token);
                if (!secondToken.isEmpty() && compoundToken == null) {
                    processIndividualToken(secondToken);
                }
                if (secondToken.equals("\n")) {
                    lineNumber++; 
                    continue;
                }
            }
            parser.receiveToken("$", lineNumber);
            String errors = parser.reportErrors();
            if (!errorMessages.isEmpty()) {
                errors += "Lexical errors found:" + "\n";
                for (String errorMsg : errorMessages) {
                    errors += errorMsg + "\n";
                }
            }
            printSymbolTable();
            if(!errors.isEmpty())
            throw new Exception(errors);
            
            
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }


    public String removeComments(String fileName) throws Exception {
        String fileContent = new String(Files.readAllBytes(Paths.get(fileName)));
        fileContent = fileContent.replaceAll("//.*", ""); //single line
        fileContent = fileContent.replaceAll("(?s)/\\\\*.*?\\\\*/", ""); //multiple lines 
        boolean inString = false;
        for (int i = 0; i < fileContent.length(); i++) {
            char ch = fileContent.charAt(i);
            if (ch == '"' && (i == 0 || fileContent.charAt(i - 1) != '\\')) { // no escape character
                inString = !inString;
            }
            if (inString && ch == '\n') { // detect error
                error("String not properly closed at index " + i);
            }
        }

        return fileContent;
    }

    private boolean processIndividualToken(String token) throws Exception {
    	
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
            parser.receiveToken(token, lineNumber);
            return true;
        }
    	if(!token.trim().isEmpty()) {
    		error(token + " not expected");
    	    System.out.println("Rule 1 of recovery mode");
    	}

        return false;
    }


    private boolean processIdentifier(String input) throws Exception {
    	
        String identifierRegex = "\\b[a-zA-Z][a-zA-Z0-9]*\\b";
        Pattern pattern = Pattern.compile(identifierRegex);
        Matcher matcher = pattern.matcher(input);
        
        if (matcher.matches()) {

        		if (!preservedKeys.contains(input)) {

            	String temp = "Identifier " + lastTypeRecieved;
    			if (lastTypeRecieved == "" && !identifierTable.containsKey(input)) {
    				error("No type for identifier " + input);
    			}
            	
                identifierTable.putIfAbsent(input, temp);
	            }
        		if (lastTypeRecieved != "") {
        			//dont tokenize if just declaring a var
        		}
        		else if(lastTypeRecieved != "String") {
	            	parser.receiveToken("i", lineNumber);
	            }
	            if(lastTypeRecieved != "") {
	            	lastTypeRecieved = "";
	        	}
            return true;
        }
        return false;
    }


    private boolean processNumbersDigits(String input) throws Exception {
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
            parser.receiveToken("i", lineNumber);
            return true;
        }
        return false;
    }

    private boolean processDelimiters(String input) {
        String[] delimiters = {"(", ")", "[", "]", ";", ":", ".", ",", "*", "-", "+", "/", "<", "=", ">", "!=",
                               "<=", ">=", "++", "%", "--"};
        for (String delimiter : delimiters) {
            if (input.equals(delimiter)) {
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

        String withoutEscapedQuotes = input.replace("\\\"", "");
        long quoteCount = withoutEscapedQuotes.chars().filter(ch -> ch == '"').count();

        if (quoteCount % 2 != 0) {
            throw new IllegalArgumentException("String not properly closed with double quotes.");
        }

        if (input.startsWith("\"") && input.endsWith("\"")) {
            String processedString = input
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"");

            System.out.print("A string found of ");
            System.out.println(processedString + " On line " + lineNumber); 
//            parser.receiveToken("i"); // no need to pass the string
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

}

