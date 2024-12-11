package Model;

import java.util.ArrayList;
import java.util.List;

public class OrdersManagerImpl implements OrdersManager {
    private final List<Order> orders;
    private final List<Request> requests;

    public OrdersManagerImpl() {
        orders = new ArrayList<>();
        requests = new ArrayList<>();
    }

    // Закрыть запросы по книге
    @Override
    public void closeRequests(List<Book> books) {
        for (Book book: books) {
            // Если книга доступна, то на неё не может быть запросов
            // Мы их закрыли, когда добавляли книгу
            if(book.isAvailable()) {
                continue;
            }
            closeRequest(book);
        }
    }

    @Override
    public void closeRequest(Book book){
        for(Request request : requests){
            if (request.getBook().equals(book) && request.getStatus() == RequestStatus.OPEN) {
                request.closeRequest();
                break; // выходим из цикла, чтобы списать 1 запрос на книгу, а не все
            }
        }
    }

    @Override
    public void addRequest(Book book) {
        requests.add(new Request(book));
    }

    // Добавить заказ
    @Override
    public void addOrder(Order order) { orders.add(order); }

    // Отменить заказ
    @Override
    public void cancelOrder(Order order) {
        // Если заказ не выполнен, то отменяем его
        for (Order it : orders) {
            if (it.getBooks().equals(order.getBooks())
                    && it.getStatus() == OrderStatus.NEW
                    && it.getClientName().equals((order.getClientName()))) {
                it.setStatus(OrderStatus.CANCELED);
            }
        }
        // При отмене заказа нужно закрыть запросы на книги этого заказа
        closeRequests(order.getBooks());
    }

    // Изменить статус заказа
    @Override
    public void setOrderStatus(Order order, OrderStatus status) {
        for (Order orderIt: getOrders()) {
            if (orderIt.equals(order)) {
                // Если статус заказа изменился с NEW на не NEW
                // То нужно закрыть запросы на книги этого заказа
                if (orderIt.getStatus() == OrderStatus.NEW && status != OrderStatus.NEW) {
                    closeRequests(orderIt.getBooks());
                    orderIt.setStatus(status);
                }
                return;
            }
        }
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
