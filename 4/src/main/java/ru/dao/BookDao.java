package ru.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import ru.model.impl.Book;
import ru.sorting.BookSort;

public interface BookDao extends GenericDao<Book> {
  void add(long bookId, int amount, LocalDateTime deliveredDate);

  void writeOff(long bookId, int amount, LocalDateTime saleDate);

  List<Book> getAllBooks(BookSort sortType);

  List<Book> getBooks(List<Long> bookIds);

  Optional<Book> getBookById(long bookId);

  void importBook(Book book) throws IllegalArgumentException;
}
