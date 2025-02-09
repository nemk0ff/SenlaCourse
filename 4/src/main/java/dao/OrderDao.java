package dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import model.impl.Order;
import sorting.OrderSort;

/**
 * {@code OrderDao} - Интерфейс, определяющий поведение для (DAO) сущности {@link Order}.
 */
public interface OrderDao {
  void setOrderStatus(long orderId, String status);

  long addOrder(Order order);

  void rewriteOrder(Order order);

  List<Order> getAllOrders(OrderSort sortType, LocalDateTime begin, LocalDateTime end);

  Optional<Order> getOrderById(long orderId);

  Double getEarnedSum(LocalDateTime begin, LocalDateTime end);

  Long getCountCompletedOrders(LocalDateTime begin, LocalDateTime end);
}
