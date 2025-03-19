package ru.bookstore.controllers.impl;

import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.java.Log;
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
import ru.bookstore.dto.BookDTO;
import ru.bookstore.manager.MainManager;
import ru.bookstore.model.impl.Book;

@Log
@AllArgsConstructor
@RestController
@RequestMapping("/books")
public class BooksControllerImpl implements BooksController {
  private final MainManager mainManager;

  @GetMapping("showBook/{id}")
  @Override
  public ResponseEntity<?> showBookDetails(@PathVariable("id") @Positive Long id) {
    return ResponseEntity.ok(new BookDTO(mainManager.getBook(id)));
  }

  @PatchMapping(value = "add", produces = "text/plain;charset=UTF-8")
  @Override
  public ResponseEntity<?> addBook(@RequestParam("id") @Positive Long id,
                                   @RequestParam("amount") @Positive Integer amount) {
    mainManager.addBook(id, amount, LocalDateTime.now());
    return ResponseEntity.ok("Добавлено " + amount + " книг с id " + id);
  }

  @PatchMapping(value = "writeOff", produces = "text/plain;charset=UTF-8")
  @Override
  public ResponseEntity<?> writeOff(@RequestParam("id") @Positive Long id,
                                    @RequestParam("amount") @Positive Integer amount) {
    mainManager.writeOff(id, amount, LocalDateTime.now());
    return ResponseEntity.ok("Списано " + id + " книг с id " + amount);
  }

  @GetMapping("getBooks/byName")
  @Override
  public ResponseEntity<?> getBooksByName() {
    return ResponseEntity.ok(mainManager.getAllBooksByName()
        .stream()
        .map(BookDTO::new)
        .toList());
  }

  @GetMapping("getBooks/byDate")
  @Override
  public ResponseEntity<?> getBooksByDate() {
    return ResponseEntity.ok(mainManager.getAllBooksByDate());
  }

  @GetMapping("getBooks/byPrice")
  @Override
  public ResponseEntity<?> getBooksByPrice() {
    return ResponseEntity.ok(mainManager.getAllBooksByPrice());
  }

  @GetMapping("getBooks/byAvailable")
  @Override
  public ResponseEntity<?> getBooksByAvailable() {
    return ResponseEntity.ok(mainManager.getAllBooksByAvailable());
  }

  @GetMapping("getStaleBooks/byDate")
  @Override
  public ResponseEntity<?> getStaleBooksByDate() {
    return ResponseEntity.ok(mainManager.getAllStaleBooksByDate());
  }

  @GetMapping("getStaleBooks/byPrice")
  @Override
  public ResponseEntity<?> getStaleBooksByPrice() {
    return ResponseEntity.ok(mainManager.getAllStaleBooksByPrice());
  }

  @PutMapping("importAll")
  @Override
  public ResponseEntity<?> importAll() {
    List<Book> importedBooks = ImportController.importAllItemsFromFile(
        FileConstants.IMPORT_BOOK_PATH, ImportController::bookParser);
    if (!importedBooks.isEmpty()) {
      log.info("Формируем из импортированных данных книги...");
      importedBooks.forEach(mainManager::importItem);
      log.info("Импорт всех книг выполнен.");
    }
    return ResponseEntity.ok(importedBooks);
  }

  @PutMapping("exportAll")
  @Override
  public ResponseEntity<?> exportAll() {
    List<Book> exportBooks = mainManager.getAllBooks();
    ExportController.exportAll(exportBooks,
        FileConstants.EXPORT_BOOK_PATH, FileConstants.BOOK_HEADER);
    return ResponseEntity.ok(exportBooks.stream()
        .map(BookDTO::new)
        .toList());
  }

  @PutMapping("importBook/{id}")
  @Override
  public ResponseEntity<?> importBook(@PathVariable("id") @Positive Long id) {
    Book findBook = ImportController.findItemInFile(id, FileConstants.IMPORT_BOOK_PATH,
        ImportController::bookParser);
    mainManager.importItem(findBook);
    return ResponseEntity.ok(new BookDTO(findBook));
  }

  @PutMapping("exportBook/{id}")
  @Override
  public ResponseEntity<?> exportBook(@PathVariable("id") @Positive Long id) {
    Book exportBook = mainManager.getBook(id);
    ExportController.exportItemToFile(exportBook,
        FileConstants.EXPORT_BOOK_PATH, FileConstants.BOOK_HEADER);
    return ResponseEntity.ok(new BookDTO(exportBook));
  }
}
