package DAO;

import model.impl.Order;
import sorting.OrderSort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    void setOrderStatus(long orderId, String status);

    void addOrder(Order order);

    List<Order> getAllOrders(OrderSort sortType, LocalDate begin, LocalDate end);

    Optional<Order> getOrderById(long order_id);

    Double getEarnedSum(LocalDate begin, LocalDate end);

    Long getCountCompletedOrders(LocalDate begin, LocalDate end);
}
