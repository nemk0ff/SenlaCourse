package dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import model.impl.Book;
import org.hibernate.Session;
import sorting.BookSort;

/**
 * {@code BookDao} - Интерфейс, определяющий поведение для (DAO) сущности {@link Book}.
 */
public interface BookDao extends GenericDao<Book> {
  void add(Session session, long bookId, int amount, LocalDateTime deliveredDate);

  void writeOff(Session session, long bookId, int amount, LocalDateTime saleDate);

  List<Book> getAllBooks(Session session, BookSort sortType);

  List<Book> getBooks(Session session, List<Long> bookIds);

  Optional<Book> getBookById(Session session, long bookId);

  void importBook(Session session, Book book) throws IllegalArgumentException;
}
