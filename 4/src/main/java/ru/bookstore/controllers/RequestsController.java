package ru.bookstore.controllers;

import org.springframework.http.ResponseEntity;

public interface RequestsController {
  ResponseEntity<?> createRequest(Long bookId, Integer bookAmount);

  ResponseEntity<?> getRequestsByCount();

  ResponseEntity<?> getRequestsByPrice();

  ResponseEntity<?> getAllRequests();

  ResponseEntity<?> exportRequest(Long id);

  ResponseEntity<?> importRequest(Long id);

  ResponseEntity<?> importAll();

  ResponseEntity<?> exportAll();
}
