package controllers;

import java.util.Scanner;

/**
 * {@code Controller} - Интерфейс, определяющий общее поведение для всех контроллеров
 * в приложении.  Предоставляет базовую функциональность для обработки пользовательского
 * ввода и управления потоком выполнения программы.
 */
public interface Controller {
  Scanner scanner = new Scanner(System.in);

  Action run();

  Action checkInput();
}
