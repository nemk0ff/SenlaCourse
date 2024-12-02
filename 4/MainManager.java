import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MainManager{
    void addBook(Book book, Integer amount);
    void writeOff(Book book, Integer amount);
    void cancelOrder(Order order);
    void setOrderStatus(Order order, OrderStatus status);
    void createOrder(Book book, String clientName);

    List<Book> getBooks();
    List<Book> getBooksByAlphabet();
    List<Book> getBooksByDate();
    List<Book> getBooksByPrice();
    List<Book> getBooksByAvailable();

    List<Order> getOrders();
    List<Order> getOrdersByDate();
    List<Order> getOrdersByPrice();
    List<Order> getOrdersByStatus();

    List<Map.Entry<Book, List<Order>>> getOrdersByBooksByCount();
    List<Map.Entry<Book, List<Order>>> getOrdersByBooksByDate();

    List<Order> getCompletedOrdersByDate(LocalDate begin, LocalDate end);
    List<Order> getCompletedOrdersByPrice(LocalDate begin, LocalDate end);

    Integer getEarnedSum(LocalDate begin, LocalDate end);

    Integer getCountCompletedOrders(LocalDate begin, LocalDate end);

    List<Book> getStaleBooksByDate();
    List<Book> getStaleBooksByPrice();

    void showOrderDetails(String client, Book book);
    void showBookDetails(Book book);
}
