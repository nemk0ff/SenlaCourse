package ru.bookstore.controllers;

import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;

public interface BooksController {
  ResponseEntity<?> addBook(@Positive Long id, @Positive Integer amount);

  ResponseEntity<?> writeOff(@Positive Long id, @Positive Integer amount);

  ResponseEntity<?> showBookDetails(@Positive Long id);

  ResponseEntity<?> getBooksByName();

  ResponseEntity<?> getBooksByDate();

  ResponseEntity<?> getBooksByPrice();

  ResponseEntity<?> getBooksByAvailable();

  ResponseEntity<?> getStaleBooksByDate();

  ResponseEntity<?> getStaleBooksByPrice();

  ResponseEntity<?> importAll();

  ResponseEntity<?> exportAll();

  ResponseEntity<?> importBook(@Positive Long id);

  ResponseEntity<?> exportBook(@Positive Long id);
}
