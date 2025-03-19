package ru.bookstore.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import ru.bookstore.model.impl.Book;
import ru.bookstore.sorting.BookSort;

public interface BookDao extends GenericDao<Book> {
  void add(long bookId, int amount, LocalDateTime deliveredDate);

  void writeOff(long bookId, int amount, LocalDateTime saleDate);

  List<Book> getAllBooks(BookSort sortType);

  List<Book> getBooks(List<Long> bookIds);

  Optional<Book> getBookById(long bookId);

  void importBook(Book book) throws IllegalArgumentException;
}
