package ru.bookstore.facade.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bookstore.facade.BookFacade;
import ru.bookstore.facade.OrderFacade;
import ru.bookstore.model.impl.Book;
import ru.bookstore.service.BookService;
import ru.bookstore.service.OrderService;
import ru.bookstore.service.RequestService;
import ru.bookstore.sorting.BookSort;

@Service
@Data
@Slf4j
public class BookFacadeImpl implements BookFacade {
  @Value("${mark.orders.completed}")
  private boolean markOrdersCompleted;

  private final BookService bookService;
  private final OrderService orderService;
  private final RequestService requestService;

  private final OrderFacade orderFacade;

  @Transactional
  @Override
  public Book addBook(Long id, Integer amount, LocalDateTime deliveredDate) {
    bookService.add(id, amount, deliveredDate);
    if (markOrdersCompleted) {
      log.info("Обновляем заказы после добавления книги...");
      updateOrders(deliveredDate);
      log.info("Все заказы успешно обновлены после добавления книги [{}].", id);
    }
    return bookService.get(id);
  }

  @Transactional
  @Override
  public Book writeOff(Long id, Integer amount, LocalDateTime writeOffDate) {
    bookService.writeOff(id, amount, writeOffDate);
    if (markOrdersCompleted) {
      log.info("Обновляем заказы после списания книги...");
      updateOrders(writeOffDate);
      log.info("Все заказы успешно обновлены после списания книги [{}].", id);
    }
    return bookService.get(id);
  }

  @Transactional(readOnly = true)
  @Override
  public Book get(Long bookId) {
    return bookService.get(bookId);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Book> getAll(BookSort sortType) {
    return switch (sortType) {
      case NAME -> bookService.getAllBooksByName();
      case PUBLICATION_DATE -> bookService.getAllBooksByDate();
      case PRICE -> bookService.getAllBooksByPrice();
      case STATUS -> bookService.getAllBooksByAvailable();
      default -> bookService.getAllBooksById();
    };
  }

  @Transactional(readOnly = true)
  @Override
  public List<Book> getStale(BookSort sortType) {
    if (sortType == BookSort.STALE_BY_DATE) {
      return bookService.getAllStaleBooksByDate();
    }
    return bookService.getAllStaleBooksByPrice();
  }

  @Transactional
  @Override
  public void importBook(Book book) {
    bookService.importBook(book);
    log.info("Обновляем заказы после импорта книги...");
    updateOrders(LocalDateTime.now());
    log.info("Все заказы успешно обновлены после импорта книги [{}].", book.getId());
  }

  private void updateOrders(LocalDateTime updateDate) {
    orderService.getAllOrdersById().forEach(order -> orderFacade.updateOrder(order, updateDate));
  }
}
