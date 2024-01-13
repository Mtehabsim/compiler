package compiler;

public class Parser {
	Lexical lexical;
	public Parser(String inputFilePath) {
		lexical = new Lexical(inputFilePath);
		String wordString = "test";
		
		while (wordString != "No") {
			wordString = lexical.getToken();
			
			if(wordString != "No" && wordString != "")
			System.out.println("Token is " + wordString);
			
		}
	}

}
