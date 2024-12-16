package Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrdersManager {
    boolean cancelOrder(long orderId);

    boolean setOrderStatus(long orderId, OrderStatus status);

    void closeRequests(Map<Long, Integer> book);

    void closeRequest(long bookId, int count);

    void addOrder(Order order);

    List<Order> getOrders();

    List<Request> getRequests();

    Optional<Order> getOrder(long orderId);

    Optional<Request> getRequest(long requestId);

    void addRequest(long bookId);

    void importRequest(Request request);
}
