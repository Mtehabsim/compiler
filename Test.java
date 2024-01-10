//package compiler;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Scanner;
//import java.util.StringTokenizer;
//
//public class Test {
//
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		Scanner myObj = new Scanner(System.in);
//	    System.out.println("Enter Word");
//	    
//	    String word = myObj.nextLine();
//	    
////	    ECERCISE 7
//	    count(word);
////	    EXERCIES 6
//	    reverseAll(word);
////	    EXERCISE 5
//// example Hello, my name is Elissa, and I was born on 5/4/1986, I work as a programmer and receive a salary of 3500$ per month.
//	    information(word);
////	    EXERCISE4
//	    System.out.print(replaceJava(word));
//	    
//	    
////	    EXERCISE3
//	    System.out.print(countVowel(word));
//	    
////	    EXERCISE2
//	    System.out.print(removeDuplicates(word,word.length()));
//	    
////	    EXERCISE 1
//		String s = reverseText(word);
//		System.out.println(s);
//		
//	}
//	public static String reverseText(String text) {
//	    StringBuilder reversedText = new StringBuilder(text);
//	    return reversedText.reverse().toString();
//	}
//	
//	public static char[] removeDuplicates(String w,int n){
//		  char s[] = w.toCharArray();
//		  Map<Character,Integer> exists = new HashMap<>();
//		 
//		  String st = "";
//		  for(int i = 0; i < n; i++){
//		    if(!exists.containsKey(s[i]))
//		    {
//		      st += s[i];
//		      exists.put(s[i], 1);
//		    }
//		  }
//		  return st.toCharArray();
//		}
//	
//	public static int countVowel(String w){
//		  String s[] = w.split(" ");
//		  ArrayList<Character> exists = new ArrayList<Character>(4);
//
//		    // populate the list
//		    exists.add('e');
//		    exists.add('a');
//		    exists.add('i');
//		    exists.add('o');
//		    exists.add('u');
//		  
//		  int count = 0;
//		  for(int i = 0; i < s.length; i++){
//			  char x[] = s[i].toCharArray();
//
//			  for(int j = 0; j < s[i].length(); j++){
//				  if(exists.contains(x[j]))
//				    {
//				      count++;
//				    }
//			  }
//			 
//		  }
//		  return count;
//		}
//
//	public static String replaceJava(String text) {
//	    String k = text.replace("Java", "C#");
//	    String r = k.replace("java", "C#");
//	    return r;
//	}
//	
//	public static void information(String inputString) {
//
//
//        
//        String name = null;
//        String birthday = null;
//        String work = null;
//        String salary = null;
//
//        
//        StringTokenizer tokenizer = new StringTokenizer(inputString);
//        while (tokenizer.hasMoreTokens()) {
//            String token = tokenizer.nextToken();
//            if (token.equals("is")) {
//                name = tokenizer.nextToken();
//            }
//            else if (token.equals("on")) {
//                birthday = tokenizer.nextToken();
//            }
//            else if (token.equals("a")) {
//                work = tokenizer.nextToken();
//            }
//            else if (token.equals("of")) {
//                salary = tokenizer.nextToken();
//            }
//        }
//
//        System.out.println("Name: " + name);
//        System.out.println("Birthday: " + birthday);
//        System.out.println("Work: " + work);
//        System.out.println("Salary: " + salary + " $");
//	    
//	}
//	
//	public static void reverseAll(String text) {
//		System.out.println("the text:");
//		System.out.println(text);
//		System.out.println("reverse word by word:");
//		String[] words = text.split(" ");
//
//        // Print the reversed words in reverse order
//        for (int i = words.length - 1; i >= 0; i--) {
//            System.out.print(words[i]);
//            if (i > 0) {
//                System.out.print(" ");
//            }
//        }
//	}
//	
//	public static void count(String text) {
//		int lineCount = text.split("\n").length;
//        int wordCount = text.split("\\s+").length;
//        int charCount = text.length();
//        int numberCount = countNumbers(text);
//
//        System.out.println("Number of lines: " + lineCount);
//        System.out.println("Number of words: " + wordCount);
//        System.out.println("Number of characters: " + charCount);
//        System.out.println("Number of numbers: " + numberCount);
//	}
//	
//	private static int countNumbers(String text) {
//        String[] words = text.split("\\s+");
//        int count = 0;
//
//        for (String word : words) {
//            if (word.matches(".*\\d+.*")) {
//                count++;
//            }
//        }
//
//        return count;
//    }
//	
//	}
//
//	
