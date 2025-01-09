package managers;

import model.impl.Book;
import model.impl.Order;
import model.Item;
import model.OrderStatus;
import model.impl.Request;

import java.time.LocalDate;
import java.util.*;

public interface MainManager {
    boolean addBook(long bookId, Integer amount, LocalDate addDate);

    boolean writeOff(long bookId, Integer amount, LocalDate saleDate);

    boolean cancelOrder(long orderId);

    boolean setOrderStatus(long orderId, OrderStatus status);

    void createOrder(Map<Long, Integer> booksIds, String clientName, LocalDate createDate);

    void addRequest(long bookId, int amount);

    LibraryManager getLibraryManager();

    OrdersManager getOrdersManager();

    List<Book> getBooks();

    List<Book> getBooksByAlphabet();

    List<Book> getBooksByDate();

    List<Book> getBooksByPrice();

    List<Book> getBooksByAvailable();

    List<Order> getOrders();

    List<Order> getOrdersByDate();

    List<Order> getOrdersByPrice();

    List<Order> getOrdersByStatus();

    List<Request> getRequests();

    LinkedHashMap<Book, Long> getRequestsByCount();

    LinkedHashMap<Book, Long> getRequestsByPrice();

    List<Order> getCompletedOrdersByDate(LocalDate begin, LocalDate end);

    List<Order> getCompletedOrdersByPrice(LocalDate begin, LocalDate end);

    Double getEarnedSum(LocalDate begin, LocalDate end);

    Long getCountCompletedOrders(LocalDate begin, LocalDate end);

    List<Book> getStaleBooksByDate();

    List<Book> getStaleBooksByPrice();

    Optional<Order> getOrder(Long orderId);

    Optional<Book> getBook(long bookId);

    Optional<Request> getRequest(long requestId);

    boolean containsBooks(List<Long> booksIds);

    boolean containsBook(long bookId);

    void createRequests(Order order);

    void importBook(Book book);

    void importOrder(Order order);

    void importRequest(Request request);

    <T extends Item> void importItem(T Item);
}
