package view.impl;

import java.util.List;
import java.util.Map;
import model.impl.Order;
import view.OrdersMenu;

/**
 * {@code OrdersMenuImpl} - Реализация интерфейса {@link OrdersMenu}, предоставляющая методы
 * для отображения информации о заказах пользователю.
 */
public class OrdersMenuImpl implements OrdersMenu {

  @Override
  public void showMenu() {
    System.out.println("##############################");
    System.out.println("########  МЕНЮ ЗАКАЗОВ  ######");
    System.out.println("##############################");
    System.out.println("    Выберите действие:");
    System.out.println("1. Создать заказ");
    System.out.println("2. Отменить заказ");
    System.out.println("3. Посмотреть детали заказа");
    System.out.println("4. Изменить статус заказа");
    System.out.println("5. Вывести список заказов (сортировка по дате исполнения)");
    System.out.println("6. Вывести список заказов (сортировка по цене)");
    System.out.println("7. Вывести список заказов (сортировка по статусу)");
    System.out.println("8. Вывести список выполненных заказов за период времени "
        + "(сортировка по дате)");
    System.out.println("9. Вывести список выполненных заказов за период времени "
        + "(сортировка по цене)");
    System.out.println("10. Вывести количество выполненных заказов за период времени");
    System.out.println("11. Вывести сумму заработанных средств за период времени");
    System.out.println("12. Импортировать заказ");
    System.out.println("13. Экспортировать заказ");
    System.out.println("14. Импортировать все заказы");
    System.out.println("15. Экспортировать все заказы");
    System.out.println("16. Вернуться в главное меню");
    System.out.println("17. Выйти из программы");
  }

  @Override
  public void showOrders(List<Order> orders) {
    if (orders.isEmpty()) {
      System.out.println("У магазина не было заказов");
      return;
    }

    System.out.println("============ ЗАКАЗЫ ============");
    orders.forEach(this::showOrder);
    System.out.println("================================");
  }

  @Override
  public void showOrder(Order order) {
    System.out.println(order.getInfoAbout());
    for (Map.Entry<Long, Integer> s : order.getBooks().entrySet()) {
      System.out.println("Книга: " + s.getKey() + ", количество: " + s.getValue());
    }
    System.out.println("");
  }

  @Override
  public void showGetBeginDate() {
    System.out.println("Введите дату начала периода");
  }

  @Override
  public void showGetEndDate() {
    System.out.println("Введите дату конца периода");
  }

  @Override
  public void showGetYear() {
    System.out.println("Введите год: ");
  }

  @Override
  public void showGetMonth() {
    System.out.println("Введите месяц: ");
  }

  @Override
  public void showGetDay() {
    System.out.println("Введите день: ");
  }

  @Override
  public void showGetClientName() {
    System.out.println("Введите имя клиента: ");
  }

  @Override
  public void showGetNewStatus() {
    System.out.println("Введите новый статус заказа (NEW, COMPLETED или CANCELED): ");
  }

  @Override
  public void showSuccessImport(Order order) {
    log.info("Выполнен импорт заказа: {}", order.getInfoAbout());
  }
}
