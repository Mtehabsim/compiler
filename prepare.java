package compiler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class prepare {

	public static void main(String[] args) throws Exception {
	        String inputFilePath = "test1O2.c"; // name of file 
	        boolean interpreter = false; // true it become error one at a time
	        Parser parser = new Parser(interpreter); //create parser
	        Lexical lexical = new Lexical(inputFilePath, parser, interpreter); // lexical object	
		}
}
