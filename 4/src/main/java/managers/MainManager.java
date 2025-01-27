package managers;

import model.impl.Book;
import model.impl.Order;
import model.Item;
import model.OrderStatus;
import model.impl.Request;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public interface MainManager {
    void addBook(long bookId, Integer amount, LocalDate addDate) throws IllegalArgumentException;

    void writeOff(long bookId, Integer amount, LocalDate saleDate) throws IllegalArgumentException;

    Optional<Book> getBook(long bookId);

    List<Book> getAllBooks();

    List<Book> getAllBooksByAlphabet();

    List<Book> getAllBooksByDate();

    List<Book> getAllBooksByPrice();

    List<Book> getAllBooksByAvailable();

    List<Book> getAllStaleBooksByDate();

    List<Book> getAllStaleBooksByPrice();

    boolean containsBooks(List<Long> booksIds) throws IllegalArgumentException;

    boolean containsBook(long bookId) throws IllegalArgumentException;

    void importBook(Book book) throws IllegalArgumentException;


    void createOrder(Map<Long, Integer> booksIds, String clientName, LocalDate createDate);

    void cancelOrder(long orderId);

    void setOrderStatus(long orderId, OrderStatus status);

    Optional<Order> getOrder(Long orderId);

    List<Order> getOrders();

    List<Order> getOrdersByDate();

    List<Order> getOrdersByPrice();

    List<Order> getOrdersByStatus();

    List<Order> getCompletedOrdersByDate(LocalDate begin, LocalDate end);

    List<Order> getCompletedOrdersByPrice(LocalDate begin, LocalDate end);

    Long getCountCompletedOrders(LocalDate begin, LocalDate end);

    void importOrder(Order order);


    void createRequest(long bookId, int amount);

    void createRequests(Order order);

    Optional<Request> getRequest(long requestId);

    List<Request> getRequests();

    LinkedHashMap<Book, Long> getRequestsByCount();

    LinkedHashMap<Book, Long> getRequestsByPrice();

    void importRequest(Request request);


    Double getEarnedSum(LocalDate begin, LocalDate end);

    <T extends Item> void importItem(T Item);
}
