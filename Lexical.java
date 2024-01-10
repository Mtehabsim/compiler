package compiler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
	int current = -1;
	HashMap<String, String> identifierTable;
	Set<String> preservedKeys = new HashSet<>();
    List<String> tokens = new ArrayList<String>();
    
    
	public Lexical(String inputFilePath, String outputFilePath) {
		try {
		        	
		        	removeComments(inputFilePath, outputFilePath);
		        	identifierTable = new HashMap<>();
		            preservedKeys.add("int");
		            preservedKeys.add("char");
		            preservedKeys.add("float");
		            preservedKeys.add("double");
		            preservedKeys.add(";");
		            preservedKeys.add("\\)");
		            
		            
		            try {
		                Path path = Paths.get(outputFilePath);
		                String fileContent = Files.readString(path, StandardCharsets.UTF_8);
		                
		                StringTokenizer tokenizer = new StringTokenizer(fileContent);
		
		                while (tokenizer.hasMoreTokens()) {
		                    String token = tokenizer.nextToken();
		                    String secondToken = "";
		                    
		                    if(tokenizer.hasMoreTokens())
		                    secondToken = tokenizer.nextToken();
		                    processIdentifier(token, secondToken, identifierTable,preservedKeys);
		                    processNumbersDigits(token, identifierTable);
		                    processNumbersDigits(secondToken, identifierTable);
		                }
		
		                System.out.println("\nSymbol Table:\n");
		                for (String identifier : identifierTable.keySet()) {
		                    String type = identifierTable.get(identifier);
		                    System.out.println(identifier + ": " + type);
		                }
		
		            } catch (IOException e) {
		                System.err.println("An error occurred while reading the file: " + e.getMessage());
		            }
		        } catch (IOException e) {
		            System.err.println("An error occurred: " + e.getMessage());
		        }
		}
    
    
    private static void removeComments(String inputFilePath, String outputFilePath) throws IOException {
        try (FileInputStream inputFileStream = new FileInputStream(inputFilePath);
             FileOutputStream outputFileStream = new FileOutputStream(outputFilePath)) {

            boolean inSingleLineComment = false;
            boolean inMultiLineComment = false;
            

            int prevChar = -1;
            int currentChar;
            StringBuilder word = new StringBuilder();

            while ((currentChar = inputFileStream.read()) != -1) {
                if (currentChar == ' ') {
                    word.setLength(0); // Clear the word
                }
                if (currentChar != ' ') {
                    word.append((char) currentChar);
                }

                if (inSingleLineComment) {
                    if (currentChar == '\n') {
                        inSingleLineComment = false;
                        currentChar = -1;
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
                        if (prevChar != -1) {
                            outputFileStream.write(prevChar);
                        }
                    }
                }
                prevChar = currentChar;
            }

            if (prevChar != -1 && !inSingleLineComment && !inMultiLineComment) {
                outputFileStream.write(prevChar);
            }
        }
    }

    public String getToken() {
    	
    	this.current += 1;
    	if (this.current < this.tokens.size()) {
    		return this.tokens.get(this.current);
    	}
    	else {
			return "No";
		}
    }
    private void processIdentifier(String input, String buffer, HashMap<String, String> identifierTable, Set<String> preservedKeys) {
        String identifierRegex = "\\b[a-zA-Z][a-zA-Z0-9]*\\b";
        Pattern pattern = Pattern.compile(identifierRegex);
        Matcher matcher = pattern.matcher(input);
        String type = "";
        for (String key : preservedKeys) {
            Pattern ptr2 = Pattern.compile(key);
            Matcher mtc2 = ptr2.matcher(input);

            if (mtc2.find()) {
            	tokens.add(key);
                type = key;
                System.out.println("buffer" + buffer + input);
                matcher = pattern.matcher(buffer);
                if ( matcher.find()) {
                	tokens.add(matcher.group());
                }
            }
        }
        while (matcher.find()) {
            String identifier = matcher.group();
            System.out.println(identifier);
            
            if (!identifierTable.containsKey(identifier) ) {
            	type = "identifier " + type;
            	identifierTable.put(identifier, type);
            	type = "";
            	matcher = pattern.matcher(buffer);
            	tokens.add(identifier);
            	
            }
            
        }
    }
    
    private void processNumbersDigits(String input, HashMap<String, String> identifierTable) {
    	 
    
    	
    	String digits = "\\d";
        String num = "\\d+(\\.\\d+)?(E[+-]?\\d+)?";
        String minusDigits = "-" + digits;
        String minusNum = "-" + num;
        
        if (input.matches(minusDigits)) {
            
            tokens.add(input);
        }
        else if (input.matches(minusNum)) {
            System.out.print("minus number : ");
            System.out.println(input);
            tokens.add(input);
        }
        
        else if (input.matches(digits)) {
            System.out.print("digit : ");
            System.out.println(input);
            tokens.add(input);
        } else if(input.matches(num)) {
        	System.out.print("number : ");
            System.out.println(input);
            tokens.add(input);
        }
    }
}
