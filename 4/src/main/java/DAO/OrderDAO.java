package DAO;

import model.impl.Order;

import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    void setOrderStatus(long orderId, String status);

    void addOrder(Order order);

    List<Order> getAllOrders();

    Optional<Order> getOrderById(long order_id);
}
