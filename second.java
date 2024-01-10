package compiler;

import java.util.Scanner;

public class second {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a number: ");
        String input = scanner.nextLine();
        String digits = "\\d";
        String num = "\\d+(\\.\\d+)?(E[+-]?\\d+)?";

        if (input.matches(num)) {
            System.out.println("The input is a number");
        } else if(input.matches(num)) {
            System.out.println("The input is a digit");
        }
        else {
        	System.out.println("Not number or a digit");
        }
        }
    }

