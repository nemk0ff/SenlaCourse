import java.util.ArrayList;
import java.util.List;

public class OrdersManager implements Orderable{
    private final List<Order> orders;

    public OrdersManager() {
        orders = new ArrayList<>();
    }

    // Закрыть запросы по книге
    void closeRequests(Book book){
        for (Order order : orders) {
            if (order.getBook().equals(book) && order.getStatus() == OrderStatus.NotCompleted) {
                order.setStatus(OrderStatus.Completed);
            }
        }
    }

    // Добавить заказ
    void addOrder(Order order){
        orders.add(order);
    }

    // Отменить заказ
    @Override
    public void cancelOrder(Order order){
        orders.remove(order);
    }

    // Изменить статус заказа
    @Override
    public void setOrderStatus(Order order, OrderStatus status){
        for (Order value : orders) {
            if (value.equals(order)) {
                value.setStatus(status);
            }
        }
    }

    public List<Order> getOrders(){
        return orders;
    }
}
