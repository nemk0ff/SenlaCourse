package ru.bookstore.controllers.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bookstore.constants.FileConstants;
import ru.bookstore.controllers.BooksController;
import lombok.AllArgsConstructor;
import ru.bookstore.controllers.impl.importexport.ExportController;
import ru.bookstore.controllers.impl.importexport.ImportController;
import ru.bookstore.dto.mappers.BookMapper;
import ru.bookstore.manager.MainManager;
import ru.bookstore.model.impl.Book;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/books")
public class BooksControllerImpl implements BooksController {
  private final MainManager mainManager;

  @GetMapping("showBook/{id}")
  @Override
  public ResponseEntity<?> showBookDetails(@PathVariable("id") Long id) {
    return ResponseEntity.ok(BookMapper.INSTANCE.toDTO(mainManager.getBook(id)));
  }

  @PatchMapping(value = "add", produces = "text/plain;charset=UTF-8")
  @Override
  public ResponseEntity<?> addBook(@RequestParam("id") Long id,
                                   @RequestParam("amount") Integer amount) {
    mainManager.addBook(id, amount, LocalDateTime.now());
    return ResponseEntity.ok("Добавлено " + amount + " книг с id " + id);
  }

  @PatchMapping(value = "writeOff", produces = "text/plain;charset=UTF-8")
  @Override
  public ResponseEntity<?> writeOff(@RequestParam("id") Long id,
                                    @RequestParam("amount") Integer amount) {
    mainManager.writeOff(id, amount, LocalDateTime.now());
    return ResponseEntity.ok("Списано " + id + " книг с id " + amount);
  }

  @GetMapping("getBooks/byName")
  @Override
  public ResponseEntity<?> getBooksByName() {
    return ResponseEntity.ok(BookMapper.INSTANCE.toListDTO(mainManager.getAllBooksByName()));
  }

  @GetMapping("getBooks/byDate")
  @Override
  public ResponseEntity<?> getBooksByDate() {
    return ResponseEntity.ok(BookMapper.INSTANCE.toListDTO(mainManager.getAllBooksByDate()));
  }

  @GetMapping("getBooks/byPrice")
  @Override
  public ResponseEntity<?> getBooksByPrice() {
    return ResponseEntity.ok(BookMapper.INSTANCE.toListDTO(mainManager.getAllBooksByPrice()));
  }

  @GetMapping("getBooks/byAvailable")
  @Override
  public ResponseEntity<?> getBooksByAvailable() {
    return ResponseEntity.ok(BookMapper.INSTANCE.toListDTO(mainManager.getAllBooksByAvailable()));
  }

  @GetMapping("getStaleBooks/byDate")
  @Override
  public ResponseEntity<?> getStaleBooksByDate() {
    return ResponseEntity.ok(BookMapper.INSTANCE.toListDTO(mainManager.getAllStaleBooksByDate()));
  }

  @GetMapping("getStaleBooks/byPrice")
  @Override
  public ResponseEntity<?> getStaleBooksByPrice() {
    return ResponseEntity.ok(BookMapper.INSTANCE.toListDTO(mainManager.getAllStaleBooksByPrice()));
  }

  @PutMapping("importAll")
  @Override
  public ResponseEntity<?> importAll() {
    List<Book> importedBooks = ImportController.importAllItemsFromFile(
        FileConstants.IMPORT_BOOK_PATH, ImportController::bookParser);
    importedBooks.forEach(mainManager::importItem);
    return ResponseEntity.ok(BookMapper.INSTANCE.toListDTO(importedBooks));
  }

  @PutMapping("exportAll")
  @Override
  public ResponseEntity<?> exportAll() {
    List<Book> exportBooks = mainManager.getAllBooks();
    ExportController.exportAll(exportBooks,
        FileConstants.EXPORT_BOOK_PATH, FileConstants.BOOK_HEADER);
    return ResponseEntity.ok(BookMapper.INSTANCE.toListDTO(exportBooks));
  }

  @PutMapping("importBook/{id}")
  @Override
  public ResponseEntity<?> importBook(@PathVariable("id") Long id) {
    Book findBook = ImportController.findItemInFile(id, FileConstants.IMPORT_BOOK_PATH,
        ImportController::bookParser);
    mainManager.importItem(findBook);
    return ResponseEntity.ok(BookMapper.INSTANCE.toDTO(findBook));
  }

  @PutMapping("exportBook/{id}")
  @Override
  public ResponseEntity<?> exportBook(@PathVariable("id") Long id) {
    Book exportBook = mainManager.getBook(id);
    ExportController.exportItemToFile(exportBook,
        FileConstants.EXPORT_BOOK_PATH, FileConstants.BOOK_HEADER);
    return ResponseEntity.ok(BookMapper.INSTANCE.toDTO(exportBook));
  }
}
