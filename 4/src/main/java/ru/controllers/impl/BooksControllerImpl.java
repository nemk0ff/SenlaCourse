package ru.controllers.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.constants.FileConstants;
import ru.controllers.BooksController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.controllers.impl.importexport.ImportController;
import ru.dto.BookDTO;
import ru.manager.MainManager;
import ru.model.impl.Book;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/books")
public class BooksControllerImpl implements BooksController {
  private final MainManager mainManager;

  @GetMapping("showBook/{id}")
  @Override
  public ResponseEntity<?> showBookDetails(@PathVariable("id") Long id) {
    return ResponseEntity.ok(new BookDTO(mainManager.getBook(id)));
  }

  @PostMapping("add")
  @Override
  public ResponseEntity<?> addBook(@RequestBody BookDTO bookDTO) {
    mainManager.addBook(bookDTO.getId(), bookDTO.getAmount(), LocalDateTime.now());
    return ResponseEntity.ok("Добавлено " + bookDTO.getAmount()
        + " книг с id " + bookDTO.getId());
  }

  @PostMapping("writeOff")
  @Override
  public ResponseEntity<?> writeOff(@RequestBody BookDTO bookDTO) {
    mainManager.writeOff(bookDTO.getId(), bookDTO.getAmount(), LocalDateTime.now());
    return ResponseEntity.ok("Списано " + bookDTO.getAmount()
        + " книг с id " + bookDTO.getId());
  }

  @GetMapping("getBooks/byName")
  @Override
  public List<BookDTO> getBooksByName() {
    return mainManager.getAllBooksByName().stream().map(BookDTO::new).toList();
  }

  @GetMapping("getBooks/byDate")
  @Override
  public List<Book> getBooksByDate() {
    return mainManager.getAllBooksByDate();
  }

  @GetMapping("getBooks/byPrice")
  @Override
  public List<Book> getBooksByPrice() {
    return mainManager.getAllStaleBooksByPrice();
  }

  @GetMapping("getBooks/byAvailable")
  @Override
  public List<Book> getBooksByAvailable() {
    return mainManager.getAllBooksByAvailable();
  }

  @GetMapping("getStaleBooks/byDate")
  @Override
  public List<Book> getStaleBooksByDate() {
    return mainManager.getAllStaleBooksByDate();
  }

  @GetMapping("getStaleBooks/byPrice")
  @Override
  public List<Book> getStaleBooksByPrice() {
    return mainManager.getAllStaleBooksByPrice();
  }

  @GetMapping("importAll")
  @Override
  public void importAll() {
    List<Book> importedBooks = ImportController.importAllItemsFromFile(
        FileConstants.IMPORT_BOOK_PATH, ImportController::bookParser);
    if (!importedBooks.isEmpty()) {
      log.info("Формируем из импортированных данных книги...");
      importedBooks.forEach(mainManager::importItem);
      log.info("Импорт всех книг выполнен.");
    }
  }

  @GetMapping("exportAll")
  @Override
  public void exportAll() {
  }

  @GetMapping("importBook/{id}")
  @Override
  public void importBook(@PathVariable("id") Long id) {
  }

  @GetMapping("exportBook/{id}")
  @Override
  public void exportBook(@PathVariable("id") Long id) {
  }
}
