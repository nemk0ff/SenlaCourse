package ru.bookstore.service;

import java.time.LocalDateTime;
import java.util.List;
import ru.bookstore.model.impl.Book;

public interface BookService {
  void add(Long id, Integer amount, LocalDateTime addTime);

  void writeOff(Long id, Integer amount, LocalDateTime addTime);

  Book get(Long bookId);

  List<Book> getBooks(List<Long> bookIds);

  void importBook(Book book);

  List<Book> getAllBooksById();

  List<Book> getAllBooksByName();

  List<Book> getAllBooksByDate();

  List<Book> getAllBooksByPrice();

  List<Book> getAllBooksByAvailable();

  List<Book> getAllStaleBooksByDate();

  List<Book> getAllStaleBooksByPrice();
}
