package compiler;

public class Parser {
	Lexical lexical;
	public Parser(String inputFilePath, String outputFilePath) {
		lexical = new Lexical(inputFilePath, outputFilePath);
		String wordString = "test";
		
		while (wordString != "No") {
			wordString = lexical.getToken();
			
			if(wordString != "No" && wordString != "")
			System.out.println("Token is " + wordString);
			
		}
	}

}
