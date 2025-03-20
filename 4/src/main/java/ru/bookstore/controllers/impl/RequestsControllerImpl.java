package ru.bookstore.controllers.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import ru.bookstore.dto.mappers.BookMapper;
import ru.bookstore.dto.mappers.RequestMapper;
import ru.bookstore.manager.MainManager;
import ru.bookstore.model.impl.Request;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/requests")
public class RequestsControllerImpl implements RequestsController {
  private final MainManager mainManager;
  private final ImportController importController;

  @PostMapping(value = "createRequest", produces = "text/plain;charset=UTF-8")
  public ResponseEntity<?> createRequest(@RequestParam("bookId") Long bookId,
                                         @RequestParam("amount") Integer amount) {
    return ResponseEntity.ok("Запрос " + mainManager.createRequest(bookId, amount) + " создан");
  }

  @GetMapping("getRequests/byCount")
  @Override
  public ResponseEntity<?> getRequestsByCount() {
    return ResponseEntity.ok(mainManager.getRequestsByCount().entrySet().stream()
        .collect(Collectors.toMap(
            entry -> BookMapper.INSTANCE.toDTO(entry.getKey()),
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new
        )));
  }

  @GetMapping("getRequests/byPrice")
  @Override
  public ResponseEntity<?> getRequestsByPrice() {
    return ResponseEntity.ok(mainManager.getRequestsByPrice().entrySet().stream()
        .collect(Collectors.toMap(
            entry -> BookMapper.INSTANCE.toDTO(entry.getKey()),
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new
        )));
  }

  @GetMapping("getAllRequests")
  @Override
  public ResponseEntity<?> getAllRequests() {
    return ResponseEntity.ok(RequestMapper.INSTANCE.toListDTO(mainManager.getRequests()));
  }

  @PutMapping("exportRequest/{id}")
  @Override
  public ResponseEntity<?> exportRequest(@PathVariable("id") Long id) {
    Request exportRequest = mainManager.getRequest(id);
    ExportController.exportItemToFile(exportRequest, FileConstants.EXPORT_REQUEST_PATH,
        FileConstants.REQUEST_HEADER);
    return ResponseEntity.ok(RequestMapper.INSTANCE.toDTO(exportRequest));
  }

  @PutMapping("importRequest/{id}")
  @Override
  public ResponseEntity<?> importRequest(@PathVariable("id") Long id) {
    Request findRequest = ImportController.findItemInFile(id, FileConstants.IMPORT_REQUEST_PATH,
        importController::requestParser);
    mainManager.importItem(findRequest);
    return ResponseEntity.ok(RequestMapper.INSTANCE.toDTO(findRequest));
  }

  @PutMapping("importAll")
  @Override
  public ResponseEntity<?> importAll() {
    List<Request> importedRequests = ImportController.importAllItemsFromFile(
        FileConstants.IMPORT_REQUEST_PATH, importController::requestParser);
    importedRequests.forEach(mainManager::importItem);
    return ResponseEntity.ok(RequestMapper.INSTANCE.toListDTO(importedRequests));
  }

  @PutMapping("exportAll")
  @Override
  public ResponseEntity<?> exportAll() {
    List<Request> exportRequests = mainManager.getRequests();
    ExportController.exportAll(exportRequests,
        FileConstants.EXPORT_REQUEST_PATH, FileConstants.REQUEST_HEADER);
    return ResponseEntity.ok(RequestMapper.INSTANCE.toListDTO(exportRequests));
  }
}
