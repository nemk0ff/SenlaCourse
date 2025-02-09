package manager;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.Item;
import model.OrderStatus;
import model.impl.Book;
import model.impl.Order;
import model.impl.Request;

/**
 * {@code MainManager} - Интерфейс, определяющий поведение для менеджера, управляющего основными
 * операциями приложения, связанными с книгами, заказами и запросами.
 */
public interface MainManager {
  void addBook(long bookId, Integer amount,
               LocalDateTime addDate) throws IllegalArgumentException;

  void writeOff(long bookId, Integer amount,
                LocalDateTime saleDate) throws IllegalArgumentException;

  Optional<Book> getBook(long bookId);

  List<Book> getAllBooks();

  List<Book> getAllBooksByName();

  List<Book> getAllBooksByDate();

  List<Book> getAllBooksByPrice();

  List<Book> getAllBooksByAvailable();

  List<Book> getAllStaleBooksByDate();

  List<Book> getAllStaleBooksByPrice();

  boolean containsBooks(List<Long> booksIds) throws IllegalArgumentException;

  boolean containsBook(long bookId) throws IllegalArgumentException;

  void importBook(Book book) throws IllegalArgumentException;


  Order createOrder(Map<Long, Integer> booksIds, String clientName, LocalDateTime createDate);

  void cancelOrder(long orderId);

  void setOrderStatus(long orderId, OrderStatus status);

  Optional<Order> getOrder(Long orderId);

  List<Order> getAllOrders();

  List<Order> getAllOrdersByDate();

  List<Order> getAllOrdersByPrice();

  List<Order> getAllOrdersByStatus();

  List<Order> getCompletedOrdersByDate(LocalDateTime begin, LocalDateTime end);

  List<Order> getCompletedOrdersByPrice(LocalDateTime begin, LocalDateTime end);

  Long getCountCompletedOrders(LocalDateTime begin, LocalDateTime end);

  void importOrder(Order order);


  long createRequest(long bookId, int amount);

  void createRequests(Order order);

  Optional<Request> getRequest(long requestId);

  List<Request> getRequests();

  LinkedHashMap<Long, Long> getRequestsByCount();

  LinkedHashMap<Long, Long> getRequestsByPrice();

  void importRequest(Request request);


  Double getEarnedSum(LocalDateTime begin, LocalDateTime end);

  <T extends Item> void importItem(T item);
}
