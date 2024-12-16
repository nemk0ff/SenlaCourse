package Model;

import java.util.List;
import java.util.Map;

public interface OrdersManager {
    boolean cancelOrder(long orderId);

    boolean setOrderStatus(long orderId, OrderStatus status);

    void closeRequests(Map<Long, Integer> book);

    void closeRequest(long bookId, int count);

    void addOrder(Order order);

    List<Order> getOrders();

    Order getOrder(long orderId);

    Request getRequest(long requestId);

    List<Request> getRequests();

    void addRequest(long bookId);

    void importRequest(Request request);
}
