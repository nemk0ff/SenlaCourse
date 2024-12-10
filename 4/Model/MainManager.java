package Model;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public interface MainManager {
    void addBook(Book book, Integer amount, LocalDate addDate);

    void writeOff(Book book, Integer amount, LocalDate saleDate);

    void cancelOrder(Order order);

    void setOrderStatus(Order order, OrderStatus status);

    void createOrder(List<Book> books, String clientName, LocalDate createDate);

    void addRequest(Book book);

    List<Book> getBooks();

    List<Book> getBooksByAlphabet();

    List<Book> getBooksByDate();

    List<Book> getBooksByPrice();

    List<Book> getBooksByAvailable();

    List<Order> getOrdersByDate();

    List<Order> getOrdersByPrice();

    List<Order> getOrdersByStatus();

    LinkedHashMap<Book, Long> getRequestsByCount();

    LinkedHashMap<Book, Long> getRequestsByPrice();

    List<Order> getCompletedOrdersByDate(LocalDate begin, LocalDate end);

    List<Order> getCompletedOrdersByPrice(LocalDate begin, LocalDate end);

    Double getEarnedSum(LocalDate begin, LocalDate end);

    Long getCountCompletedOrders(LocalDate begin, LocalDate end);

    List<Book> getStaleBooksByDate();

    List<Book> getStaleBooksByPrice();

    Optional<Order> getOrderDetails(String client, List<Book> book);

    Optional<Book> getBookDetails(Book book);
}
