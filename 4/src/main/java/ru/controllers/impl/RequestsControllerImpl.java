package ru.controllers.impl;

import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.controllers.RequestsController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.dto.RequestDTO;
import ru.manager.MainManager;
import ru.model.impl.Book;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/requests")
public class RequestsControllerImpl implements RequestsController {
  private final MainManager mainManager;

  @PostMapping("createRequest")
  public ResponseEntity<?> createRequest(@RequestParam Long bookId,
                                      @RequestParam Integer bookAmount) {
    return ResponseEntity.ok(mainManager.createRequest(bookId, bookAmount));
  }

  @GetMapping("getRequests/byCount")
  @Override
  public LinkedHashMap<Book, Long> getRequestsByCount() {
    return mainManager.getRequestsByCount();
  }

  @GetMapping("getRequests/byPrice")
  @Override
  public LinkedHashMap<Book, Long> getRequestsByPrice() {
    return mainManager.getRequestsByPrice();
  }

  @GetMapping("getAllRequests")
  @Override
  public List<RequestDTO> getAllRequests() {
    return mainManager.getRequests()
        .stream()
        .map(RequestDTO::new)
        .toList();
  }

  @GetMapping("exportRequest/{id}")
  @Override
  public void exportRequest(@PathVariable("id") Long id) {
  }

  @GetMapping("importRequest/{id}")
  @Override
  public void importRequest(@PathVariable("id") Long id) {
  }

  @GetMapping("importAll")
  @Override
  public void importAll() {
  }

  @GetMapping("exportAll")
  @Override
  public void exportAll() {
  }
}
