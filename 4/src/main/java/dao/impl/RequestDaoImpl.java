package dao.impl;

import annotations.ComponentDependency;
import config.DatabaseConnection;
import dao.RequestDao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.RequestStatus;
import model.impl.Request;
import sorting.RequestSort;

/**
 * {@code RequestDaoImpl} - Реализация интерфейса {@link RequestDao}, предоставляющая методы для
 * взаимодействия с базой данных для управления информацией о запросах.
 */
@Slf4j
@NoArgsConstructor
public class RequestDaoImpl implements RequestDao {
  @ComponentDependency
  DatabaseConnection databaseConnection;

  @Override
  public List<Request> getAllRequests(RequestSort typeSort) {
    log.info("Получаем все запросы, отсортированные по {}...", typeSort);
    List<Request> requests = new ArrayList<>();

    String query = getQuery(typeSort);
    logQuery(query);

    try (PreparedStatement preparedStatement =
             databaseConnection.connection().prepareStatement(query)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        getRequest(resultSet).ifPresent(requests::add);
      }
      log.info("Успешно получено {} запросов", requests.size());
    } catch (SQLException e) {
      log.error("Ошибка при получении всех запросов: {}", e.getMessage(), e);
      throw new RuntimeException("Ошибка при получении всех запросов: " + e.getMessage(), e);
    }
    return requests;
  }

  @Override
  public LinkedHashMap<Long, Long> getRequests(RequestSort typeSort) {
    log.debug("Получаем запросы, отсортированные по {}...", typeSort);
    LinkedHashMap<Long, Long> requests = new LinkedHashMap<>();

    String query = getQuery(typeSort);
    logQuery(query);

    try (PreparedStatement preparedStatement =
             databaseConnection.connection().prepareStatement(query)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        requests.put(resultSet.getLong(1), resultSet.getLong(2));
      }
      log.debug("Успешно получено {} запросов", requests.size());
    } catch (SQLException e) {
      log.error("Ошибка при получении запросов в виде LinkedHashMap: {}", e.getMessage(), e);
      return new LinkedHashMap<>();
    }
    return requests;
  }

  private String getQuery(RequestSort sortType) {
    return switch (sortType) {
      case RequestSort.ID -> "SELECT * FROM requests ORDER BY request_id";
      case RequestSort.COUNT -> "SELECT book_id, COUNT(*) as count FROM requests "
          + "WHERE status = 'OPEN' GROUP BY book_id ORDER BY count";
      case RequestSort.PRICE -> "SELECT l.book_id, COUNT(*) FROM requests r JOIN library l "
          + "on r.book_id = l.book_id WHERE r.status = 'OPEN' GROUP BY l.book_id ORDER BY l.price";
    };
  }

  @Override
  public Optional<Request> getRequestById(long requestId) {
    log.debug("Получаем запрос [{}]...", requestId);
    String query = "SELECT * FROM requests WHERE request_id = " + requestId;
    try (Statement statement = databaseConnection.connection().createStatement()) {
      ResultSet results = statement.executeQuery(query);
      logQuery(query);
      if (!results.next()) {
        log.warn("Запрос [{}] не найден", requestId);
        return Optional.empty();
      }
      return getRequest(results);
    } catch (SQLException e) {
      log.error("Ошибка при получении запроса [{}]: {}", requestId, e.getMessage(), e);
      return Optional.empty();
    }
  }

  @Override
  public Optional<Request> getRequestByBook(long bookId, int amount) {
    log.debug("Получаем запрос по bookId={} и amount={}...", bookId, amount);
    String query = "SELECT * FROM requests WHERE book_id = " + bookId + " AND amount = " + amount
        + " LIMIT 1";
    try (Statement statement = databaseConnection.connection().createStatement()) {
      logQuery(query);
      ResultSet results = statement.executeQuery(query);
      if (!results.next()) {
        log.warn("Запрос по bookId={} и amount={} не найден", bookId, amount);
        return Optional.empty();
      }
      return getRequest(results);
    } catch (SQLException e) {
      log.error("Ошибка при получении запроса по bookId={} и amount={}: {}",
          bookId, amount, e.getMessage(), e);
      return Optional.empty();
    }
  }

  @Override
  public long addRequest(long bookId, int amount) {
    log.debug("Добавляем запрос для bookId={} и amount={}", bookId, amount);
    String insertOrderQuery = "INSERT INTO requests (book_id, amount, status) VALUES (?, ?, ?)";
    long generatedId;

    try (PreparedStatement orderStatement =
             databaseConnection.connection().prepareStatement(insertOrderQuery,
                 Statement.RETURN_GENERATED_KEYS)) {
      orderStatement.setLong(1, bookId);
      orderStatement.setInt(2, amount);
      orderStatement.setString(3, "OPEN");

      log.debug("Выполняем SQL запрос: {} с параметрами: bookId={}, amount={}, status=OPEN",
          insertOrderQuery, bookId, amount);

      if (orderStatement.executeUpdate() == 0) {
        String errorMessage = "Ошибка БД при создании запроса: ни одна строка не изменена";
        log.error("{} bookId={}, amount={}", errorMessage, bookId, amount);
        throw new RuntimeException(errorMessage);
      }

      try (ResultSet generatedKeys = orderStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          generatedId = generatedKeys.getLong(1);
          log.debug("Сгенерирован id для нового запроса: {}", generatedId);
        } else {
          String errorMessage = "Ошибка БД: не удалось получить сгенерированный id";
          log.error(errorMessage);
          throw new RuntimeException(errorMessage);
        }
      }

      databaseConnection.connection().commit();
      log.debug("Успешно добавлен запрос для bookId={} и amount={} с id={}",
          bookId, amount, generatedId);
    } catch (SQLException e) {
      rollbackTransaction(e);
      log.error("Не удалось добавить запрос для bookId={} и amount={}: {}",
          bookId, amount, e.getMessage(), e);
      throw new RuntimeException(e);
    }

    return generatedId;
  }

  @Override
  public void importRequest(Request request) {
    log.debug("Импортируем запрос: {}...", request);
    String query = "INSERT INTO requests (book_id, amount, status) VALUES (?, ?, ?) ";

    try (PreparedStatement preparedStatement =
             databaseConnection.connection().prepareStatement(query)) {
      preparedStatement.setLong(1, request.getBookId());
      preparedStatement.setInt(2, request.getAmount());
      preparedStatement.setString(3, request.getStatus().toString());

      log.debug("Выполняем SQL запрос: {} с параметрами: bookId={}, amount={}, status={}",
          query, request.getBookId(), request.getAmount(), request.getStatus());
      if (preparedStatement.executeUpdate() == 0) {
        String errorMessage = "Ошибка БД при импорте запроса: ни одна строка не изменена";
        log.error("{} request={}", errorMessage, request);
        throw new RuntimeException(errorMessage);
      }
      databaseConnection.connection().commit();
      log.debug("Запрос успешно импортирован: {}", request);
    } catch (SQLException e) {
      rollbackTransaction(e);
      log.error("Не удалось импортировать запрос: {}", request, e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void closeRequest(long requestId) {
    log.info("Закрываем запрос с id: {}...", requestId);
    String updateQuery = "UPDATE requests SET status = ? WHERE request_id = ?";
    try (PreparedStatement statement =
             databaseConnection.connection().prepareStatement(updateQuery)) {
      statement.setString(1, "CLOSED");
      statement.setLong(2, requestId);
      log.debug("Выполняем SQL запрос: {} с параметрами: status=CLOSED, requestId={}",
          updateQuery, requestId);

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 0) {
        String errorMessage = "Не удалось закрыть запрос [" + requestId + "]: запрос не найден";
        log.warn(errorMessage);
        throw new IllegalArgumentException(errorMessage);
      }
      databaseConnection.connection().commit();
      log.info("Запрос [{}] успешно закрыт.", requestId);
    } catch (SQLException e) {
      rollbackTransaction(e);
      log.error("Не удалось закрыть запрос [{}]: {}", requestId, e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void closeRequests(Map<Long, Integer> books) {
    log.info("Закрываем запросы для книг: {}...", books);
    for (Map.Entry<Long, Integer> book : books.entrySet()) {
      Optional<Request> optionalRequest = getRequestByBook(book.getKey(), book.getValue());
      if (optionalRequest.isPresent()) {
        Request request = optionalRequest.get();
        log.debug("Закрываем запрос [{}] для bookId={} и amount={}",
            request.getId(), book.getKey(), book.getValue());
        closeRequest(request.getId());
      } else {
        log.warn("Не найден запрос для bookId {} и amount {}, пропуск закрытия",
            book.getKey(), book.getValue());
      }
    }
    log.info("Запросы для книг {} успешно закрыты.", books);
  }

  private Optional<Request> getRequest(ResultSet resultOrder) {
    try {
      Request request = new Request(resultOrder.getLong(1),
          resultOrder.getLong(2),
          resultOrder.getInt(3),
          getStatusFromString(resultOrder.getString(4)));
      log.debug("Запрос успешно создан из ResultSet: {}", request);
      return Optional.of(request);
    } catch (SQLException e) {
      log.warn("Ошибка при создании запроса из ResultSet: {}", e.getMessage());
      return Optional.empty();
    }
  }

  private RequestStatus getStatusFromString(String input) {
    for (RequestStatus status : RequestStatus.values()) {
      if (status.name().equalsIgnoreCase(input)) {
        return status;
      }
    }
    log.warn("Не удалось найти RequestStatus для строки {}", input);
    return null;
  }

  private void logQuery(String query) {
    log.debug("Выполняем SQL запрос: {}", query);
  }

  private void rollbackTransaction(SQLException e) {
    log.warn("Откатываем транзакцию из-за SQLException: {}", e.getMessage());
    try {
      databaseConnection.connection().rollback();
    } catch (SQLException ex) {
      log.error("Ошибка при откате транзакции: {}", ex.getMessage(), ex);
      throw new RuntimeException(ex);
    }
  }
}
