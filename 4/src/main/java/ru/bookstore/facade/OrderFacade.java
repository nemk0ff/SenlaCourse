package ru.bookstore.facade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import ru.bookstore.model.OrderStatus;
import ru.bookstore.model.impl.Order;
import ru.bookstore.sorting.OrderSort;

public interface OrderFacade {
  Order get(Long orderId);

  List<Order> getAll(OrderSort orderSort);

  void setOrderStatus(Long id, OrderStatus orderStatus);

  List<Order> getCompleted(OrderSort orderSort, LocalDateTime begin, LocalDateTime end);

  Order createOrder(Map<Long, Integer> booksIds, String clientName, LocalDateTime createDate);

  void cancelOrder(long orderId);

  Double getEarnedSum(LocalDateTime begin, LocalDateTime end);

  Long getCountCompletedOrders(LocalDateTime begin, LocalDateTime end);

  void importOrder(Order order);

  void updateOrders();

  void updateOrder(Order order, LocalDateTime updateDate);
}
