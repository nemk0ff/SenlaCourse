public interface OrderManager {
    void cancelOrder(Order order);
    void setOrderStatus(Order order, OrderStatus status);
}
