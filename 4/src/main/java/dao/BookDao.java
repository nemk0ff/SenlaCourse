package dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import model.impl.Book;
import sorting.BookSort;

/**
 * {@code BookDao} - Интерфейс, определяющий поведение для (DAO) сущности {@link Book}.
 */
public interface BookDao {
  void add(long bookId, int amount, LocalDateTime deliveredDate) throws IllegalArgumentException;

  void writeOff(long bookId, int amount, LocalDateTime saleDate) throws IllegalArgumentException;

  List<Book> getAllBooks(BookSort sortType);

  List<Book> getBooks(List<Long> bookIds);

  Optional<Book> getBookById(long bookId);

  boolean containsBook(long bookId) throws IllegalArgumentException;

  void importBook(Book book) throws IllegalArgumentException;
}
