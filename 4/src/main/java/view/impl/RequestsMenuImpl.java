package view.impl;

import java.util.List;
import java.util.Map;
import model.impl.Request;
import view.RequestsMenu;

/**
 * {@code RequestsMenuImpl} - Реализация интерфейса {@link RequestsMenu}, предоставляющая методы
 * для отображения информации о запросах на книги магазина.
 */
public class RequestsMenuImpl implements RequestsMenu {

  @Override
  public void showMenu() {
    System.out.println("##############################");
    System.out.println("########  МЕНЮ ЗАПРОСОВ  #####");
    System.out.println("##############################");
    System.out.println("    Выберите действие:");
    System.out.println("1. Оставить запрос на книгу");
    System.out.println("2. Вывести список открытых запросов на книги (сортировка по количеству "
        + "запросов)");
    System.out.println("3. Вывести список открытых запросов на книги (сортировка по цене)");
    System.out.println("4. Импортировать запрос");
    System.out.println("5. Экспортировать запрос");
    System.out.println("6. Импортировать все запросы");
    System.out.println("7. Экспортировать все запросы");
    System.out.println("8. Вывести все запросы за все время");
    System.out.println("9. Вернуться в главное меню");
    System.out.println("10. Выйти из программы");
  }

  @Override
  public void showRequests(Map<Long, Long> requests) {
    System.out.println("======= ОТКРЫТЫЕ ЗАПРОСЫ НА КНИГИ =======");
    requests.forEach((key, value) -> {
      System.out.println("Книга: №" + key);
      System.out.println("Количество запросов: " + value);
      System.out.println();
    });
    System.out.println("=========================================");
  }

  @Override
  public void showRequests(List<Request> requests) {
    requests.forEach(this::showItem);
  }

  @Override
  public void showSuccessImport(Request request) {
    log.info("Выполнен импорт запроса: {}", request.getInfoAbout());
  }

}
