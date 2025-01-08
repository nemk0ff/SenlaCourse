package managers.impl;

import DTO.*;
import managers.OrdersManager;
import model.impl.Order;
import model.OrderStatus;
import model.impl.Request;
import model.RequestStatus;

import java.time.LocalDate;
import java.util.*;

public class OrdersManagerImpl implements OrdersManager {
    private final Map<Long, Order> orders;
    private final Map<Long, Request> requests;

    public OrdersManagerImpl(OrdersManagerDTO ordersManagerDTO) {
        this.orders = new HashMap<>();
        long maxOrderId = 0;
        for (OrderDTO orderDTO : ordersManagerDTO.orders()) {
            Order order = new Order(orderDTO);
            this.orders.put(orderDTO.id(), order);
            maxOrderId = Math.max(maxOrderId, order.getId());
        }
        Order.setCounter(maxOrderId);

        this.requests = new HashMap<>();
        long maxRequestId = 0;
        for (RequestDTO requestDTO : ordersManagerDTO.requests()) {
            Request request = new Request(requestDTO);
            this.requests.put(requestDTO.id(), request);
            maxRequestId = Math.max(maxRequestId, request.getId());
        }
        Request.setCounter(maxRequestId);
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
        for (Request request : getRequestsAsList()) {
            if (request.getBookId() == bookId && request.getStatus() == RequestStatus.OPEN
                    && count == request.getAmount()) {
                request.closeRequest();
                break;
            }
        }
    }

    @Override
    public void addRequest(long bookId, int amount) {
        Request request = new Request(bookId, amount);
        requests.put(request.getId(), request);
    }

    @Override
    public void addOrder(Order order) {
        orders.put(order.getId(), order);
    }

    @Override
    public boolean cancelOrder(long orderId) {
        Optional<Order> order = getOrder(orderId);
        if (order.isPresent() && order.get().getStatus() == OrderStatus.NEW) {
            order.get().setStatus(OrderStatus.CANCELED);
            closeRequests(order.get().getBooks());
            return true;
        }
        return false;
    }

    // Изменить статус заказа
    @Override
    public boolean setOrderStatus(long orderId, OrderStatus status) {
        for (Order orderIt : getOrdersAsList()) {
            if (orderIt.getId() == orderId) {
                // Если статус заказа изменился с NEW на не NEW
                // То нужно закрыть запросы на книги этого заказа
                if (orderIt.getStatus() == OrderStatus.NEW && status != OrderStatus.NEW) {
                    closeRequests(orderIt.getBooks());
                    orderIt.setStatus(status);
                    orderIt.setCompleteDate(LocalDate.now());
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public Map<Long, Order> getOrders() {
        return orders;
    }

    @Override
    public List<Order> getOrdersAsList() {
        return orders.values().stream().toList();
    }

    @Override
    public Map<Long, Request> getRequests() {
        return requests;
    }

    @Override
    public List<Request> getRequestsAsList() {
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
