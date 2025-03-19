package ru.bookstore.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import ru.bookstore.model.OrderStatus;
import ru.bookstore.model.impl.Order;
import ru.bookstore.sorting.OrderSort;

public interface OrderDao extends GenericDao<Order> {
  void setOrderStatus(long orderId, OrderStatus status);

  long addOrder(Order order);

  List<Order> getAllOrders(OrderSort sortType, LocalDateTime begin, LocalDateTime end);

  Double getEarnedSum(LocalDateTime begin, LocalDateTime end);

  Long getCountCompletedOrders(LocalDateTime begin, LocalDateTime end);

  Optional<Order> findWithBooks(Long id);
}
