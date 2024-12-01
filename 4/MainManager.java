import java.time.LocalDate;
import java.util.List;

public interface MainManager{
    void addBook(Book book, Integer amount);
    void writeOff(Book book, Integer amount);
    void cancelOrder(Order order);
    void setOrderStatus(Order order, OrderStatus status);
    void createOrder(Book book);

    List<Book> getBooks();
    List<Book> getBooksByAlphabet();
    List<Book> getBooksByDate();
    List<Book> getBooksByPrice();
    List<Book> getBooksByAvailable();

    List<Order> getOrders();
    List<Order> getOrdersByDate();
    List<Order> getOrdersByPrice();
    List<Order> getOrdersByStatus();

    List<Order> getCompletedOrdersByDate(LocalDate begin, LocalDate end);
    List<Order> getCompletedOrdersByPrice(LocalDate begin, LocalDate end);

    Integer getEarnedSum(LocalDate begin, LocalDate end);

    Integer getCountCompletedOrders(LocalDate begin, LocalDate end);

    void showOrderDetails(String client, String bookName, String author);
    void showBookDetails(String bookName, String author);
}
