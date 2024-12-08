package Model;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

public interface MainManager{
    void addBook(Book book, Integer amount, LocalDate addDate);
    void writeOff(Book book, Integer amount, LocalDate saleDate);
    void cancelOrder(Order order);
    void setOrderStatus(Order order, OrderStatus status);
    void createOrder(Book book, String clientName, LocalDate createDate);
    void addRequest(Book book);

    List<Book> getBooks();
    List<Book> getBooksByAlphabet();
    List<Book> getBooksByDate();
    List<Book> getBooksByPrice();
    List<Book> getBooksByAvailable();
    List<Book> sortBooks(List<Book> books, Comparator<Book> comparator);

    List<Order> getOrders();
    List<Order> getOrdersByDate();
    List<Order> getOrdersByPrice();
    List<Order> getOrdersByStatus();
    List<Order> sortOrders(List<Order> orders, Comparator<Order> comparator);

    List<Request> getRequests();
    Map<Book, Long> groupRequestsByBook(List<Request> requests);
    LinkedHashMap<Book, Long> getRequestsByCount();
    LinkedHashMap<Book, Long> getRequestsByPrice();

    List<Order> getCompletedOrdersByDate(LocalDate begin, LocalDate end);
    List<Order> getCompletedOrdersByPrice(LocalDate begin, LocalDate end);

    Double getEarnedSum(LocalDate begin, LocalDate end);

    Long getCountCompletedOrders(LocalDate begin, LocalDate end);

    Stream<Book> getStaleBooks();
    List<Book> getStaleBooksByDate();
    List<Book> getStaleBooksByPrice();

    Optional<Order> getOrderDetails(String client, Book book);
    Optional<Book> getBookDetails(Book book);
}
