package ru.bookstore.controllers.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import ru.bookstore.facade.BookFacade;
import ru.bookstore.model.impl.Book;
import ru.bookstore.sorting.BookSort;

@Slf4j
@AllArgsConstructor
@RestController
@Validated
@RequestMapping("/books")
public class BooksControllerImpl implements BooksController {
  private final BookFacade bookFacade;

  @GetMapping("{id}")
  @Override
  public ResponseEntity<?> showBookDetails(@PathVariable("id") Long id) {
    return ResponseEntity.ok(BookMapper.INSTANCE.toDTO(bookFacade.get(id)));
  }

  @PatchMapping("add")
  @Override
  public ResponseEntity<?> addBook(@RequestParam("id") Long id,
                                   @RequestParam("amount") Integer amount) {
    return ResponseEntity.ok(BookMapper.INSTANCE.toDTO(bookFacade.addBook(id, amount,
        LocalDateTime.now())));
  }

  @PatchMapping("writeOff")
  @Override
  public ResponseEntity<?> writeOff(@RequestParam("id") Long id,
                                    @RequestParam("amount") Integer amount) {
    return ResponseEntity.ok(BookMapper.INSTANCE.toDTO(bookFacade.writeOff(id, amount,
        LocalDateTime.now())));
  }

  @GetMapping
  @Override
  public ResponseEntity<?> getBooks(@RequestParam("sort") BookSort bookSort) {
    return ResponseEntity.ok(BookMapper.INSTANCE.toListDTO(bookFacade.getAll(bookSort)));
  }

  @GetMapping("stale")
  @Override
  public ResponseEntity<?> getStaleBooks(@RequestParam("sort") BookSort bookSort) {
    return ResponseEntity.ok(BookMapper.INSTANCE.toListDTO(bookFacade.getStale(bookSort)));
  }

  @PutMapping("import")
  @Override
  public ResponseEntity<?> importAll() {
    List<Book> importedBooks = ImportController.importAllItemsFromFile(
        FileConstants.IMPORT_BOOK_PATH, ImportController::bookParser);
    importedBooks.forEach(bookFacade::importBook);
    return ResponseEntity.ok(BookMapper.INSTANCE.toListDTO(importedBooks));
  }

  @PutMapping("export")
  @Override
  public ResponseEntity<?> exportAll() {
    List<Book> exportBooks = bookFacade.getAll(BookSort.ID);
    ExportController.exportAll(exportBooks,
        FileConstants.EXPORT_BOOK_PATH, FileConstants.BOOK_HEADER);
    return ResponseEntity.ok(BookMapper.INSTANCE.toListDTO(exportBooks));
  }

  @PutMapping("import/{id}")
  @Override
  public ResponseEntity<?> importBook(@PathVariable("id") Long id) {
    Book findBook = ImportController.findItemInFile(id, FileConstants.IMPORT_BOOK_PATH,
        ImportController::bookParser);
    bookFacade.importBook(findBook);
    return ResponseEntity.ok(BookMapper.INSTANCE.toDTO(findBook));
  }

  @PutMapping("export/{id}")
  @Override
  public ResponseEntity<?> exportBook(@PathVariable("id") Long id) {
    Book exportBook = bookFacade.get(id);
    ExportController.exportItemToFile(exportBook,
        FileConstants.EXPORT_BOOK_PATH, FileConstants.BOOK_HEADER);
    return ResponseEntity.ok(BookMapper.INSTANCE.toDTO(exportBook));
  }
}
