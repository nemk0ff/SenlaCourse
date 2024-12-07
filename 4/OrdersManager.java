import java.util.List;

public interface OrdersManager {
    void cancelOrder(Order order);
    void setOrderStatus(Order order, OrderStatus status);
    void closeRequests(Book book);
    void addOrder(Order order);
    List<Order> getOrders();
    List<Request> getRequests();
}
