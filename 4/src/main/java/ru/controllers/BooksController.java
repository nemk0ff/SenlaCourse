package ru.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import ru.dto.BookDTO;
import ru.model.impl.Book;

public interface BooksController {
  ResponseEntity<?> addBook(BookDTO bookDTO);

  ResponseEntity<?> writeOff(BookDTO bookDTO);

  ResponseEntity<?> showBookDetails(Long id);

  List<BookDTO> getBooksByName();

  List<Book> getBooksByDate();

  List<Book> getBooksByPrice();

  List<Book> getBooksByAvailable();

  List<Book> getStaleBooksByDate();

  List<Book> getStaleBooksByPrice();

  void importAll();

  void exportAll();

  void importBook(Long id);

  void exportBook(Long id);
}
