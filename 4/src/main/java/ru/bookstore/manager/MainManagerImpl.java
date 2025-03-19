package ru.bookstore.manager;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import ru.bookstore.dao.BookDao;
import ru.bookstore.dao.OrderDao;
import ru.bookstore.dao.RequestDao;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bookstore.exceptions.EntityNotFoundException;
import ru.bookstore.model.Item;
import ru.bookstore.model.OrderStatus;
import ru.bookstore.model.impl.Book;
import ru.bookstore.model.impl.Order;
import ru.bookstore.model.impl.Request;
import org.springframework.stereotype.Service;
import ru.bookstore.sorting.BookSort;
import ru.bookstore.sorting.OrderSort;
import ru.bookstore.sorting.RequestSort;

@Service
@Slf4j
@Data
@Primary
public class MainManagerImpl implements MainManager {
  @Value("${mark.orders.completed}")
  private boolean markOrdersCompleted;

  private final BookDao bookDao;
  private final OrderDao orderDao;
  private final RequestDao requestDao;

  @Transactional
  @Override
  public void addBook(Long id, Integer amount, LocalDateTime deliveredDate) {
    bookDao.add(id, amount, deliveredDate);
    if (markOrdersCompleted) {
      log.info("Обновляем заказы после добавления книги...");
      updateOrders(deliveredDate);
      log.info("Все заказы успешно обновлены после добавления книги [{}].", id);
    }
  }

  @Transactional
  @Override
  public void writeOff(Long id, Integer amount, LocalDateTime writeOffDate) {
    bookDao.writeOff(id, amount, writeOffDate);
    if (markOrdersCompleted) {
      log.info("Обновляем заказы после списания книги...");
      updateOrders(writeOffDate);
      log.info("Все заказы успешно обновлены после списания книги [{}].", id);
    }
  }

  @Transactional
  @Override
  public Book getBook(Long id) {
    return bookDao.getBookById(id).orElseThrow(()
        -> new EntityNotFoundException("Книга [" + id + "] не найдена"));
  }

  @Transactional
  @Override
  public List<Book> getAllBooks() {
    return bookDao.getAllBooks(BookSort.ID);
  }

  @Transactional
  @Override
  public List<Book> getAllBooksByName() {
    return bookDao.getAllBooks(BookSort.NAME);
  }

  @Transactional
  @Override
  public List<Book> getAllBooksByDate() {
    return bookDao.getAllBooks(BookSort.PUBLICATION_DATE);
  }

  @Transactional
  @Override
  public List<Book> getAllBooksByPrice() {
    return bookDao.getAllBooks(BookSort.PRICE);
  }

  @Transactional
  @Override
  public List<Book> getAllBooksByAvailable() {
    return bookDao.getAllBooks(BookSort.STATUS);
  }

  @Transactional
  @Override
  public List<Book> getAllStaleBooksByDate() {
    return bookDao.getAllBooks(BookSort.STALE_BY_DATE);
  }

  @Transactional
  @Override
  public List<Book> getAllStaleBooksByPrice() {
    return bookDao.getAllBooks(BookSort.STALE_BY_PRICE);
  }

  @Transactional
  @Override
  public void importBook(Book book) {
    bookDao.importBook(book);
    log.info("Обновляем заказы после импорта книги...");
    updateOrders(LocalDateTime.now());
    log.info("Все заказы успешно обновлены после импорта книги [{}].", book.getId());
  }

  private void updateOrders(LocalDateTime updateDate) {
    for (Order order : orderDao.getAllOrders(OrderSort.ID, null, null)) {
      log.debug("Обновляем заказ: {}...", order.getInfoAbout());
      if (order.getStatus() == OrderStatus.NEW) {
        for (Map.Entry<Long, Integer> entry : order.getBooks().entrySet()) {
          Optional<Book> optionalBook = bookDao.getBookById(entry.getKey());
          if (optionalBook.isEmpty()) {
            log.debug("В заказе [{}] есть {} книг [{}], но на складе таких книг нет",
                order.getId(), entry.getValue(), entry.getValue());
            return;
          } else if (optionalBook.get().getAmount() < entry.getValue()) {
            log.debug("В заказе [{}] есть {} книг [{}], но на складе таких книг только {}",
                order.getId(), entry.getValue(), entry.getValue(),
                optionalBook.get().getAmount());
            return;
          }
        }
        log.info("Есть все необходимые книги для заказа [{}], выполняем заказ...",
            order.getId());
        completeOrder(order, updateDate);
        log.info("Заказ [{}] успешно выполнен.", order.getId());
      }
      log.debug("Заказ успешно обновлен: {}.", order.getInfoAbout());
    }
  }

  private void completeOrder(Order order, LocalDateTime completeDate) {
    orderDao.setOrderStatus(order.getId(), OrderStatus.COMPLETED);
    requestDao.closeRequests(order.getBooks());
    for (Map.Entry<Long, Integer> entry : order.getBooks().entrySet()) {
      bookDao.writeOff(entry.getKey(), entry.getValue(), completeDate);
    }
  }

  @Transactional
  @Override
  public Long createRequest(Long bookId, Integer amount) {
    return requestDao.addRequest(getBook(bookId), amount);
  }

  @Transactional
  @Override
  public Order createOrder(Map<Long, Integer> booksIds, String clientName,
                           LocalDateTime orderDate) {
    long newOrderId = orderDao.addOrder(
        new Order(booksIds, bookDao.getBooks(booksIds.keySet()
                .stream()
                .toList())
            .stream()
            .mapToDouble(Book::getPrice)
            .sum(),
            OrderStatus.NEW, orderDate, clientName));
    createRequests(newOrderId);
    return orderDao.findWithBooks(newOrderId).orElse(new Order());
  }

  @Transactional
  @Override
  public void cancelOrder(long orderId) {
    log.debug("Отменяем заказ [{}]...", orderId);
    Optional<Order> order = orderDao.findWithBooks(orderId);
    if (order.isEmpty()) {
      throw new EntityNotFoundException("Заказ [" + orderId + "] не найден");
    }
    if (order.get().getStatus() == OrderStatus.NEW) {
      orderDao.setOrderStatus(orderId, OrderStatus.CANCELED);
      requestDao.closeRequests(order.get().getBooks());
      log.info("Заказ [{}] успешно отменен", orderId);
    } else {
      throw new IllegalArgumentException("Невозможно отменить заказ, статус которого не NEW");
    }
  }

  @Transactional
  @Override
  public void setOrderStatus(long orderId, OrderStatus status) {
    orderDao.setOrderStatus(orderId, status);
    if (status == OrderStatus.NEW) {
      createRequests(orderId);
    }
  }

  @Transactional
  @Override
  public List<Order> getAllOrders() {
    return orderDao.getAllOrders(OrderSort.ID, null, null);
  }

  @Transactional
  @Override
  public List<Order> getAllOrdersByDate() {
    return orderDao.getAllOrders(OrderSort.COMPLETE_DATE, null, null);
  }

  @Transactional
  @Override
  public List<Order> getAllOrdersByPrice() {
    return orderDao.getAllOrders(OrderSort.PRICE, null, null);
  }

  @Transactional
  @Override
  public List<Order> getAllOrdersByStatus() {
    return orderDao.getAllOrders(OrderSort.STATUS, null, null);
  }

  @Transactional
  @Override
  public List<Order> getCompletedOrdersByDate(LocalDateTime begin, LocalDateTime end) {
    return orderDao.getAllOrders(OrderSort.COMPLETED_BY_DATE, begin, end);
  }

  @Transactional
  @Override
  public List<Order> getCompletedOrdersByPrice(LocalDateTime begin, LocalDateTime end) {
    return orderDao.getAllOrders(OrderSort.COMPLETED_BY_PRICE, begin, end);
  }

  @Transactional
  @Override
  public Double getEarnedSum(LocalDateTime begin, LocalDateTime end) {
    return orderDao.getEarnedSum(begin, end);
  }

  @Transactional
  @Override
  public Long getCountCompletedOrders(LocalDateTime begin, LocalDateTime end) {
    return orderDao.getCountCompletedOrders(begin, end);
  }

  @Transactional
  @Override
  public Order getOrder(Long orderId) {
    return orderDao.findWithBooks(orderId).orElseThrow(()
        -> new EntityNotFoundException("Заказ [" + orderId + "] не найден"));
  }

  @Transactional
  @Override
  public List<Request> getRequests() {
    return requestDao.getAllRequests(RequestSort.ID);
  }

  @Transactional
  @Override
  public LinkedHashMap<Book, Long> getRequestsByCount() {
    return requestDao.getRequests(RequestSort.COUNT);
  }

  @Transactional
  @Override
  public LinkedHashMap<Book, Long> getRequestsByPrice() {
    return requestDao.getRequests(RequestSort.PRICE);
  }

  public boolean isAvailable(long bookId, int requestAmount) {
    Optional<Book> book = bookDao.getBookById(bookId);
    return book.filter(value -> value.getAmount() >= requestAmount).isPresent();
  }

  @Transactional
  @Override
  public void createRequests(Long orderId) {
    log.info("Создание запросов для заказа [{}]...", orderId);
    Optional<Order> order = orderDao.findWithBooks(orderId);
    if (order.isEmpty()) {
      throw new EntityNotFoundException("Не удалось создать запросы для заказа " + orderId
          + " : заказ с таким id не найден");
    }
    boolean completed = true;
    for (Map.Entry<Long, Integer> entry : order.get().getBooks().entrySet()) {
      Optional<Book> book = bookDao.getBookById(entry.getKey());
      if (book.isEmpty()) {
        throw new IllegalArgumentException("Попытка создать заказ с несуществующей книгой (id=["
            + entry.getKey() + "])");
      }
      requestDao.addRequest(book.get(), entry.getValue());
      if (!isAvailable(entry.getKey(), entry.getValue())) {
        completed = false;
      }
    }
    log.info("Созданы запросы для заказа [{}].", orderId);
    if (completed) {
      log.info("При создании заказа [{}] обнаружено, что все книги есть в наличии. Выполняем "
          + "заказ...", orderId);
      completeOrder(order.get(), LocalDateTime.now());
    }
  }

  @Transactional
  @Override
  public Request getRequest(Long requestId) {
    return requestDao.getRequestById(requestId).orElseThrow(()
        -> new EntityNotFoundException("Запрос [" + requestId + "] не найден"));
  }

  @Transactional
  @Override
  public void importOrder(Order order) {
    Optional<Order> findOrder = orderDao.findWithBooks(order.getId());
    if (findOrder.isPresent()) {
      requestDao.closeRequests(findOrder.get().getBooks());
      orderDao.update(order);
    } else {
      orderDao.addOrder(order);
    }
    createRequests(order.getId());
  }

  @Transactional
  @Override
  public void importRequest(Request request) {
    log.info("Импорт запроса {}...", request.getId());
    Optional<Request> findRequest = requestDao.getRequestById(request.getId());
    if (findRequest.isPresent()) {
      log.warn("Ошибка при импорте: Запрос [{}] уже есть в магазине", request.getId());
    } else {
      requestDao.importRequest(request);
    }
  }

  @Transactional
  @Override
  public <T extends Item> void importItem(T item) {
    if (item instanceof Book) {
      importBook((Book) item);
    } else if (item instanceof Order) {
      importOrder((Order) item);
    } else if (item instanceof Request) {
      importRequest((Request) item);
    }
  }
}
