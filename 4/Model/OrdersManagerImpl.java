package Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrdersManagerImpl implements OrdersManager {
    private final Map<Long, Order> orders;
    private final Map<Long, Request> requests;

    public OrdersManagerImpl() {
        orders = new HashMap<>();
        requests = new HashMap<>();
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
        for (Request request : getRequests()) {
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
        Request request = new Request(bookId);
        requests.put(request.getId(), request);
    }

    @Override
    public void addOrder(Order order) {
        orders.put(order.getId(),order);
    }

    @Override
    public boolean cancelOrder(long orderId) {
        Optional<Order> order = getOrder(orderId);
        if(order.isPresent() && order.get().getStatus() == OrderStatus.NEW){
            order.get().setStatus(OrderStatus.CANCELED);
            closeRequests(order.get().getBooks());
            return true;
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
        return orders.values().stream().toList();
    }

    @Override
    public List<Request> getRequests() {
        return requests.values().stream().toList();
    }

    @Override
    public Optional<Order> getOrder(long orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public Optional<Request> getRequest(long requestId) {
        return Optional.ofNullable(requests.get(requestId));
    }

    @Override
    public void importRequest(Request request) {
        requests.put(request.getId(), request);
    }
}
