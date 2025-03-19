package ru.bookstore.controllers;

import org.springframework.http.ResponseEntity;

public interface BooksController {
  ResponseEntity<?> addBook(Long id, Integer amount);

  ResponseEntity<?> writeOff(Long id, Integer amount);

  ResponseEntity<?> showBookDetails(Long id);

  ResponseEntity<?> getBooksByName();

  ResponseEntity<?> getBooksByDate();

  ResponseEntity<?> getBooksByPrice();

  ResponseEntity<?> getBooksByAvailable();

  ResponseEntity<?> getStaleBooksByDate();

  ResponseEntity<?> getStaleBooksByPrice();

  ResponseEntity<?> importAll();

  ResponseEntity<?> exportAll();

  ResponseEntity<?> importBook(Long id);

  ResponseEntity<?> exportBook(Long id);
}
