package dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import model.OrderStatus;
import model.impl.Order;
import org.hibernate.Session;
import sorting.OrderSort;

/**
 * {@code OrderDao} - Интерфейс, определяющий поведение для (DAO) сущности {@link Order}.
 */
public interface OrderDao {
  void setOrderStatus(Session session, long orderId, OrderStatus status);

  long addOrder(Session session, Order order);

  List<Order> getAllOrders(Session session, OrderSort sortType, LocalDateTime begin, LocalDateTime end);

  Double getEarnedSum(Session session, LocalDateTime begin, LocalDateTime end);

  Long getCountCompletedOrders(Session session, LocalDateTime begin, LocalDateTime end);

  Optional<Order> findWithBooks(Session session, Long id);
}
