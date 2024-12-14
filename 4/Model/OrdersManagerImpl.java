package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrdersManagerImpl implements OrdersManager {
    private final List<Order> orders;
    private final List<Request> requests;

    public OrdersManagerImpl() {
        orders = new ArrayList<>();
        requests = new ArrayList<>();
    }

    // Закрыть запросы по книге
    @Override
    public void closeRequests(Map<Long, Integer> books) {
        for (Map.Entry<Long, Integer> book : books.entrySet()) {
            closeRequest(book.getKey(), book.getValue());
        }
    }

    @Override
    public void closeRequest(long bookId, int count) {
        int counter = 0;
        for (Request request : requests) {
            if (request.getBook() == bookId && request.getStatus() == RequestStatus.OPEN) {
                request.closeRequest();
                counter++;
                if (counter == count) {
                    break;
                }
            }
        }
    }

    @Override
    public void addRequest(long bookId) {
        requests.add(new Request(bookId));
    }

    // Добавить заказ
    @Override
    public void addOrder(Order order) {
        orders.add(order);
    }

    // Отменить заказ
    @Override
    public boolean cancelOrder(long orderId) {
        for (Order order : orders) {
            if (order.getId() == orderId && order.getStatus() == OrderStatus.NEW) {
                order.setStatus(OrderStatus.CANCELED);
                closeRequests(order.getBooks());
                return true;
            }
        }
        return false;
    }

    // Изменить статус заказа
    @Override
    public boolean setOrderStatus(long orderId, OrderStatus status) {
        for (Order orderIt : getOrders()) {
            if (orderIt.getId() == orderId) {
                // Если статус заказа изменился с NEW на не NEW
                // То нужно закрыть запросы на книги этого заказа
                if (orderIt.getStatus() == OrderStatus.NEW && status != OrderStatus.NEW) {
                    closeRequests(orderIt.getBooks());
                    orderIt.setStatus(status);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public List<Request> getRequests() {
        return requests;
    }
}
