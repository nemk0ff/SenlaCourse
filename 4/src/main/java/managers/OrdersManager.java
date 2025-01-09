package managers;

import DTO.OrdersManagerDTO;
import model.impl.Order;
import model.OrderStatus;
import model.impl.Request;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrdersManager {
    boolean cancelOrder(long orderId);

    boolean setOrderStatus(long orderId, OrderStatus status);

    void initialize(OrdersManagerDTO ordersManagerDTO);

    void closeRequests(Map<Long, Integer> book);

    void closeRequest(long bookId, int count);

    void addOrder(Order order);

    Map<Long, Order> getOrders();

    List<Order> getOrdersAsList();

    Map<Long, Request> getRequests();

    List<Request> getRequestsAsList();

    Optional<Order> getOrder(long orderId);

    Optional<Request> getRequest(long requestId);

    void addRequest(long bookId, int amount);

    void importRequest(Request request);
}
