package ru.bookstore.controllers.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import ru.bookstore.controllers.impl.importexport.ExportController;
import ru.bookstore.controllers.impl.importexport.ImportController;
import ru.bookstore.dto.mappers.BookMapper;
import ru.bookstore.facade.BookFacade;
import ru.bookstore.facade.OrderFacade;
import ru.bookstore.model.impl.Book;
import ru.bookstore.sorting.BookSort;
import ru.bookstore.sorting.OrderSort;

@Slf4j
@RestController
@Validated
@Data
@RequestMapping("/books")
public class BooksControllerImpl implements BooksController {
  @Value("${mark.orders.completed}")
  private boolean markOrdersCompleted;

  private final BookFacade bookFacade;
  private final OrderFacade orderFacade;

  @GetMapping("{id}")
  @Override
  public ResponseEntity<?> showBookDetails(@PathVariable("id") Long id) {
    return ResponseEntity.ok(BookMapper.INSTANCE.toDTO(bookFacade.get(id)));
  }

  @PatchMapping("add")
  @Override
  public ResponseEntity<?> addBook(@RequestParam("id") Long id,
                                   @RequestParam("amount") Integer amount) {
    Book book = bookFacade.addBook(id, amount, LocalDateTime.now());
    if (markOrdersCompleted) {
      log.info("Обновляем заказы после добавления книги...");
      orderFacade.getAll(OrderSort.ID)
          .forEach(order -> orderFacade.updateOrder(order, LocalDateTime.now()));
      log.info("Все заказы успешно обновлены после добавления книги [{}].", id);
    }
    return ResponseEntity.ok(BookMapper.INSTANCE.toDTO(book));
  }

  @PatchMapping("writeOff")
  @Override
  public ResponseEntity<?> writeOff(@RequestParam("id") Long id,
                                    @RequestParam("amount") Integer amount) {
    Book book = bookFacade.writeOff(id, amount, LocalDateTime.now());
    if (markOrdersCompleted) {
      log.info("Обновляем заказы после списания книги...");
      orderFacade.getAll(OrderSort.ID)
          .forEach(order -> orderFacade.updateOrder(order, LocalDateTime.now()));
      log.info("Все заказы успешно обновлены после списания книги [{}].", id);
    }
    return ResponseEntity.ok(BookMapper.INSTANCE.toDTO(book));
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
    if (markOrdersCompleted) {
      log.info("Обновляем заказы после импорта всех книг...");
      orderFacade.getAll(OrderSort.ID)
          .forEach(order -> orderFacade.updateOrder(order, LocalDateTime.now()));
      log.info("Все заказы успешно обновлены после импорта всех книг.");
    }
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
    if (markOrdersCompleted) {
      log.info("Обновляем заказы после импорта книги...");
      orderFacade.getAll(OrderSort.ID)
          .forEach(order -> orderFacade.updateOrder(order, LocalDateTime.now()));
      log.info("Все заказы успешно обновлены после импорта книги [{}].", id);
    }
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
