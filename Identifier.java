package compiler;

import java.io.IOException;

public class Identifier {

    public static void main(String[] args) throws IOException {
        char ch;
        StringBuilder id = new StringBuilder("");
        while (true) {
            ch = (char) System.in.read();

            if (Character.isLetter(ch)) {

                while (Character.isDigit(ch) || Character.isLetter(ch)) {
                    id.append(ch);
                    ch = (char) System.in.read();
                }

                System.out.println(id);
                id = new StringBuilder("");

            } else if (ch == '\n' || ch == '\r') {
                // Add a condition to break the loop on newline or carriage return
                break;
            }
        }
    }
}
