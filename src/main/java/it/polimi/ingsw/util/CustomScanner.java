package it.polimi.ingsw.util;
import java.io.InputStream;
import java.util.Scanner;

public class CustomScanner {
    private Scanner scanner;
    private String preloadedString;

    public CustomScanner(InputStream source) {
        this.scanner = new Scanner(source);
        this.preloadedString = null;
    }

    public String nextLine() {
        if (preloadedString != null) {
            String line = preloadedString;
            preloadedString = null;
            return line;
        }
        return scanner.nextLine();
    }

    public void setPreloadedString(String preloadedString) {
        this.preloadedString = preloadedString;
    }

    public void close() {
        scanner.close();
    }
}
