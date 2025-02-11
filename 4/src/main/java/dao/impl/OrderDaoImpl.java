package dao.impl;

import annotations.ComponentDependency;
import config.DatabaseConnection;
import dao.OrderDao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.OrderStatus;
import model.impl.Order;
import sorting.OrderSort;

/**
 * {@code OrderDaoImpl} - Реализация интерфейса {@link OrderDao}, предоставляющая методы для
 * взаимодействия с базой данных для управления информацией о заказах.
 */
@Slf4j
@NoArgsConstructor
public class OrderDaoImpl implements OrderDao {
  @ComponentDependency
  DatabaseConnection databaseConnection;

  @Override
  public void setOrderStatus(long orderId, String status) {
    log.debug("Устанавливаем статус {} для заказа [{}]...", status, orderId);
    Savepoint save = setTransactionSavepoint();

    String updateQuery = "UPDATE orders SET status = ?, completeDate = ? WHERE order_id = ?";
    try (PreparedStatement statement =
             databaseConnection.connection().prepareStatement(updateQuery)) {
      statement.setString(1, status);
      statement.setString(2, LocalDateTime.now().toString());
      statement.setLong(3, orderId);

      log.debug("Выполняем SQL запрос: {} с параметрами: status={}, orderId={}",
          updateQuery, status, orderId);
      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 0) {
        log.warn("Не удалось обновить статус заказа [{}]. "
            + "Возможно, заказ не существует.", orderId);
      }
      databaseConnection.connection().commit();
      log.debug("Статус {} успешно установлен для заказа [{}]", status, orderId);
    } catch (SQLException e) {
      rollbackTransaction(save, e);
      throw new RuntimeException("Не удалось установить статус " + status
          + " для заказа [" + orderId + "]: " + e.getMessage(), e);
    }
  }

  @Override
  public void rewriteOrder(Order order) {
    log.debug("Перезаписываем информацию о заказе: {}", order.getInfoAbout());
    Savepoint save = setTransactionSavepoint();

    String updateQuery = "UPDATE orders SET status = ?, price = ?, orderDate = ?, "
        + "completeDate = ?, clientName = ? WHERE order_id = ?";
    try (PreparedStatement statement =
             databaseConnection.connection().prepareStatement(updateQuery)) {
      statement.setString(1, order.getStatus().toString());
      statement.setDouble(2, order.getPrice());
      statement.setTimestamp(3, Timestamp.valueOf(order.getOrderDate()));
      if (order.getCompleteDate() != null) {
        statement.setTimestamp(4, Timestamp.valueOf(order.getCompleteDate()));
      } else {
        statement.setNull(4, Types.TIMESTAMP);
      }
      statement.setString(5, order.getClientName());
      statement.setLong(6, order.getId());

      log.debug("Выполняем SQL запрос: {} с параметрами: {}", updateQuery, order);
      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 0) {
        log.warn("Не удалось перезаписать информацию о заказе [{}]. "
            + "Возможно, заказ не существует.", order.getId());
      }
      databaseConnection.connection().commit();
      log.debug("Информация о заказе [{}] успешно перезаписана", order.getId());
    } catch (SQLException e) {
      rollbackTransaction(save, e);
      throw new RuntimeException("Не удалось перезаписать информацию о заказе ["
          + order.getId() + "]: " + e.getMessage(), e);
    }
  }

  @Override
  public long addOrder(Order order) {
    log.debug("Добавление заказа в бд: {}...", order.getInfoAbout());
    Savepoint save = setTransactionSavepoint();

    String insertOrderQuery =
        "INSERT INTO orders (status, price, orderDate, completeDate, clientName) "
            + "VALUES (?, ?, ?, ?, ?)";
    String insertOrderedBooksQuery =
        "INSERT INTO ordered_books (order_id, book_id, amount) VALUES (?, ?, ?)";

    try (PreparedStatement orderStatement = databaseConnection.connection()
        .prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS);
         PreparedStatement orderedBooksStatement = databaseConnection.connection()
             .prepareStatement(insertOrderedBooksQuery)) {
      orderStatement.setString(1, order.getStatus().toString());
      orderStatement.setDouble(2, order.getPrice());
      orderStatement.setObject(3, order.getOrderDate());
      orderStatement.setObject(4, order.getCompleteDate());
      orderStatement.setString(5, order.getClientName());

      log.debug("Выполняем SQL запрос: {} с параметрами: "
              + "status={}, price={}, orderDate={}, completeDate={}, clientName={}",
          insertOrderQuery, order.getStatus(), order.getPrice(),
          order.getOrderDate(), order.getCompleteDate(), order.getClientName());

      if (orderStatement.executeUpdate() == 0) {
        throw new RuntimeException("Ошибка бд при добавлении заказа: ни одна строка не изменена");
      }

      long newId;
      try (ResultSet generatedKeys = orderStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          newId = generatedKeys.getLong(1);
          log.debug("Заказ успешно добавлен. Id заказа: {}", newId);
        } else {
          throw new RuntimeException("Ошибка бд при добавлении заказа: "
              + "не удалось получить order_id");
        }
      }

      log.debug("Добавление книг в заказ [{}]", newId);
      for (Map.Entry<Long, Integer> entry : order.getBooks().entrySet()) {
        orderedBooksStatement.setLong(1, newId);
        orderedBooksStatement.setLong(2, entry.getKey());
        orderedBooksStatement.setInt(3, entry.getValue());
        orderedBooksStatement.addBatch();
      }
      int[] batchResults = orderedBooksStatement.executeBatch();
      for (int rowsAffected : batchResults) {
        if (rowsAffected == 0) {
          log.error("Ошибка при добавлении книг в заказ: ни одна строка не изменена");
          throw new RuntimeException("Ошибка бд при обновлении списка заказанных книг: "
              + "ни одна строка не изменена");
        }
      }
      databaseConnection.connection().commit();
      log.info("Заказ с order_id [{}] успешно добавлен", newId);
      return newId;
    } catch (Exception e) {
      rollbackTransaction(save, e);
      throw new RuntimeException("Не удалось добавить заказ: " + e.getMessage(), e);
    }
  }

  @Override
  public List<Order> getAllOrders(OrderSort sortType, LocalDateTime begin, LocalDateTime end) {
    log.debug("Получаем все заказы, отсортированные по {}...", sortType);
    List<Order> allOrders = new ArrayList<>();

    String query = getQuery(sortType, begin, end);
    logQuery(query);

    try (Statement statement = databaseConnection.connection().createStatement();
         ResultSet resultSetOrders = statement.executeQuery(query)) {
      while (resultSetOrders.next()) {
        long orderId = resultSetOrders.getLong("order_id");
        Optional<Order> order = getOrderById(orderId);
        order.ifPresent(allOrders::add);
      }
      log.debug("Успешно получено {} заказов", allOrders.size());
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при получении всех заказов: " + e.getMessage(), e);
    }
    return allOrders;
  }

  private String getQuery(OrderSort sortType, LocalDateTime begin, LocalDateTime end) {
    return switch (sortType) {
      case OrderSort.COMPLETE_DATE -> "SELECT * FROM orders ORDER BY completeDate";
      case OrderSort.PRICE -> "SELECT * FROM orders ORDER BY price";
      case OrderSort.STATUS -> "SELECT * FROM orders ORDER BY status";
      case OrderSort.COMPLETED_BY_DATE -> "SELECT * FROM orders WHERE completeDate >= '" + begin
          + "' AND completeDate <= '" + end + "' ORDER BY completeDate";
      case OrderSort.COMPLETED_BY_PRICE -> "SELECT * FROM orders WHERE completeDate >= '" + begin
          + "' AND completeDate <= '" + end + "' ORDER BY price";
      default -> "SELECT * FROM orders ORDER BY order_id";
    };
  }

  @Override
  public Optional<Order> getOrderById(long orderId) {
    log.debug("Получаем заказ [{}]...", orderId);

    String orderQuery = "SELECT * FROM orders WHERE order_id = " + orderId;
    String orderedBooksQuery = "SELECT * FROM ordered_books WHERE order_id = " + orderId;

    try (Statement OrdersStatement = databaseConnection.connection().createStatement();
         Statement OrderedBooksStatement = databaseConnection.connection().createStatement()) {
      logQuery(orderQuery);
      ResultSet resultOrder = OrdersStatement.executeQuery(orderQuery);

      if (!resultOrder.next()) {
        log.debug("Заказ [{}] не найден", orderId);
        return Optional.empty();
      }

      logQuery(orderedBooksQuery);
      ResultSet orderedBooks = OrderedBooksStatement
          .executeQuery(orderedBooksQuery);

      return getOrder(resultOrder, orderedBooks);
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при получении заказа ["
          + orderId + "]: " + e.getMessage(), e);
    }
  }

  @Override
  public Double getEarnedSum(LocalDateTime begin, LocalDateTime end) {
    log.info("Получаем заработанную сумму за период с {} по {}...", begin, end);

    String query = "SELECT SUM(price) FROM orders WHERE completeDate >= '"
        + begin + "' AND completeDate <= '" + end + "'";

    try (Statement statement = databaseConnection.connection().createStatement();
         ResultSet result = statement.executeQuery(query)) {
      logQuery(query);
      result.next();
      Double sum = result.getDouble(1);
      log.info("Заработанная сумма за период: {}", sum);
      return sum;
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при получении заработанной суммы за период с "
          + begin + " по " + end + " : " + e.getMessage(), e);
    }
  }

  @Override
  public Long getCountCompletedOrders(LocalDateTime begin, LocalDateTime end) {
    log.info("Получаем количество завершенных заказов за период с {} по {}...", begin, end);
    String query = "SELECT COUNT(*) FROM orders WHERE completeDate >= '"
        + begin + "' AND completeDate <= '" + end + "'";

    try (Statement statement = databaseConnection.connection().createStatement();
         ResultSet result = statement.executeQuery(query)) {

      logQuery(query);
      result.next();
      Long count = result.getLong(1);
      log.info("Количество завершенных заказов за период: {}", count);
      return count;
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при получении количества завершенных заказов "
          + "за период с " + begin + " по " + end + " : " + e.getMessage(), e);
    }
  }

  private Optional<Order> getOrder(ResultSet resultOrder, ResultSet orderedBooks) {
    Map<Long, Integer> books = new HashMap<>();
    try {
      while (orderedBooks.next()) {
        Long bookId = orderedBooks.getLong(2);
        Integer amount = orderedBooks.getInt(3);
        books.put(bookId, amount);
        log.debug("Добавлена книга в заказ: bookId={}, amount={}", bookId, amount);
      }
      Order order = new Order(resultOrder.getLong(1),
          getStatusFromString(resultOrder.getString(2)),
          resultOrder.getDouble(3),
          resultOrder.getObject(4, LocalDateTime.class),
          resultOrder.getObject(5, LocalDateTime.class),
          resultOrder.getString(6),
          books);
      log.debug("Заказ успешно сформирован из ResultSet: {}", order.getInfoAbout());
      return Optional.of(order);

    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при формировании заказа из ResultSet: " + e.getMessage());
    }
  }

  private OrderStatus getStatusFromString(String input) {
    for (OrderStatus status : OrderStatus.values()) {
      if (status.name().equalsIgnoreCase(input)) {
        return status;
      }
    }
    log.warn("Не удалось найти OrderStatus для строки {}", input);
    return null;
  }

  private void logQuery(String query) {
    log.debug("Выполняем SQL запрос: {}", query);
  }

  private void rollbackTransaction(Savepoint savepoint, Exception e) {
    log.warn("Откатываем транзакцию из-за SQLException: {}...", e.getMessage());
    try {
      if (savepoint != null) {
        databaseConnection.connection().rollback(savepoint);
        log.debug("Транзакция успешно отменена до точки сохранения");
      } else {
        log.warn("Точка сохранения не была установлена, откат невозможен");
        databaseConnection.connection().rollback();
      }
    } catch (Exception ex) {
      throw new RuntimeException("Ошибка при откате транзакции после SQLException: "
          + e.getMessage(), ex);
    }
  }

  private Savepoint setTransactionSavepoint() {
    log.debug("Установка точки сохранения транзакции...");
    Savepoint savepoint;
    try {
      savepoint = databaseConnection.connection().setSavepoint();
      log.debug("Точка сохранения успешно создана");
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при создании точки сохранения", e);
    }
    return savepoint;
  }
}
