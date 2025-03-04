package controllers.impl;

import constants.FileConstants;
import controllers.Action;
import controllers.OrdersController;
import controllers.impl.importexport.ExportController;
import controllers.impl.importexport.ImportController;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manager.MainManager;
import model.OrderStatus;
import model.impl.Order;
import org.springframework.stereotype.Component;
import view.OrdersMenu;

/**
 * {@code OrdersControllerImpl} - Реализация интерфейса {@link OrdersController},
 * представляющая собой контроллер для управления заказами.
 */
@Slf4j
@Component
@AllArgsConstructor
public class OrdersControllerImpl implements OrdersController {
  private final MainManager mainManager;
  private final OrdersMenu ordersMenu;

  @Override
  public Action run() {
    ordersMenu.showMenu();
    Action action = checkInput();

    while (action == Action.CONTINUE) {
      ordersMenu.showMenu();
      action = checkInput();
    }

    return action;
  }

  @Override
  public Action checkInput() {
    int answer = (int) getNumberFromConsole();

    return switch (answer) {
      case 1:
        createOrder();
        yield Action.CONTINUE;
      case 2:
        cancelOrder();
        yield Action.CONTINUE;
      case 3:
        showOrderDetails();
        yield Action.CONTINUE;
      case 4:
        setOrderStatus();
        yield Action.CONTINUE;
      case 5:
        getOrdersByDate();
        yield Action.CONTINUE;
      case 6:
        getOrdersByPrice();
        yield Action.CONTINUE;
      case 7:
        getOrdersByStatus();
        yield Action.CONTINUE;
      case 8:
        getCompletedOrdersByDate();
        yield Action.CONTINUE;
      case 9:
        getCompletedOrdersByPrice();
        yield Action.CONTINUE;
      case 10:
        getCountCompletedOrders();
        yield Action.CONTINUE;
      case 11:
        getEarnedSum();
        yield Action.CONTINUE;
      case 12:
        importOrder();
        yield Action.CONTINUE;
      case 13:
        exportOrder();
        yield Action.CONTINUE;
      case 14:
        importAll();
        yield Action.CONTINUE;
      case 15:
        exportAll();
        yield Action.CONTINUE;
      case 16:
        yield Action.MAIN_MENU;
      case 17:
        yield Action.EXIT;
      default:
        ordersMenu.showInputError("Неизвестная команда");
        yield Action.CONTINUE;
    };
  }

  private String getClientNameFromConsole() {
    ordersMenu.showGetClientName();
    return scanner.nextLine().trim();
  }

  private Map<Long, Integer> getBooksFromConsole() {
    ordersMenu.showBooks(mainManager.getAllBooks());
    ordersMenu.showGetAmountBooks();
    int count = (int) getNumberFromConsole();

    long tempId;
    int tempAmount;
    Map<Long, Integer> booksIds = new HashMap<>();

    for (int i = 0; i < count; i++) {
      tempId = getBookFromConsole(i);
      while (!mainManager.containsBook(tempId) || booksIds.containsKey(tempId)) {
        if (!mainManager.containsBook(tempId)) {
          ordersMenu.showError("Не удалось найти книгу №" + tempId);
          ordersMenu.showGetId("Выберите другую книгу и введите её id: ");
          tempId = getNumberFromConsole();
        }
        if (booksIds.containsKey(tempId)) {
          ordersMenu.showInputError("Вы уже выбрали эту книгу");
          ordersMenu.showGetId("Выберите другую книгу и введите её id: ");
          tempId = getNumberFromConsole();
        }
      }

      ordersMenu.showGetAmountBooks();
      tempAmount = (int) getNumberFromConsole();
      booksIds.put(tempId, tempAmount);
    }
    return booksIds;
  }

  private long getBookFromConsole(int index) {
    ordersMenu.showGetBookId(index);
    return getNumberFromConsole();
  }

  @Override
  public void createOrder() {
    try {
      Order createdOrder = mainManager.createOrder(getBooksFromConsole(),
          getClientNameFromConsole(), LocalDateTime.now());
      ordersMenu.showSuccess("Заказ создан: " + createdOrder.getInfoAbout());
    } catch (Exception e) {
      log.warn("При создании заказа произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void cancelOrder() {
    try {
      ordersMenu.showOrders(mainManager.getAllOrders());
      ordersMenu.showGetId("Введите id заказа, который хотите отменить: ");
      long id = getNumberFromConsole();
      mainManager.cancelOrder(id);
    } catch (Exception e) {
      log.warn("При отмене заказа произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void showOrderDetails() {
    ordersMenu.showGetId("Введите Id заказа: ");
    try {
      long id = getNumberFromConsole();
      Optional<Order> maybeOrder = mainManager.getOrder(id);
      if (maybeOrder.isEmpty()) {
        ordersMenu.showError("Не удалось найти заказ №" + id);
      } else {
        ordersMenu.showOrder(maybeOrder.get());
      }
    } catch (Exception e) {
      log.warn("При поиске заказа произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void setOrderStatus() {
    ordersMenu.showGetId("Введите Id заказа: ");
    long orderId = getNumberFromConsole();

    OrderStatus newStatus = getStatusFromConsole();

    try {
      mainManager.setOrderStatus(orderId, newStatus);
      ordersMenu.showSuccess("Статус заказа №" + orderId + " изменен на " + newStatus);
    } catch (Exception e) {
      log.warn("При изменении статуса заказа произошла ошибка: {}", e.getMessage(), e);
    }
  }

  private OrderStatus getStatusFromConsole() {
    ordersMenu.showGetNewStatus();
    String newStatus = scanner.nextLine().trim();

    while (!newStatus.equalsIgnoreCase(OrderStatus.COMPLETED.toString())
        && !newStatus.equalsIgnoreCase(OrderStatus.NEW.toString())
        && !newStatus.equalsIgnoreCase(OrderStatus.CANCELED.toString())) {
      ordersMenu.showInputError("Вы ввели некорректный статус. Попробуйте ещё раз");
      ordersMenu.showGetNewStatus();
      newStatus = scanner.nextLine().trim();
    }
    return OrderStatus.valueOf(newStatus.toUpperCase());
  }

  private void showOrders(List<Order> orders, String methodName) {
    try {
      ordersMenu.showOrders(orders);
    } catch (Exception e) {
      log.warn("При выводе заказов, отсортированных по {}, произошла ошибка: {}",
          methodName, e.getMessage(), e);
    }
  }

  @Override
  public void getOrdersByDate() {
    showOrders(mainManager.getAllOrdersByDate(), "дате");
  }

  @Override
  public void getOrdersByPrice() {
    showOrders(mainManager.getAllOrdersByPrice(), "цене");
  }

  @Override
  public void getOrdersByStatus() {
    showOrders(mainManager.getAllOrdersByStatus(), "статусу");
  }

  @Override
  public void getCountCompletedOrders() {
    try {
      mainManager.getCountCompletedOrders(getBeginDate(), getEndDate());
    } catch (Exception e) {
      ordersMenu.showError(e.getMessage());
    }
  }

  @Override
  public void getEarnedSum() {
    try {
      mainManager.getEarnedSum(getBeginDate(), getEndDate());
    } catch (Exception e) {
      log.warn("При получении заработанной суммы произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void getCompletedOrdersByDate() {
    try {
      ordersMenu.showOrders(mainManager.getCompletedOrdersByDate(getBeginDate(), getEndDate()));
    } catch (Exception e) {
      log.warn("При получении количества выполненных заказов произошла ошибка: {}",
          e.getMessage(), e);
    }
  }

  @Override
  public void getCompletedOrdersByPrice() {
    try {
      ordersMenu.showOrders(mainManager.getCompletedOrdersByPrice(getBeginDate(), getEndDate()));
    } catch (Exception e) {
      log.warn("При получении количества выполненных заказов произошла ошибка: {}",
          e.getMessage(), e);
    }
  }

  private LocalDateTime getBeginDate() {
    ordersMenu.showGetBeginDate();
    return getDateFromConsole();
  }

  private LocalDateTime getEndDate() {
    ordersMenu.showGetEndDate();
    return getDateFromConsole();
  }

  private LocalDateTime getDateFromConsole() {
    ordersMenu.showGetYear();
    int year = scanner.nextInt();

    ordersMenu.showGetMonth();
    int month = scanner.nextInt();

    ordersMenu.showGetDay();
    int day = scanner.nextInt();

    try {
      return LocalDateTime.of(year, month, day, 0, 0, 0);
    } catch (DateTimeException e) {
      ordersMenu.showInputError("Некорректный формат даты. Попробуйте ещё раз");
      return getDateFromConsole();
    }
  }

  @Override
  public void importOrder() {
    try {
      Optional<Order> findOrder = ImportController.importItem(FileConstants.IMPORT_ORDER_PATH,
          ImportController::orderParser);
      if (findOrder.isPresent()) {
        mainManager.importItem(findOrder.get());
        ordersMenu.showSuccessImport(findOrder.get());
        findOrder.ifPresent(ordersMenu::showItem);
      } else {
        ordersMenu.showErrorImport();
      }
    } catch (Exception e) {
      log.warn("При импорте заказа произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void exportOrder() {
    Order exportOrder;
    try {
      ordersMenu.showOrders(mainManager.getAllOrders());
      ordersMenu.showGetId("Введите id заказа, который хотите экспортировать: ");
      long exportId = getNumberFromConsole();
      exportOrder = getExportOrder(exportId);

      ExportController.exportItemToFile(exportOrder,
          FileConstants.EXPORT_ORDER_PATH, FileConstants.ORDER_HEADER);
    } catch (IllegalArgumentException e) {
      log.warn("При экспорте заказа произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void importAll() {
    try {
      List<Order> importedOrders = ImportController.importAllItemsFromFile(
          FileConstants.IMPORT_ORDER_PATH, ImportController::orderParser);
      if (!importedOrders.isEmpty()) {
        ordersMenu.showMessage("Результат импортирования: ");
        for (Order importedOrder : importedOrders) {
          mainManager.importItem(importedOrder);
          ordersMenu.showMessage("Импортирован: " + importedOrder.getInfoAbout());
        }
        log.info("Все заказы успешно импортированы.");
      } else {
        log.warn("Не удалось импортировать заказы. Возможно, файл пуст.");
      }
    } catch (Exception e) {
      log.warn("При импорте заказов произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void exportAll() {
    try {
      ExportController.exportAll(mainManager.getAllOrders(),
          FileConstants.EXPORT_ORDER_PATH, FileConstants.ORDER_HEADER);
    } catch (Exception e) {
      log.warn("При экспорте заказов произошла ошибка: {}", e.getMessage(), e);
    }
  }

  private Order getExportOrder(long id) {
    Optional<Order> order = mainManager.getOrder(id);
    if (order.isPresent()) {
      return order.get();
    }
    throw new IllegalArgumentException("Заказ №" + id + " не найден");
  }

  private long getNumberFromConsole() {
    long answer;
    while (true) {
      try {
        answer = InputUtils.getNumberFromConsole();
        if (answer <= 0) {
          throw new NumberFormatException();
        }
        break;
      } catch (NumberFormatException e) {
        ordersMenu.showInputError("Неверный формат, попробуйте еще раз");
      }
    }
    return answer;
  }
}