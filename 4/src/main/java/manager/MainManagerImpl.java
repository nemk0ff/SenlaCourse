package manager;

import annotations.ComponentDependency;
import annotations.ConfigProperty;
import config.ConfigurationManager;
import dao.impl.BookDaoImpl;
import dao.impl.OrderDaoImpl;
import dao.impl.RequestDaoImpl;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import model.Item;
import model.OrderStatus;
import model.impl.Book;
import model.impl.Order;
import model.impl.Request;
import sorting.BookSort;
import sorting.OrderSort;
import sorting.RequestSort;

/**
 * {@code MainManagerImpl} - Реализация интерфейса {@link MainManager}, предоставляющая методы
 * для управления основными операциями приложения.
 */
@Slf4j
@Data
public class MainManagerImpl implements MainManager {
  @ConfigProperty(propertyName = "mark.orders.completed", type = boolean.class)
  private boolean markOrdersCompleted;

  @ComponentDependency
  BookDaoImpl bookDao;
  @ComponentDependency
  OrderDaoImpl orderDao;
  @ComponentDependency
  RequestDaoImpl requestDao;

  public MainManagerImpl() {
    ConfigurationManager.configure(this);
    log.debug("MainManagerImpl инициализирован, markOrdersCompleted = {}", markOrdersCompleted);
  }

  @Override
  public void addBook(long id, Integer amount, LocalDateTime deliveredDate)
      throws IllegalArgumentException {
    bookDao.add(id, amount, deliveredDate);
    if (markOrdersCompleted) {
      log.info("Обновляем заказы после добавления книги...");
      updateOrders(deliveredDate);
      log.info("Все заказы успешно обновлены после добавления книги [{}].", id);
    }
  }

  @Override
  public void writeOff(long id, Integer amount, LocalDateTime writeOffDate)
      throws IllegalArgumentException {
    bookDao.writeOff(id, amount, writeOffDate);
    updateOrders(writeOffDate);
  }

  @Override
  public Optional<Book> getBook(long id) {
    return bookDao.getBookById(id);
  }

  @Override
  public List<Book> getAllBooks() {
    return bookDao.getAllBooks(BookSort.ID);
  }

  @Override
  public List<Book> getAllBooksByName() {
    return bookDao.getAllBooks(BookSort.NAME);
  }

  @Override
  public List<Book> getAllBooksByDate() {
    return bookDao.getAllBooks(BookSort.PUBLICATION_DATE);
  }

  @Override
  public List<Book> getAllBooksByPrice() {
    return bookDao.getAllBooks(BookSort.PRICE);
  }

  @Override
  public List<Book> getAllBooksByAvailable() {
    return bookDao.getAllBooks(BookSort.STATUS);
  }

  @Override
  public List<Book> getAllStaleBooksByDate() throws IllegalArgumentException {
    return bookDao.getAllBooks(BookSort.STALE_BY_DATE);
  }

  @Override
  public List<Book> getAllStaleBooksByPrice() throws IllegalArgumentException {
    return bookDao.getAllBooks(BookSort.STALE_BY_PRICE);
  }

  @Override
  public boolean containsBook(long bookId) {
    return bookDao.containsBook(bookId);
  }

  @Override
  public void importBook(Book book) throws IllegalArgumentException {
    bookDao.importBook(book);
    log.info("Обновляем заказы после импорта книги...");
    updateOrders(LocalDateTime.now());
    log.info("Все заказы успешно обновлены после импорта книги [{}].", book.getId());
  }


  private void updateOrders(LocalDateTime updateDate) {
    for (Order order : getAllOrders()) {
      log.debug("Обновляем заказ: {}...", order.getInfoAbout());
      updateOrder(order, updateDate);
      log.debug("Заказ успешно обновлен: {}.", order.getInfoAbout());
    }
  }

  private void updateOrder(Order order, LocalDateTime updateDate) {
    if (order.getStatus() == OrderStatus.NEW) {
      for (Map.Entry<Long, Integer> entry : order.getBooks().entrySet()) {
        Optional<Book> optionalBook = getBook(entry.getKey());
        if (optionalBook.isEmpty()) {
          log.debug("В заказе [{}] есть {} книг [{}], но на складе таких книг нет",
              order.getId(), entry.getValue(), entry.getValue());
          return;
        } else if (optionalBook.get().getAmount() < entry.getValue()) {
          log.debug("В заказе [{}] есть {} книг [{}], но на складе таких книг только {}",
              order.getId(), entry.getValue(), entry.getValue(), optionalBook.get().getAmount());
          return;
        }
      }
      log.info("Есть все необходимые книги для заказа [{}], выполняем заказ...", order.getId());
      completeOrder(order, updateDate);
      log.info("Заказ [{}] успешно выполнен.", order.getId());
    }
  }

  private void completeOrder(Order order, LocalDateTime completeDate) {
    orderDao.setOrderStatus(order.getId(), "COMPLETED");

    requestDao.closeRequests(order.getBooks());

    for (Map.Entry<Long, Integer> entry : order.getBooks().entrySet()) {
      bookDao.writeOff(entry.getKey(), entry.getValue(), completeDate);
    }
  }

  @Override
  public long createRequest(long bookId, int amount) {
    return requestDao.addRequest(bookId, amount);
  }

  @Override
  public Order createOrder(Map<Long, Integer> booksIds, String clientName,
                           LocalDateTime orderDate) {
    long newOrderId = orderDao.addOrder(new Order(booksIds,
        getPrice(booksIds.keySet().stream().toList()), OrderStatus.NEW, orderDate, clientName));

    createRequests(newOrderId);
    return orderDao.getOrderById(newOrderId).orElse(new Order());
  }

  @Override
  public void cancelOrder(long orderId) throws IllegalArgumentException {
    log.debug("Отменяем заказ [{}]...", orderId);
    Optional<Order> order = orderDao.getOrderById(orderId);
    if (order.isEmpty()) {
      throw new IllegalArgumentException("Заказ [" + orderId + "] не найден");
    }
    if (order.get().getStatus() == OrderStatus.NEW) {
      orderDao.setOrderStatus(orderId, "CANCELED");
      requestDao.closeRequests(order.get().getBooks());
      log.info("Заказ [{}] успешно отменен", orderId);
    } else {
      throw new IllegalArgumentException("Невозможно отменить заказ, статус которого не NEW");
    }
  }

  @Override
  public void setOrderStatus(long orderId, OrderStatus status) {
    orderDao.setOrderStatus(orderId, status.toString());
    if (status == OrderStatus.NEW) {
      createRequests(orderId);
    }
  }


  @Override
  public List<Order> getAllOrders() {
    return orderDao.getAllOrders(OrderSort.ID, null, null);
  }

  @Override
  public List<Order> getAllOrdersByDate() {
    return orderDao.getAllOrders(OrderSort.COMPLETE_DATE, null, null);
  }

  @Override
  public List<Order> getAllOrdersByPrice() {
    return orderDao.getAllOrders(OrderSort.PRICE, null, null);
  }

  @Override
  public List<Order> getAllOrdersByStatus() {
    return orderDao.getAllOrders(OrderSort.STATUS, null, null);
  }

  @Override
  public List<Order> getCompletedOrdersByDate(LocalDateTime begin, LocalDateTime end) {
    return orderDao.getAllOrders(OrderSort.COMPLETED_BY_DATE, begin, end);
  }

  @Override
  public List<Order> getCompletedOrdersByPrice(LocalDateTime begin, LocalDateTime end) {
    return orderDao.getAllOrders(OrderSort.COMPLETED_BY_PRICE, begin, end);
  }

  @Override
  public Double getEarnedSum(LocalDateTime begin, LocalDateTime end) {
    return orderDao.getEarnedSum(begin, end);
  }

  @Override
  public Long getCountCompletedOrders(LocalDateTime begin, LocalDateTime end) {
    return orderDao.getCountCompletedOrders(begin, end);
  }

  @Override
  public Optional<Order> getOrder(Long orderId) {
    return orderDao.getOrderById(orderId);
  }

  @Override
  public List<Request> getRequests() {
    return requestDao.getAllRequests(RequestSort.ID);
  }

  @Override
  public LinkedHashMap<Long, Long> getRequestsByCount() {
    return requestDao.getRequests(RequestSort.COUNT);
  }

  @Override
  public LinkedHashMap<Long, Long> getRequestsByPrice() {
    return requestDao.getRequests(RequestSort.PRICE);
  }

  private List<Book> getBooks(List<Long> booksIds) {
    return bookDao.getBooks(booksIds);
  }

  public double getPrice(List<Long> booksIds) {
    return getBooks(booksIds).stream().mapToDouble(Book::getPrice).sum();
  }

  public boolean isAvailable(long bookId, int requestAmount) {
    Optional<Book> book = getBook(bookId);
    return book.filter(value -> value.getAmount() >= requestAmount).isPresent();
  }

  @Override
  public void createRequests(long orderId) {
    log.info("Создание запросов для заказа [{}]...", orderId);
    Optional<Order> order = getOrder(orderId);
    if (order.isEmpty()) {
      throw new RuntimeException("Не удалось создать запросы для заказа " + orderId
          + " : заказ с таким id не найден");
    }
    boolean completed = true;
    for (Map.Entry<Long, Integer> entry : order.get().getBooks().entrySet()) {
      Optional<Book> book = getBook(entry.getKey());
      createRequest(entry.getKey(), entry.getValue());
      if (book.isPresent() && !isAvailable(entry.getKey(), entry.getValue())) {
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

  @Override
  public Optional<Request> getRequest(long requestId) {
    return requestDao.getRequestById(requestId);
  }

  @Override
  public void importOrder(Order order) throws IllegalArgumentException {
    Optional<Order> findOrder = getOrder(order.getId());
    if (findOrder.isPresent()) {
      requestDao.closeRequests(findOrder.get().getBooks());
      orderDao.rewriteOrder(order);
    } else {
      orderDao.addOrder(order);
    }
    createRequests(order.getId());
  }

  @Override
  public void importRequest(Request request) throws IllegalArgumentException {
    log.info("Импорт запроса {}...", request.getId());
    if (!containsBook((request).getBookId())) {
      log.warn("Ошибка при импорте: Запрос [{}] - запрос на книгу, которой не существует",
          request.getId());
      return;
    }
    Optional<Request> findRequest = getRequest(request.getId());
    if (findRequest.isPresent()) {
      log.warn("Ошибка при импорте: Запрос [{}] уже есть в магазине", request.getId());
    } else {
      requestDao.importRequest(request);
    }
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
