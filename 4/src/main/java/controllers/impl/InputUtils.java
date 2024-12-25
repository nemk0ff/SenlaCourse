package controllers.impl;

import java.util.Scanner;

public class InputUtils {
    private static final Scanner scanner = new Scanner(System.in);

    public static long getNumberFromConsole() {
        String input = scanner.nextLine().trim();
        return Long.parseLong(input);
    }
}
