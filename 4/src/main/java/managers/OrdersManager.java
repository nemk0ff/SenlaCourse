package managers;

import model.impl.Order;
import model.OrderStatus;
import model.impl.Request;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrdersManager {
    void cancelOrder(long order_id) throws IllegalArgumentException;

    void setOrderStatus(long order_id, OrderStatus status) throws IllegalArgumentException;

    void addOrder(Order order);

    List<Order> getOrders();

    Optional<Order> getOrder(long order_id);


    Optional<Request> getRequest(long request_id);

    List<Request> getRequests();

    void addRequest(long book_id, int amount);

    void importRequest(Request request);

    void closeRequest(long book_id, int count);

    void closeRequests(Map<Long, Integer> book);
}
