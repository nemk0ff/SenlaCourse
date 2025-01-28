package manager;

import DAO.impl.BookDAOImpl;
import DAO.impl.OrderDAOImpl;
import DAO.impl.RequestDAOImpl;
import annotations.ConfigProperty;
import annotations.DIComponentDependency;
import config.ConfigurationManager;
import lombok.Data;
import model.RequestStatus;
import model.impl.*;
import model.Item;
import model.OrderStatus;
import sorting.BookSort;
import sorting.OrderSort;
import sorting.RequestSort;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class MainManagerImpl implements MainManager {
    @ConfigProperty(propertyName = "mark.orders.completed", type = boolean.class)
    private boolean markOrdersCompleted;

    @DIComponentDependency
    BookDAOImpl bookDAO;
    @DIComponentDependency
    OrderDAOImpl orderDAO;
    @DIComponentDependency
    RequestDAOImpl requestDAO;

    public MainManagerImpl() {
        ConfigurationManager.configure(this);
    }

    @Override
    public void addBook(long id, Integer amount, LocalDateTime deliveredDate) throws IllegalArgumentException {
        bookDAO.add(id, amount, deliveredDate);
        if (markOrdersCompleted) {
            updateOrders(deliveredDate);
        }
    }

    @Override
    public void writeOff(long id, Integer amount, LocalDateTime writeOffDate) throws IllegalArgumentException {
        bookDAO.writeOff(id, amount, writeOffDate);
        updateOrders(writeOffDate);
    }

    @Override
    public Optional<Book> getBook(long id) {
        return bookDAO.getBookById(id);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks(BookSort.ID);
    }

    @Override
    public List<Book> getAllBooksByName() {
        return bookDAO.getAllBooks(BookSort.NAME);
    }

    @Override
    public List<Book> getAllBooksByDate() {
        return bookDAO.getAllBooks(BookSort.PUBLICATION_DATE);
    }

    @Override
    public List<Book> getAllBooksByPrice() {
        return bookDAO.getAllBooks(BookSort.PRICE);
    }

    @Override
    public List<Book> getAllBooksByAvailable() {
        return bookDAO.getAllBooks(BookSort.STATUS);
    }

    @Override
    public List<Book> getAllStaleBooksByDate() throws IllegalArgumentException {
        return bookDAO.getAllBooks(BookSort.STALE_BY_DATE);
    }

    @Override
    public List<Book> getAllStaleBooksByPrice() throws IllegalArgumentException {
        return bookDAO.getAllBooks(BookSort.STALE_BY_PRICE);
    }

    @Override
    public boolean containsBooks(List<Long> booksIds) {
        for (long id : booksIds) {
            if (!containsBook(id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsBook(long bookId) {
        return bookDAO.containsBook(bookId);
    }

    @Override
    public void importBook(Book book) throws IllegalArgumentException {
        bookDAO.importBook(book);
        updateOrders(LocalDateTime.now());
    }


    private void updateOrders(LocalDateTime updateDate) {
        for (Order order : getAllOrders()) {
            updateOrder(order, updateDate);
        }
    }

    private void updateOrder(Order order, LocalDateTime updateDate) {
        // Если все книги для заказа есть, то мы завершаем заказ
        if (order.getStatus() == OrderStatus.NEW) {
            for (Map.Entry<Long, Integer> entry : order.getBooks().entrySet()) {
                Optional<Book> optionalBook = getBook(entry.getKey());
                if (optionalBook.isEmpty() || optionalBook.get().getAmount() < entry.getValue()) {
                    return;
                }
            }
            completeOrder(order, updateDate);
        }
    }

    private void completeOrder(Order order, LocalDateTime completeDate) {
        orderDAO.setOrderStatus(order.getId(), "COMPLETED");

        requestDAO.closeRequests(order.getBooks());

        for (Map.Entry<Long, Integer> entry : order.getBooks().entrySet()) {
            bookDAO.writeOff(entry.getKey(), entry.getValue(), completeDate);
        }
    }

    @Override
    public void createRequest(long bookId, int amount) {
        requestDAO.addRequest(bookId, amount);
    }

    @Override
    public void createOrder(Map<Long, Integer> booksIds, String clientName, LocalDateTime orderDate) {
        Order newOrder = new Order(booksIds, getPrice(booksIds.keySet().stream().toList()),
                OrderStatus.NEW, orderDate, clientName);

        createRequests(newOrder);

        orderDAO.addOrder(newOrder);
    }

    @Override
    public void cancelOrder(long order_id) throws IllegalArgumentException {
        Optional<Order> order = orderDAO.getOrderById(order_id);
        if (order.isEmpty()) {
            throw new IllegalArgumentException("Заказ " + order_id + " не найден");
        }
        if (order.get().getStatus() == OrderStatus.NEW) {
            orderDAO.setOrderStatus(order_id, "CANCELED");
            requestDAO.closeRequests(order.get().getBooks());
        } else {
            throw new IllegalArgumentException("Невозможно отменить заказ, статус которого не NEW");
        }
    }

    @Override
    public void setOrderStatus(long orderId, OrderStatus status) {
        orderDAO.setOrderStatus(orderId, status.toString());
    }


    @Override
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders(OrderSort.ID, null, null);
    }

    @Override
    public List<Order> getAllOrdersByDate() {
        return orderDAO.getAllOrders(OrderSort.COMPLETE_DATE, null, null);
    }

    @Override
    public List<Order> getAllOrdersByPrice() {
        return orderDAO.getAllOrders(OrderSort.PRICE, null, null);
    }

    @Override
    public List<Order> getAllOrdersByStatus() {
        return orderDAO.getAllOrders(OrderSort.STATUS, null, null);
    }

    @Override
    public List<Order> getCompletedOrdersByDate(LocalDateTime begin, LocalDateTime end) {
        return orderDAO.getAllOrders(OrderSort.COMPLETED_BY_DATE, begin, end);
    }

    @Override
    public List<Order> getCompletedOrdersByPrice(LocalDateTime begin, LocalDateTime end) {
        return orderDAO.getAllOrders(OrderSort.COMPLETED_BY_PRICE, begin, end);
    }

    @Override
    public Double getEarnedSum(LocalDateTime begin, LocalDateTime end) {
        return orderDAO.getEarnedSum(begin, end);
    }

    @Override
    public Long getCountCompletedOrders(LocalDateTime begin, LocalDateTime end) {
        return orderDAO.getCountCompletedOrders(begin, end);
    }

    @Override
    public Optional<Order> getOrder(Long orderId) {
        return orderDAO.getOrderById(orderId);
    }

    @Override
    public List<Request> getRequests() {
        return requestDAO.getAllRequests(RequestSort.ID);
    }

    @Override
    public LinkedHashMap<Long, Long> getRequestsByCount() {
        return requestDAO.getRequests(RequestSort.COUNT);
    }

    @Override
    public LinkedHashMap<Long, Long> getRequestsByPrice() {
        return requestDAO.getRequests(RequestSort.PRICE);
    }

    private List<Book> getBooks(List<Long> booksIds) {
        return bookDAO.getBooks(booksIds);
    }

    public double getPrice(List<Long> booksIds) {
        return getBooks(booksIds).stream().mapToDouble(Book::getPrice).sum();
    }

    public boolean isAvailable(long bookId, int requestAmount) {
        Optional<Book> book = getBook(bookId);
        return book.filter(value -> value.getAmount() >= requestAmount).isPresent();
    }

    @Override
    public void createRequests(Order order) {
        boolean completed = true;
        for (Map.Entry<Long, Integer> entry : order.getBooks().entrySet()) {
            Optional<Book> book = getBook(entry.getKey());
            createRequest(entry.getKey(), entry.getValue());
            if (book.isPresent() && !isAvailable(entry.getKey(), entry.getValue())) {
                completed = false;
            }
        }
        // Если все книги есть, то мы их списываем и закрываем заказ
        if (completed) {
            completeOrder(order, LocalDateTime.now());
        }
    }

    @Override
    public Optional<Request> getRequest(long requestId) {
        return requestDAO.getRequestById(requestId);
    }

    @Override
    public void importOrder(Order order) throws IllegalArgumentException {
        // Если импортируем заказ на книгу, которой нет в магазине вообще
        if (!containsBooks((order).getBooks().keySet().stream().toList())) {
            throw new IllegalArgumentException("В импортируемом заказе " + order.getId() + " есть несуществующие книги");
        }

        Optional<Order> findOrder = getOrder(order.getId());
        if (findOrder.isPresent()) {
            // При копировании меняется состав заказа, нужно закрыть старые запросы
            requestDAO.closeRequests(findOrder.get().getBooks());
            // Перезаписываем заказ
            orderDAO.rewriteOrder(order);
        } else {
            orderDAO.addOrder(order);
        }
        // Открываем новые запросы, соответствующие составу импортируемого заказа
        createRequests(order);
    }

    @Override
    public void importRequest(Request request) throws IllegalArgumentException {
        // Если импортируем запрос на книгу, которой нет в магазине вообще
        if (!containsBook((request).getBookId())) {
            throw new IllegalArgumentException("Запрос " + request.getId() + " - запрос на книгу, которой не существует");
        }
        Optional<Request> findRequest = getRequest(request.getId());
        if (findRequest.isPresent()) {
            throw new IllegalArgumentException("Запрос [" + request.getId() + "] уже есть в магазине");
        } else {
            requestDAO.importRequest(request);
        }
    }

    @Override
    public <T extends Item> void importItem(T item) throws IllegalArgumentException {
        if (item instanceof Book) {
            importBook((Book) item);
        } else if (item instanceof Order) {
            importOrder((Order) item);
        } else if (item instanceof Request) {
            importRequest((Request) item);
        }
    }
}
