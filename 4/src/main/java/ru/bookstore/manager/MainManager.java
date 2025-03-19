package ru.bookstore.manager;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ru.bookstore.model.Item;
import ru.bookstore.model.OrderStatus;
import ru.bookstore.model.impl.Book;
import ru.bookstore.model.impl.Order;
import ru.bookstore.model.impl.Request;

public interface MainManager {
  void addBook(Long bookId, Integer amount,
               LocalDateTime addDate) throws IllegalArgumentException;

  void writeOff(Long bookId, Integer amount,
                LocalDateTime saleDate) throws IllegalArgumentException;

  Book getBook(Long bookId);

  List<Book> getAllBooks();

  List<Book> getAllBooksByName();

  List<Book> getAllBooksByDate();

  List<Book> getAllBooksByPrice();

  List<Book> getAllBooksByAvailable();

  List<Book> getAllStaleBooksByDate();

  List<Book> getAllStaleBooksByPrice();

  void importBook(Book book);


  Order createOrder(Map<Long, Integer> booksIds, String clientName, LocalDateTime createDate);

  void cancelOrder(long orderId);

  void setOrderStatus(long orderId, OrderStatus status);

  Order getOrder(Long orderId);

  List<Order> getAllOrders();

  List<Order> getAllOrdersByDate();

  List<Order> getAllOrdersByPrice();

  List<Order> getAllOrdersByStatus();

  List<Order> getCompletedOrdersByDate(LocalDateTime begin, LocalDateTime end);

  List<Order> getCompletedOrdersByPrice(LocalDateTime begin, LocalDateTime end);

  Long getCountCompletedOrders(LocalDateTime begin, LocalDateTime end);

  void importOrder(Order order);


  Long createRequest(Long bookId, Integer amount);

  void createRequests(Long orderId);

  Request getRequest(Long requestId);

  List<Request> getRequests();

  LinkedHashMap<Book, Long> getRequestsByCount();

  LinkedHashMap<Book, Long> getRequestsByPrice();

  void importRequest(Request request);


  Double getEarnedSum(LocalDateTime begin, LocalDateTime end);

  <T extends Item> void importItem(T item);
}
