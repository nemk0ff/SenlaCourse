package dao.impl;

import dao.OrderDao;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import model.OrderStatus;
import model.impl.Order;
import org.hibernate.Session;
import org.hibernate.query.Query;
import sorting.OrderSort;

/**
 * {@code OrderDaoImpl} - Реализация интерфейса {@link OrderDao}, предоставляющая методы для
 * взаимодействия с базой данных для управления информацией о заказах.
 */
@Slf4j
public class OrderDaoImpl extends HibernateAbstractDao<Order> implements OrderDao {

  public OrderDaoImpl() {
    super(Order.class);
    log.debug("OrderDaoImpl инициализирован");
  }

  @Override
  public void setOrderStatus(Session session, long orderId, OrderStatus status) {
    log.debug("Устанавливаем статус {} для заказа [{}]...", status, orderId);

    Optional<Order> orderOptional = find(session, orderId);
    if (orderOptional.isEmpty()) {
      throw new IllegalArgumentException("Заказ с id " + orderId + " не найден");
    }
    Order order = orderOptional.get();
    order.setStatus(status);
    order.setCompleteDate(LocalDateTime.now());

    update(session, order);
    log.debug("Статус {} успешно установлен для заказа [{}]", status, orderId);
  }

  @Override
  public long addOrder(Session session, Order order) {
    log.debug("Добавление заказа в бд: {}...", order.getInfoAbout());
    try {
      session.persist(order);
      log.info("Заказ с order_id [{}] успешно добавлен", order.getId());
      return order.getId();
    } catch (Exception e) {
      throw new RuntimeException("Не удалось добавить заказ: " + e.getMessage(), e);
    }
  }

  @Override
  public List<Order> getAllOrders(Session session, OrderSort sortType,
                                  LocalDateTime begin, LocalDateTime end) {
    log.debug("Получаем все заказы, отсортированные по {}...", sortType);
    try {
      Query<Order> query = session.createQuery(getQuery(sortType), Order.class);
      if (sortType == OrderSort.COMPLETED_BY_DATE || sortType == OrderSort.COMPLETED_BY_PRICE) {
        query.setParameter("begin", begin);
        query.setParameter("end", end);
      }
      List<Order> orders = query.list();
      log.info("Успешно получено {} заказов, отсортированных по {}", orders.size(), sortType);
      return orders;
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при получении всех заказов: " + e.getMessage(), e);
    }
  }

  private String getQuery(OrderSort sortType) {
    String baseQuery = "SELECT DISTINCT o FROM Order o";
    return switch (sortType) {
      case COMPLETE_DATE -> baseQuery + " LEFT JOIN FETCH o.books ORDER BY o.completeDate";
      case PRICE -> baseQuery + " LEFT JOIN FETCH o.books ORDER BY o.price";
      case STATUS -> baseQuery + " LEFT JOIN FETCH o.books ORDER BY o.status";
      case COMPLETED_BY_DATE -> baseQuery
          + " LEFT JOIN FETCH o.books WHERE o.completeDate >= :begin AND o.completeDate <= "
          + ":end ORDER BY o.completeDate";
      case COMPLETED_BY_PRICE -> baseQuery
          + " LEFT JOIN FETCH o.books WHERE o.completeDate >= :begin AND o.completeDate <= "
          + ":end ORDER BY o.price";
      default -> baseQuery + " LEFT JOIN FETCH o.books ORDER BY o.id";
    };
  }

  @Override
  public Double getEarnedSum(Session session, LocalDateTime begin, LocalDateTime end) {
    log.info("Получаем заработанную сумму за период с {} по {}...", begin, end);
    String hql = "SELECT SUM(price) FROM Order "
        + "WHERE completeDate >= :begin AND completeDate <= :end";
    try {
      Double sum = session.createQuery(hql, Double.class)
          .setParameter("begin", begin)
          .setParameter("end", end)
          .uniqueResult();
      log.info("Заработанная сумма за период: {}", sum);
      return sum;
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при получении заработанной суммы за период с "
          + begin + " по " + end + " : " + e.getMessage(), e);
    }
  }

  @Override
  public Long getCountCompletedOrders(Session session, LocalDateTime begin, LocalDateTime end) {
    log.info("Получаем количество завершенных заказов за период с {} по {}...", begin, end);
    String hql = "SELECT COUNT(*) FROM Order WHERE completeDate >= :begin AND completeDate <= :end";
    try {
      Long count = session.createQuery(hql, Long.class)
          .setParameter("begin", begin)
          .setParameter("end", end)
          .uniqueResult();
      log.info("Количество завершенных заказов за период: {}", count);
      return count;
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при получении количества завершенных заказов "
          + "за период с " + begin + " по " + end + " : " + e.getMessage(), e);
    }
  }

  @Override
  public Optional<Order> findWithBooks(Session session, Long id) {
    log.debug("Поиск заказа [{}] с книгами...", id);
    try {
      String hql = "SELECT o FROM Order o LEFT JOIN FETCH o.books WHERE o.id = :id";
      Order order = session.createQuery(hql, Order.class)
          .setParameter("id", id)
          .uniqueResultOptional()
          .orElse(null);

      if (order == null) {
        log.debug("Заказ [{}] не найден.", id);
        return Optional.empty();
      } else {
        log.debug("Заказ [{}] найден: {}", id, order.getInfoAbout());
        return Optional.of(order);
      }
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при поиске заказа с id " + id + ": " + e.getMessage(), e);
    }
  }
}
