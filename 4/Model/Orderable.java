package Model;

public interface Orderable {
    void cancelOrder(Order order);
    void setOrderStatus(Order order, OrderStatus status);
}
