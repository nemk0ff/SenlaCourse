package managers.impl;

import DAO.impl.OrderDAOImpl;
import DAO.impl.RequestDAOImpl;
import annotations.DIComponentDependency;
import managers.OrdersManager;
import model.impl.Order;
import model.OrderStatus;
import model.impl.Request;

import java.util.*;

public class OrdersManagerImpl implements OrdersManager {
    @DIComponentDependency
    OrderDAOImpl orderDAO;
    @DIComponentDependency
    RequestDAOImpl requestDAO;

    public OrdersManagerImpl() {
    }

    @Override
    public void cancelOrder(long order_id) throws IllegalArgumentException {
        Optional<Order> order = getOrder(order_id);
        if (order.isEmpty()) {
            throw new IllegalArgumentException("Заказ " + order_id + " не найден");
        }
        if (order.get().getStatus() == OrderStatus.NEW) {
            orderDAO.setOrderStatus(order_id, "CANCELED");
            closeRequests(order.get().getBooks());
        } else {
            throw new IllegalArgumentException("Невозможно отменить заказ, статус которого не NEW");
        }
    }

    @Override
    public void setOrderStatus(long order_id, OrderStatus status) throws IllegalArgumentException {
        Optional<Order> order = getOrder(order_id);
        if (order.isEmpty()) {
            throw new IllegalArgumentException("Заказ " + order_id + " не найден");
        }
        // Если статус заказа изменился с NEW на не NEW
        // То нужно закрыть запросы на книги этого заказа
        if (order.get().getStatus() == OrderStatus.NEW && status != OrderStatus.NEW) {
            closeRequests(order.get().getBooks());
            orderDAO.setOrderStatus(order_id, status.toString());
        } else {
            throw new IllegalArgumentException("Статус " + order.get().getStatus() + " нельзя изменить на " + status);
        }
    }

    @Override
    public void addOrder(Order order) {
        orderDAO.addOrder(order);
    }

    @Override
    public List<Order> getOrders() {
        return orderDAO.getAllOrders();
    }

    @Override
    public Optional<Order> getOrder(long order_id) {
        return orderDAO.getOrderById(order_id);
    }


    @Override
    public void closeRequests(Map<Long, Integer> books) {
        for (Map.Entry<Long, Integer> book : books.entrySet()) {
            closeRequest(book.getKey(), book.getValue());
        }
    }

    @Override
    public void closeRequest(long book_id, int amount) {
        Optional<Request> request = requestDAO.getRequestByBook(book_id, amount);
        request.ifPresent(value -> requestDAO.closeRequest(value.getId()));
    }

    @Override
    public void addRequest(long book_id, int amount) {
        requestDAO.addRequest(book_id, amount);
    }

    @Override
    public List<Request> getRequests() {
        return requestDAO.getRequests();
    }

    @Override
    public Optional<Request> getRequest(long request_id) {
        return requestDAO.getRequestById(request_id);
    }

    @Override
    public void importRequest(Request request) {
        requestDAO.importRequest(request);
    }
}
