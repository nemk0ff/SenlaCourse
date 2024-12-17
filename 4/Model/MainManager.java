package Model;

import Model.Items.Impl.Book;
import Model.Items.Impl.Order;
import Model.Items.Impl.Request;
import Model.Items.Item;
import Model.Items.OrderStatus;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MainManager {
    void addBook(long bookId, Integer amount, LocalDate addDate);

    void writeOff(long bookId, Integer amount, LocalDate saleDate);

    boolean cancelOrder(long orderId);

    boolean setOrderStatus(long orderId, OrderStatus status);

    void createOrder(Map<Long, Integer> booksIds, String clientName, LocalDate createDate);

    void addRequest(long bookId);

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

    <T extends Item> void importItem(T Item);
}
