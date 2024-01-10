package compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Identifier2 {

    public static void main(String[] args) {
        // Example string
        String inputString = "Hello123 World 456 Java_Identifier";

        // Extract identifiers from the string
        extractIdentifiers(inputString);
    }

    private static void extractIdentifiers(String input) {
        // Regular expression to match identifiers
        String identifierRegex = "\\b[a-zA-Z][a-zA-Z0-9]*\\b";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(identifierRegex);
        Matcher matcher = pattern.matcher(input);

        // Find and print identifiers
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
