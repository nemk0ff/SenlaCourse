package dao.impl;

import annotations.ConfigProperty;
import config.ConfigurationManager;
import dao.BookDao;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import model.impl.Book;
import org.hibernate.Session;
import org.hibernate.query.Query;
import sorting.BookSort;

/**
 * {@code BookDaoImpl} - Реализация интерфейса {@link BookDao}, предоставляющая методы для
 * взаимодействия с базой данных для управления информацией о книгах.
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class BookDaoImpl extends HibernateAbstractDao<Book> implements BookDao {
  @ConfigProperty(propertyName = "book.stale.months", type = int.class)
  private int staleBookMonths;

  public BookDaoImpl() {
    super(Book.class);
    ConfigurationManager.configure(this);
    log.debug("BookDaoImpl инициализирован, staleBookMonths = {}", staleBookMonths);
  }

  @Override
  public boolean containsBook(Session session, long bookId) {
    log.debug("Проверяем, существует ли книга [{}]...", bookId);
    boolean contains = getBookById(session, bookId).isPresent();
    log.debug("Книга [{}] существует: {}.", bookId, contains);
    return contains;
  }

  @Override
  public void add(Session session, long bookId, int amount, LocalDateTime deliveredDate)
      throws IllegalArgumentException {
    log.debug("Добавляем {} книг [{}]...", amount, bookId);
    if (amount < 0) {
      throw new IllegalArgumentException("Количество добавленных книг должно быть положительным");
    }
    try {
      Book book = session.get(Book.class, bookId);
      if (book == null) {
        throw new IllegalArgumentException("Такой книги нет в магазине");
      }
      book.setAmount(book.getAmount() + amount);
      book.setLastDeliveredDate(deliveredDate);

      session.merge(book);
      log.info("Успешно добавлено {} книг [{}], дата поставки: {}", amount, bookId, deliveredDate);
    } catch (Exception e) {
      throw new RuntimeException("Не удалось добавить книги [" + bookId + "]: " + e.getMessage(), e);
    }
  }

  @Override
  public void writeOff(Session session, long bookId, int amount, LocalDateTime saleDate)
      throws IllegalArgumentException {
    log.info("Списываем {} книг [{}]...", amount, bookId);
    if (amount <= 0) {
      throw new IllegalArgumentException("Попытка списать неположительное количество (" + amount + ") книг [" + bookId + "]");
    }
    try {
      Book book = session.get(Book.class, bookId);
      if (book == null) {
        throw new IllegalArgumentException("Попытка списать книги с несуществующим id: " + bookId);
      }
      if (book.getAmount() < amount) {
        throw new IllegalArgumentException("Попытка списать " + amount + " книг [" + bookId + "], но доступно только " + book.getAmount());
      }
      book.setAmount(book.getAmount() - amount);
      book.setLastSaleDate(saleDate);

      session.merge(book);
      log.info("Списано {} книг [{}], дата продажи: {}", amount, bookId, saleDate);
    } catch (Exception e) {
      throw new RuntimeException("Не удалось списать книги [" + bookId + "]: " + e.getMessage(), e);
    }
  }

  @Override
  public List<Book> getAllBooks(Session session, BookSort sortType) {
    log.debug("Получаем все книги, отсортированные по {}...", sortType);
    try {
      Query<Book> query = session.createQuery(getQuery(sortType), Book.class);
      if (sortType == BookSort.STALE_BY_DATE || sortType == BookSort.STALE_BY_PRICE) {
        query.setParameter("staleBookMonths", staleBookMonths);
      }
      List<Book> books = query.list();
      log.info("Успешно получено {} книг, отсортированных по {}", books.size(), sortType);
      return books;
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при получении всех книг: " + e.getMessage(), e);
    }
  }

  private String getQuery(BookSort sortType) {
    return switch (sortType) {
      case ID -> "FROM Book ORDER BY id";
      case NAME -> "FROM Book ORDER BY name";
      case PUBLICATION_DATE -> "FROM Book ORDER BY publicationDate";
      case PRICE -> "FROM Book ORDER BY price";
      case STATUS -> "FROM Book ORDER BY status";
      case STALE_BY_DATE -> "FROM Book WHERE "
          + "(lastSaleDate IS NULL AND DATEDIFF(CURRENT_DATE, lastDeliveredDate) >= "
          + ":staleBookMonths) OR (lastSaleDate IS NOT NULL AND DATEDIFF(CURRENT_DATE, "
          + "lastSaleDate) >= :staleBookMonths) ORDER BY lastDeliveredDate";
      case STALE_BY_PRICE -> "FROM Book WHERE "
          + "(lastSaleDate IS NULL AND DATEDIFF(CURRENT_DATE, lastDeliveredDate) >= " +
          ":staleBookMonths) OR (lastSaleDate IS NOT NULL AND DATEDIFF(CURRENT_DATE,"
          + "lastSaleDate) >= :staleBookMonths) ORDER BY price";
    };
  }

  @Override
  public List<Book> getBooks(Session session, List<Long> bookIds) {
    log.debug("Получаем книги [{}]...", bookIds);
    try {
      List<Book> books = session
          .createQuery("FROM Book WHERE id IN (:bookIds)", Book.class)
          .setParameterList("bookIds", bookIds)
          .list();
      log.debug("Успешно получено {} книг [{}]", books.size(), bookIds);
      return books;
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при получении книг ["
          + bookIds + "]: " + e.getMessage(), e);
    }
  }

  @Override
  public void importBook(Session session, Book book) {
    log.info("Импортируем книгу: {}...", book.getInfoAbout());
    try {
      session.merge(book);
      log.info("Книга успешно импортирована: {}", book.getInfoAbout());
    } catch (Exception e) {
      throw new RuntimeException("Не удалось импортировать книгу: " + book.getInfoAbout(), e);
    }
  }

  @Override
  public Optional<Book> getBookById(Session session, long bookId) {
    return find(session, bookId);
  }
}
