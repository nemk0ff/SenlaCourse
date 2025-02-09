package controllers.impl;

import java.util.Scanner;

/**
 * {@code InputUtils} - Утилитарный класс, предоставляющий статические методы для получения
 * ввода от пользователя через консоль.
 */
public class InputUtils {
  private static final Scanner scanner = new Scanner(System.in);

  public static long getNumberFromConsole() {
    String input = scanner.nextLine().trim();
    return Long.parseLong(input);
  }
}
