package ru.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.http.ResponseEntity;
import ru.dto.RequestDTO;
import ru.model.impl.Book;

public interface RequestsController {
  ResponseEntity<?> createRequest(Long bookId, Integer bookAmount);

  LinkedHashMap<Book, Long> getRequestsByCount();

  LinkedHashMap<Book, Long> getRequestsByPrice();

  List<RequestDTO> getAllRequests();

  void exportRequest(Long id);

  void importRequest(Long id);

  void importAll();

  void exportAll();
}
