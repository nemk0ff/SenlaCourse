package dao.impl;

import annotations.ComponentDependency;
import annotations.ConfigProperty;
import config.ConfigurationManager;
import config.DatabaseConnection;
import dao.BookDao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import model.impl.Book;
import sorting.BookSort;

/**
 * {@code BookDaoImpl} - Реализация интерфейса {@link BookDao}, предоставляющая методы для
 * взаимодействия с базой данных для управления информацией о книгах.
 */
@Slf4j
@Data
public class BookDaoImpl implements BookDao {
  @ConfigProperty(propertyName = "book.stale.months", type = int.class)
  private int staleBookMonths;
  @ComponentDependency
  DatabaseConnection databaseConnection;

  public BookDaoImpl() {
    ConfigurationManager.configure(this);
    log.debug("BookDaoImpl инициализирован, staleBookMonths = {}", staleBookMonths);
  }

  @Override
  public boolean containsBook(long bookId) {
    log.debug("Проверяем, существует ли книга [{}]...", bookId);
    boolean contains = getBookById(bookId).isPresent();
    log.debug("Книга [{}] существует: {}.", bookId, contains);
    return contains;
  }

  @Override
  public void add(long bookId, int amount, LocalDateTime deliveredDate)
      throws IllegalArgumentException {
    log.debug("Добавляем {} книг [{}]...", amount, bookId);
    Optional<Book> ourBook = getBookById(bookId);
    if (ourBook.isEmpty()) {
      throw new IllegalArgumentException("Такой книги нет в магазине");
    } else if (amount < 0) {
      throw new IllegalArgumentException("Количество добавленных книг должно быть положительным");
    }

    int newAmount = ourBook.get().getAmount() + amount;
    try {
      setAmount(bookId, newAmount, "lastDeliveredDate", deliveredDate);
      log.info("Успешно добавлено {} книг [{}], дата поставки: {}",
          amount, bookId, deliveredDate);
    } catch (SQLException e) {
      throw new RuntimeException("Не удалось установить количество для книги ["
          + bookId + "] : " + e.getMessage(), e);
    }
  }

  @Override
  public void writeOff(long bookId, int amount, LocalDateTime saleDate)
      throws IllegalArgumentException {
    log.info("Списываем {} книг [{}]...", amount, bookId);
    Optional<Book> ourBook = getBookById(bookId);
    if (ourBook.isEmpty()) {
      throw new IllegalArgumentException("Попытка списать книги с несуществующим id: " + bookId);
    } else if (ourBook.get().getAmount() < amount) {
      throw new IllegalArgumentException("Попытка списать " + amount + " книг [" + bookId
          + "],но доступно только " + ourBook.get().getAmount());
    } else if (amount <= 0) {
      throw new IllegalArgumentException("Попытка списать неположительное количество ("
          + amount + ") книг [" + bookId + "]");
    }

    int newAmount = ourBook.get().getAmount() - amount;
    try {
      setAmount(bookId, newAmount, "lastSaleDate", saleDate);
      log.info("Списано {} книг [{}], дата продажи: {}", amount, bookId, saleDate);
    } catch (SQLException e) {
      throw new RuntimeException("Не удалось установить количество для книги ["
          + bookId + "]: " + e.getMessage(), e);
    }
  }

  private void setAmount(long bookId, int amount, String dateType, LocalDateTime dateSet)
      throws SQLException {
    log.debug("Устанавливаем количество {} для книги [{}], тип даты {}, дата установки {}...",
        amount, bookId, dateType, dateSet);

    String query = "UPDATE library SET amount = ?, " + dateType + " = ? WHERE book_id = ?";
    try (PreparedStatement preparedStatement = databaseConnection.connection()
        .prepareStatement(query)) {

      preparedStatement.setInt(1, amount);
      preparedStatement.setTimestamp(2, Timestamp.valueOf(dateSet));
      preparedStatement.setLong(3, bookId);

      logQuery(query);
      if (preparedStatement.executeUpdate() == 0) {
        throw new SQLException("Ошибка БД при обновлении количества книг в базе данных: "
            + "ни одна строка не изменена. bookId=" + bookId + ", amount=" + amount
            + ", dateType=" + dateType + ", dateSet=" + dateSet);
      }
      databaseConnection.connection().commit();
      log.debug("Успешно обновлено количество книг [{}]. Теперь на складе {} книг",
          bookId, amount);
    } catch (SQLException e) {
      rollbackTransaction(e);
      throw new RuntimeException("Не удалось установить количество для книги ["
          + bookId + "]: " + e.getMessage(), e);
    }
  }

  @Override
  public List<Book> getAllBooks(BookSort sortType) {
    log.debug("Получаем все книги, отсортированные по {}...", sortType);
    List<Book> books = new ArrayList<>();

    String query = getQuery(sortType);
    logQuery(query);

    try (PreparedStatement preparedStatement =
             databaseConnection.connection().prepareStatement(query)) {
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        getBook(resultSet).ifPresent(books::add);
      }
      log.info("Успешно получено {} книг, отсортированных по {}", books.size(), sortType);
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при получении всех книг: " + e.getMessage(), e);
    }
    return books;
  }

  private String getQuery(BookSort sortType) {
    return switch (sortType) {
      case BookSort.ID -> "SELECT * FROM library ORDER BY book_id";
      case BookSort.NAME -> "SELECT * FROM library ORDER BY name";
      case BookSort.PUBLICATION_DATE -> "SELECT * FROM library ORDER BY publicationDate";
      case BookSort.PRICE -> "SELECT * FROM library ORDER BY price";
      case BookSort.STATUS -> "SELECT * FROM library ORDER BY status";
      case STALE_BY_DATE -> "SELECT * FROM library WHERE "
          + "(lastSaleDate IS NULL AND DATEDIFF(CURDATE(), lastDeliveredDate) >= "
          + staleBookMonths + ") "
          + "OR (lastSaleDate IS NOT NULL AND DATEDIFF(CURDATE(), lastSaleDate) >= "
          + staleBookMonths + ") "
          + "ORDER BY lastDeliveredDate";
      case STALE_BY_PRICE -> "SELECT * FROM library WHERE "
          + "(lastSaleDate IS NULL AND DATEDIFF(CURDATE(), lastDeliveredDate) >= "
          + staleBookMonths + ") "
          + "OR (lastSaleDate IS NOT NULL AND DATEDIFF(CURDATE(), lastSaleDate) >= "
          + staleBookMonths + ") "
          + "ORDER BY price";
    };
  }

  @Override
  public List<Book> getBooks(List<Long> bookIds) {
    log.debug("Получаем книги [{}]...", bookIds);

    List<Book> books = new ArrayList<>();

    StringBuilder partOfQuery = new StringBuilder();
    for (int i = 0; i < bookIds.size(); i++) {
      partOfQuery.append("?");
      if (i < bookIds.size() - 1) {
        partOfQuery.append(",");
      }
    }

    String query = "SELECT * FROM library WHERE book_id IN (" + partOfQuery + ")";
    try (PreparedStatement statement = databaseConnection.connection()
        .prepareStatement(query)) {
      for (int i = 0; i < bookIds.size(); i++) {
        statement.setLong(i + 1, bookIds.get(i));
      }
      logQuery(query);
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        getBook(resultSet).ifPresent(books::add);
      }
      log.debug("Успешно получено {} книг [{}]", books.size(), bookIds);
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при получении книг ["
          + bookIds + "]: " + e.getMessage(), e);
    }
    return books;
  }

  @Override
  public void importBook(Book book) throws IllegalArgumentException {
    log.info("Импортируем книгу: {}...", book.getInfoAbout());
    String query = "INSERT INTO library (book_id, name, author, publicationDate, "
        + "amount, price, lastDeliveredDate, lastSaleDate, status) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " + "AS new_values "
        + "ON DUPLICATE KEY UPDATE " + "name = new_values.name, "
        + "author = new_values.author, " + "publicationDate = new_values.publicationDate, "
        + "amount = new_values.amount, " + "price = new_values.price, "
        + "lastDeliveredDate = new_values.lastDeliveredDate, "
        + "lastSaleDate = new_values.lastSaleDate, " + "status = new_values.status";

    try (PreparedStatement preparedStatement =
             databaseConnection.connection().prepareStatement(query)) {

      preparedStatement.setLong(1, book.getId());
      preparedStatement.setString(2, book.getName());
      preparedStatement.setString(3, book.getAuthor());
      preparedStatement.setInt(4, book.getPublicationDate());
      preparedStatement.setInt(5, book.getAmount());
      preparedStatement.setDouble(6, book.getPrice());
      preparedStatement.setString(7, book.getLastDeliveredDate().toString());
      preparedStatement.setObject(8, book.getLastSaleDate());
      preparedStatement.setString(9, book.getStatus().toString());

      logQuery(query);
      if (preparedStatement.executeUpdate() == 0) {
        throw new SQLException("Ошибка БД при импорте/обновлении книги: "
            + "не удалось изменить ни одну строку. book=" + book.getInfoAbout());
      }
      databaseConnection.connection().commit();
      log.info("Книга успешно импортирована: {}", book.getInfoAbout());
    } catch (SQLException e) {
      rollbackTransaction(e);
      throw new RuntimeException("Не удалось импортировать книгу: " + book.getInfoAbout(), e);
    }
  }

  @Override
  public Optional<Book> getBookById(long bookId) {
    log.debug("Получаем книгу по id: {}...", bookId);
    String query = "SELECT * FROM library WHERE book_id = " + bookId;
    try (Statement statement = databaseConnection.connection().createStatement()) {
      logQuery(query);
      ResultSet results = statement.executeQuery(query);
      if (!results.next()) {
        log.debug("Книга [{}] не найдена", bookId);
        return Optional.empty();
      }
      return getBook(results);
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при получении книги по id "
          + bookId + " :" + e.getMessage(), e);
    }
  }

  private Optional<Book> getBook(ResultSet results) {
    try {
      Book book = new Book(results.getLong(1),
          results.getString(2),
          results.getString(3),
          results.getInt(4),
          results.getInt(5),
          results.getDouble(6),
          results.getObject(7, LocalDateTime.class),
          results.getObject(8, LocalDateTime.class),
          Book.getStatusFromString(results.getString(9), results.getInt(5)));
      log.debug("Книга успешно извлечена из ResultSet: {}", book);
      return Optional.of(book);
    } catch (SQLException e) {
      throw new RuntimeException("Ошибка при извлечении книги из ResultSet: " + e.getMessage(), e);
    }
  }

  private void logQuery(String query) {
    log.debug("Выполняем SQL запрос: {}", query);
  }

  private void rollbackTransaction(Exception e) {
    log.warn("Откатываем транзакцию из-за SQLException: {}...", e.getMessage());
    try {
      databaseConnection.connection().rollback();
      log.debug("Транзакция успешно отменена");
    } catch (Exception ex) {
      throw new RuntimeException("Ошибка при откате транзакции после SQLException: "
          + e.getMessage(), ex);
    }
  }
}
