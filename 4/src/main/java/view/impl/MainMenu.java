package view.impl;

import lombok.extern.slf4j.Slf4j;
import view.Menu;

/**
 * {@code MainMenu} - Класс, реализующий интерфейс {@link Menu} и отображающий главное меню
 * приложения с различными опциями.
 */
@Slf4j
public class MainMenu implements Menu {
  @Override
  public void showMenu() {
    System.out.println("##############################");
    System.out.println("#######  ГЛАВНОЕ МЕНЮ  #######");
    System.out.println("##############################");
    System.out.println("    Выберите действие:");
    System.out.println("1. Действие с книгами");
    System.out.println("2. Действие с заказами");
    System.out.println("3. Действие с запросами");
    System.out.println("4. Выйти из программы");
  }
}
