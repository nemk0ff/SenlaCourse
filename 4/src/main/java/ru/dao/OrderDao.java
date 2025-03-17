package ru.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import ru.model.OrderStatus;
import ru.model.impl.Order;
import ru.sorting.OrderSort;

public interface OrderDao extends GenericDao<Order> {
  void setOrderStatus(long orderId, OrderStatus status);

  long addOrder(Order order);

  List<Order> getAllOrders(OrderSort sortType, LocalDateTime begin, LocalDateTime end);

  Double getEarnedSum(LocalDateTime begin, LocalDateTime end);

  Long getCountCompletedOrders(LocalDateTime begin, LocalDateTime end);

  Optional<Order> findWithBooks(Long id);
}
