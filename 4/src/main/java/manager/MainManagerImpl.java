package manager;

import annotations.ConfigProperty;
import dao.BookDao;
import dao.OrderDao;
import dao.RequestDao;
import hibernate.HibernateUtil;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import model.Item;
import model.OrderStatus;
import model.impl.Book;
import model.impl.Order;
import model.impl.Request;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;
import sorting.BookSort;
import sorting.OrderSort;
import sorting.RequestSort;

/**
 * {@code MainManagerImpl} - Реализация интерфейса {@link MainManager}, предоставляющая методы
 * для управления основными операциями приложения.
 */
@Service
@Slf4j
@Data
public class MainManagerImpl implements MainManager {
  @ConfigProperty(propertyName = "mark.orders.completed", type = boolean.class)
  private boolean markOrdersCompleted;

  private final HibernateUtil hibernateUtil;
  private final BookDao bookDao;
  private final OrderDao orderDao;
  private final RequestDao requestDao;

  private <T> T inSession(Function<Session, T> operation) {
    try (Session session = hibernateUtil.getSession()) {
      Transaction transaction = session.beginTransaction();
      T result = operation.apply(session);
      transaction.commit();
      return result;
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при выполнении операции", e);
    }
  }

  private void inSessionVoid(Consumer<Session> operation) {
    try (Session session = hibernateUtil.getSession()) {
      Transaction transaction = session.beginTransaction();
      operation.accept(session);
      transaction.commit();
    } catch (Exception e) {
      throw new RuntimeException("Ошибка при выполнении операции", e);
    }
  }

  @Override
  public void addBook(long id, Integer amount, LocalDateTime deliveredDate) {
    inSessionVoid(session -> {
      bookDao.add(session, id, amount, deliveredDate);
      if (markOrdersCompleted) {
        log.info("Обновляем заказы после добавления книги...");
        updateOrders(session, deliveredDate);
        log.info("Все заказы успешно обновлены после добавления книги [{}].", id);
      }
    });
  }

  @Override
  public void writeOff(long id, Integer amount, LocalDateTime writeOffDate) {
    inSessionVoid(session -> {
      bookDao.writeOff(session, id, amount, writeOffDate);
      if (markOrdersCompleted) {
        log.info("Обновляем заказы после списания книги...");
        updateOrders(session, writeOffDate);
        log.info("Все заказы успешно обновлены после списания книги [{}].", id);
      }
    });
  }

  @Override
  public Optional<Book> getBook(long id) {
    return inSession(session -> bookDao.getBookById(session, id));
  }

  @Override
  public List<Book> getAllBooks() {
    return inSession(session -> bookDao.getAllBooks(session, BookSort.ID));
  }

  @Override
  public List<Book> getAllBooksByName() {
    return inSession(session -> bookDao.getAllBooks(session, BookSort.NAME));
  }

  @Override
  public List<Book> getAllBooksByDate() {
    return inSession(session -> bookDao.getAllBooks(session, BookSort.PUBLICATION_DATE));
  }

  @Override
  public List<Book> getAllBooksByPrice() {
    return inSession(session -> bookDao.getAllBooks(session, BookSort.PRICE));
  }

  @Override
  public List<Book> getAllBooksByAvailable() {
    return inSession(session -> bookDao.getAllBooks(session, BookSort.STATUS));
  }

  @Override
  public List<Book> getAllStaleBooksByDate() {
    return inSession(session -> bookDao.getAllBooks(session, BookSort.STALE_BY_DATE));
  }

  @Override
  public List<Book> getAllStaleBooksByPrice() {
    return inSession(session -> bookDao.getAllBooks(session, BookSort.STALE_BY_PRICE));
  }

  @Override
  public boolean containsBook(long bookId) {
    return inSession(session -> bookDao.getBookById(session, bookId).isPresent());
  }

  @Override
  public void importBook(Book book) throws IllegalArgumentException {
    inSessionVoid(session -> {
      bookDao.importBook(session, book);
      log.info("Обновляем заказы после импорта книги...");
      updateOrders(session, LocalDateTime.now());
      log.info("Все заказы успешно обновлены после импорта книги [{}].", book.getId());
    });
  }

  private void updateOrders(Session session, LocalDateTime updateDate) {
    for (Order order : orderDao.getAllOrders(session, OrderSort.ID, null, null)) {
      log.debug("Обновляем заказ: {}...", order.getInfoAbout());
      if (order.getStatus() == OrderStatus.NEW) {
        for (Map.Entry<Long, Integer> entry : order.getBooks().entrySet()) {
          Optional<Book> optionalBook = bookDao.getBookById(session, entry.getKey());
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
        completeOrder(session, order, updateDate);
        log.info("Заказ [{}] успешно выполнен.", order.getId());
      }
      log.debug("Заказ успешно обновлен: {}.", order.getInfoAbout());
    }
  }

  private void completeOrder(Session session, Order order, LocalDateTime completeDate) {
    orderDao.setOrderStatus(session, order.getId(), OrderStatus.COMPLETED);
    requestDao.closeRequests(session, order.getBooks());
    for (Map.Entry<Long, Integer> entry : order.getBooks().entrySet()) {
      bookDao.writeOff(session, entry.getKey(), entry.getValue(), completeDate);
    }
  }

  @Override
  public long createRequest(Book book, int amount) {
    return inSession(session -> requestDao.addRequest(session, book, amount));
  }

  @Override
  public Order createOrder(Map<Long, Integer> booksIds, String clientName,
                           LocalDateTime orderDate) {
    return inSession(session -> {
      long newOrderId = orderDao.addOrder(session,
          new Order(booksIds, bookDao.getBooks(session, booksIds.keySet()
                  .stream()
                  .toList())
              .stream()
              .mapToDouble(Book::getPrice)
              .sum(),
              OrderStatus.NEW, orderDate, clientName));
      createRequests(session, newOrderId);
      return orderDao.findWithBooks(session, newOrderId).orElse(new Order());
    });
  }

  @Override
  public void cancelOrder(long orderId) throws IllegalArgumentException {
    inSessionVoid(session -> {
      log.debug("Отменяем заказ [{}]...", orderId);
      Optional<Order> order = orderDao.findWithBooks(session, orderId);
      if (order.isEmpty()) {
        throw new IllegalArgumentException("Заказ [" + orderId + "] не найден");
      }
      if (order.get().getStatus() == OrderStatus.NEW) {
        orderDao.setOrderStatus(session, orderId, OrderStatus.CANCELED);
        requestDao.closeRequests(session, order.get().getBooks());
        log.info("Заказ [{}] успешно отменен", orderId);
      } else {
        throw new IllegalArgumentException("Невозможно отменить заказ, статус которого не NEW");
      }
    });
  }

  @Override
  public void setOrderStatus(long orderId, OrderStatus status) {
    inSessionVoid(session -> {
      orderDao.setOrderStatus(session, orderId, status);
      if (status == OrderStatus.NEW) {
        createRequests(session, orderId);
      }
    });
  }

  @Override
  public List<Order> getAllOrders() {
    return inSession(session
        -> orderDao.getAllOrders(session, OrderSort.ID, null, null));
  }

  @Override
  public List<Order> getAllOrdersByDate() {
    return inSession(session ->
        orderDao.getAllOrders(session, OrderSort.COMPLETE_DATE, null, null));
  }

  @Override
  public List<Order> getAllOrdersByPrice() {
    return inSession(session ->
        orderDao.getAllOrders(session, OrderSort.PRICE, null, null));
  }

  @Override
  public List<Order> getAllOrdersByStatus() {
    return inSession(session ->
        orderDao.getAllOrders(session, OrderSort.STATUS, null, null));
  }

  @Override
  public List<Order> getCompletedOrdersByDate(LocalDateTime begin, LocalDateTime end) {
    return inSession(session ->
        orderDao.getAllOrders(session, OrderSort.COMPLETED_BY_DATE, begin, end));
  }

  @Override
  public List<Order> getCompletedOrdersByPrice(LocalDateTime begin, LocalDateTime end) {
    return inSession(session ->
        orderDao.getAllOrders(session, OrderSort.COMPLETED_BY_PRICE, begin, end));
  }

  @Override
  public Double getEarnedSum(LocalDateTime begin, LocalDateTime end) {
    return inSession(session -> orderDao.getEarnedSum(session, begin, end));
  }

  @Override
  public Long getCountCompletedOrders(LocalDateTime begin, LocalDateTime end) {
    return inSession(session -> orderDao.getCountCompletedOrders(session, begin, end));
  }

  @Override
  public Optional<Order> getOrder(Long orderId) {
    return inSession(session -> orderDao.findWithBooks(session, orderId));
  }

  @Override
  public List<Request> getRequests() {
    return inSession(session -> requestDao.getAllRequests(session, RequestSort.ID));
  }

  @Override
  public LinkedHashMap<Book, Long> getRequestsByCount() {
    return inSession(session -> requestDao.getRequests(session, RequestSort.COUNT));
  }

  @Override
  public LinkedHashMap<Book, Long> getRequestsByPrice() {
    return inSession(session -> requestDao.getRequests(session, RequestSort.PRICE));
  }

  public boolean isAvailable(Session session, long bookId, int requestAmount) {
    Optional<Book> book = bookDao.getBookById(session, bookId);
    return book.filter(value -> value.getAmount() >= requestAmount).isPresent();
  }

  @Override
  public void createRequests(Session session, long orderId) {
    log.info("Создание запросов для заказа [{}]...", orderId);
    Optional<Order> order = orderDao.findWithBooks(session, orderId);
    if (order.isEmpty()) {
      throw new RuntimeException("Не удалось создать запросы для заказа " + orderId
          + " : заказ с таким id не найден");
    }
    boolean completed = true;
    for (Map.Entry<Long, Integer> entry : order.get().getBooks().entrySet()) {
      Optional<Book> book = bookDao.getBookById(session, entry.getKey());
      if (book.isEmpty()) {
        throw new RuntimeException("Попытка создать заказ с несуществующей книгой (id=["
            + entry.getKey() + "])");
      }
      requestDao.addRequest(session, book.get(), entry.getValue());
      if (!isAvailable(session, entry.getKey(), entry.getValue())) {
        completed = false;
      }
    }
    log.info("Созданы запросы для заказа [{}].", orderId);
    if (completed) {
      log.info("При создании заказа [{}] обнаружено, что все книги есть в наличии. Выполняем "
          + "заказ...", orderId);
      completeOrder(session, order.get(), LocalDateTime.now());
    }
  }

  @Override
  public Optional<Request> getRequest(long requestId) {
    return inSession(session -> requestDao.getRequestById(session, requestId));
  }

  @Override
  public void importOrder(Order order) {
    inSessionVoid(session -> {
      Optional<Order> findOrder = orderDao.findWithBooks(session, order.getId());
      if (findOrder.isPresent()) {
        requestDao.closeRequests(session, findOrder.get().getBooks());
        orderDao.update(session, order);
      } else {
        orderDao.addOrder(session, order);
      }
      createRequests(session, order.getId());
    });
  }

  @Override
  public void importRequest(Request request) throws IllegalArgumentException {
    inSessionVoid(session -> {
      log.info("Импорт запроса {}...", request.getId());
      Optional<Request> findRequest = requestDao.getRequestById(session, request.getId());
      if (findRequest.isPresent()) {
        log.warn("Ошибка при импорте: Запрос [{}] уже есть в магазине", request.getId());
      } else {
        requestDao.importRequest(session, request);
      }
    });
  }

  @Override
  public <T extends Item> void importItem(T item) throws IllegalArgumentException {
    if (item instanceof Book) {
      importBook((Book) item);
    } else if (item instanceof Order) {
      importOrder((Order) item);
    } else if (item instanceof Request) {
      importRequest((Request) item);
    }
  }
}
