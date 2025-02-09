package controllers;

/**
 * {@code OrdersController} - Интерфейс, определяющий поведение для контроллера, управляющего
 * операциями, связанными с заказами.
 */
public interface OrdersController extends Controller {
  void createOrder();

  void cancelOrder();

  void showOrderDetails();

  void setOrderStatus();

  void getOrdersByDate();

  void getOrdersByPrice();

  void getOrdersByStatus();

  void getCompletedOrdersByDate();

  void getCompletedOrdersByPrice();

  void getCountCompletedOrders();

  void getEarnedSum();

  void importAll();

  void exportAll();

  void importOrder();

  void exportOrder();
}
