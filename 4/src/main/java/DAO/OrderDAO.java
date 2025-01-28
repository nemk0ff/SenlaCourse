package DAO;

import model.impl.Order;
import sorting.OrderSort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    void setOrderStatus(long orderId, String status);

    void addOrder(Order order);

    void rewriteOrder(Order order);

    List<Order> getAllOrders(OrderSort sortType, LocalDateTime begin, LocalDateTime end);

    Optional<Order> getOrderById(long order_id);

    Double getEarnedSum(LocalDateTime begin, LocalDateTime end);

    Long getCountCompletedOrders(LocalDateTime begin, LocalDateTime end);
}
