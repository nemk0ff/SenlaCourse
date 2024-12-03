import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public interface MainManager{
    void addBook(Book book, Integer amount);
    void writeOff(Book book, Integer amount, LocalDate saleDate);
    void cancelOrder(Order order);
    void setOrderStatus(Order order, OrderStatus status);
    void createOrder(Book book, String clientName, LocalDate createDate);

    List<Book> getBooks();
    List<Book> getBooksByAlphabet();
    List<Book> getBooksByDate();
    List<Book> getBooksByPrice();
    List<Book> getBooksByAvailable();

    List<Request> getRequests();
    List<Order> getOrders();
    List<Order> getOrdersByDate();
    List<Order> getOrdersByPrice();
    List<Order> getOrdersByStatus();

    SortedMap<Book, Long> getRequestsByCount();
    SortedMap<Book, Long> getRequestsByPrice();

    List<Order> getCompletedOrdersByDate(LocalDate begin, LocalDate end);
    List<Order> getCompletedOrdersByPrice(LocalDate begin, LocalDate end);

    Integer getEarnedSum(LocalDate begin, LocalDate end);

    Long getCountCompletedOrders(LocalDate begin, LocalDate end);

    List<Book> getStaleBooksByDate();
    List<Book> getStaleBooksByPrice();

    void showOrderDetails(String client, Book book);
    void showBookDetails(Book book);
}
