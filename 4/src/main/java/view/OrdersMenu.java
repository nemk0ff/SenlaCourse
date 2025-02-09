package view;

import java.util.List;
import model.impl.Book;
import model.impl.Order;

/**
 * {@code OrdersMenu} - Интерфейс, расширяющий {@link Menu} и определяющий
 * методы для отображения информации о заказах пользователю.
 */
public interface OrdersMenu extends Menu {
  void showOrders(List<Order> orders);

  void showOrder(Order order);

  void showGetBeginDate();

  void showGetEndDate();

  void showGetYear();

  void showGetMonth();

  void showGetDay();

  void showGetClientName();

  void showGetNewStatus();

  void showCountCompletedOrders(Long count);

  void showEarnedSum(Double sum);

  void showSuccessImport(Order order);
}
