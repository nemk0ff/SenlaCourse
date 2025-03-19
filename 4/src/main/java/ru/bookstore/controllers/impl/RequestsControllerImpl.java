package ru.bookstore.controllers.impl;

import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bookstore.constants.FileConstants;
import ru.bookstore.controllers.RequestsController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.bookstore.controllers.impl.importexport.ExportController;
import ru.bookstore.controllers.impl.importexport.ImportController;
import ru.bookstore.dto.RequestDTO;
import ru.bookstore.manager.MainManager;
import ru.bookstore.model.impl.Request;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/requests")
public class RequestsControllerImpl implements RequestsController {
  private final MainManager mainManager;

  @PostMapping("createRequest")
  public ResponseEntity<?> createRequest(@RequestParam @Positive Long bookId,
                                         @RequestParam @Positive Integer bookAmount) {
    return ResponseEntity.ok(mainManager.createRequest(bookId, bookAmount));
  }

  @GetMapping("getRequests/byCount")
  @Override
  public ResponseEntity<?> getRequestsByCount() {
    return ResponseEntity.ok(mainManager.getRequestsByCount());
  }

  @GetMapping("getRequests/byPrice")
  @Override
  public ResponseEntity<?> getRequestsByPrice() {
    return ResponseEntity.ok(mainManager.getRequestsByPrice());
  }

  @GetMapping("getAllRequests")
  @Override
  public ResponseEntity<?> getAllRequests() {
    return ResponseEntity.ok(mainManager.getRequests()
        .stream()
        .map(RequestDTO::new)
        .toList());
  }

  @PutMapping("exportRequest/{id}")
  @Override
  public ResponseEntity<?> exportRequest(@PathVariable("id") Long id) {
    Request exportRequest = mainManager.getRequest(id);
    ExportController.exportItemToFile(exportRequest, FileConstants.EXPORT_REQUEST_PATH,
        FileConstants.REQUEST_HEADER);
    return ResponseEntity.ok(new RequestDTO(exportRequest));
  }

  @PutMapping("importRequest/{id}")
  @Override
  public ResponseEntity<?> importRequest(@PathVariable("id") Long id) {
    Request findRequest = ImportController.findItemInFile(id, FileConstants.IMPORT_REQUEST_PATH,
        ImportController::requestParser);
    mainManager.importItem(findRequest);
    return ResponseEntity.ok(new RequestDTO(findRequest));
  }

  @PutMapping("importAll")
  @Override
  public ResponseEntity<?> importAll() {
    List<Request> importedRequests = ImportController.importAllItemsFromFile(
        FileConstants.IMPORT_REQUEST_PATH, ImportController::requestParser);
    importedRequests.forEach(mainManager::importItem);
    return ResponseEntity.ok(importedRequests.stream().map(RequestDTO::new).toList());
  }

  @PutMapping("exportAll")
  @Override
  public ResponseEntity<?> exportAll() {
    List<Request> exportRequests = mainManager.getRequests();
    ExportController.exportAll(exportRequests,
        FileConstants.EXPORT_REQUEST_PATH, FileConstants.REQUEST_HEADER);
    return ResponseEntity.ok(exportRequests.stream().map(RequestDTO::new).toList());
  }
}
