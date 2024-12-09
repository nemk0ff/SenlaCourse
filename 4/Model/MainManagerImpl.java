package Model;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainManagerImpl implements MainManager {
    private final LibraryManager libraryManager;
    private final OrdersManager ordersManager;

    public MainManagerImpl() {
        libraryManager = new LibraryManagerImpl();
        ordersManager = new OrdersManagerImpl();
    }

    // Списать книгу со склада
    @Override
    public void writeOff(Book book, Integer amount, LocalDate saleDate) {
        libraryManager.writeOff(book, amount, saleDate);
    }

    // Создать запрос на книгу
    @Override
    public void addRequest(Book book) {
        ordersManager.addRequest(book);
    }

    // Создать заказ
    @Override
    public void createOrder(Book book, String clientName, LocalDate saleDate) {
        Order newOrder;

        if (libraryManager.isAvailable(book)) {
            newOrder = new Order(book, OrderStatus.COMPLETED, saleDate, clientName);
            writeOff(book, 1, saleDate);
        }
        // Оставить запрос на книгу(в addOrder)
        else {
            newOrder = new Order(book, OrderStatus.NOT_COMPLETED, saleDate, clientName);
        }
        ordersManager.addOrder(newOrder);
    }

    // Отменить заказ
    @Override
    public void cancelOrder(Order order) {
        ordersManager.cancelOrder(order);
    }

    // Изменить статус заказа
    @Override
    public void setOrderStatus(Order order, OrderStatus status) {
        ordersManager.setOrderStatus(order, status);
    }

    // Добавить книгу
    @Override
    public void addBook(Book book, Integer amount, LocalDate addDate) {
        libraryManager.addBook(book, amount, addDate);
        ordersManager.closeRequests(book);
    }

    public LibraryManager getLibraryManager() {
        return libraryManager;
    }

    public OrdersManager getOrdersManager() {
        return ordersManager;
    }

    @Override
    public List<Book> getBooks() {
        return libraryManager.getBooks();
    }

    @Override
    public List<Book> getBooksByAlphabet() {
        return sortBooks(getBooks(), Comparator.comparing(Book::getName));
    }

    @Override
    public List<Book> getBooksByDate() {
        return sortBooks(getBooks(), Comparator.comparing(Book::getPublicationDate));
    }

    @Override
    public List<Book> getBooksByPrice() {
        return sortBooks(getBooks(), Comparator.comparing(Book::getPrice));
    }

    @Override
    public List<Book> getBooksByAvailable() {
        return sortBooks(getBooks(), Comparator.comparing(Book::getStatus));
    }

    @Override
    public List<Book> sortBooks(List<Book> books, Comparator<Book> comparator) {
        books.sort(comparator);
        return books;
    }

    @Override
    public List<Order> getOrders() {
        return ordersManager.getOrders();
    }

    @Override
    public List<Order> getOrdersByDate() {
        return sortOrders(getOrders(), Comparator.comparing(Order::getCompleteDate,
                Comparator.nullsFirst(Comparator.naturalOrder())));
    }

    @Override
    public List<Order> getOrdersByPrice() {
        return sortOrders(getOrders(), Comparator.comparing(Order::getPrice));
    }

    @Override
    public List<Order> getOrdersByStatus() {
        return sortOrders(getOrders(), Comparator.comparing(Order::getStatus));
    }

    @Override
    public List<Order> sortOrders(List<Order> orders, Comparator<Order> comparator) {
        orders.sort(comparator);
        return orders;
    }

    @Override
    public List<Request> getRequests() {
        return ordersManager.getRequests();
    }

    @Override
    public Map<Book, Long> groupRequestsByBook(List<Request> requests) {
        return requests.stream()
                .collect(Collectors.groupingBy(Request::getBook, Collectors.counting()));
    }

    @Override
    public LinkedHashMap<Book, Long> getRequestsByCount() {
        return groupRequestsByBook(getRequests()).entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public LinkedHashMap<Book, Long> getRequestsByPrice() {
        return groupRequestsByBook(getRequests()).entrySet().stream()
                .sorted(Comparator.comparingDouble(entry -> entry.getKey().getPrice()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public List<Order> getCompletedOrdersByDate(LocalDate begin, LocalDate end) {
        return getOrders().stream()
                .filter(Order::isCompleted)
                .filter(order -> order.getCompleteDate().isAfter(begin) && order.getCompleteDate().isBefore(end))
                .sorted(Comparator.comparing(Order::getCompleteDate))
                .toList();
    }

    @Override
    public List<Order> getCompletedOrdersByPrice(LocalDate begin, LocalDate end) {
        return getOrders().stream()
                .filter(Order::isCompleted)
                .filter(order -> order.getCompleteDate().isAfter(begin) && order.getCompleteDate().isBefore(end))
                .sorted(Comparator.comparing(Order::getPrice))
                .toList();
    }

    @Override
    public Double getEarnedSum(LocalDate begin, LocalDate end) {
        return getOrders().stream()
                .filter(Order::isCompleted)
                .filter(order -> order.getCompleteDate().isAfter(begin) && order.getCompleteDate().isBefore(end))
                .mapToDouble(Order::getPrice)
                .sum();
    }

    @Override
    public Long getCountCompletedOrders(LocalDate begin, LocalDate end) {
        return getOrders().stream()
                .filter(Order::isCompleted)
                .filter(order -> order.getCompleteDate().isAfter(begin) && order.getCompleteDate().isBefore(end))
                .count();
    }

    @Override
    public Stream<Book> getStaleBooks() {
        return getBooks().stream()
                .filter(book -> book.getAmount() > 0)
                .filter(book -> (book.getLastSaleDate() == null
                        && Period.between(book.getLastDeliveredDate(), LocalDate.now()).getMonths() >= 6) ||
                        (book.getLastSaleDate() != null
                                && Period.between(book.getLastSaleDate(), LocalDate.now()).getMonths() >= 6));
    }

    @Override
    public List<Book> getStaleBooksByDate() {
        return getStaleBooks()
                .sorted(Comparator.comparing(Book::getLastDeliveredDate))
                .toList();
    }

    @Override
    public List<Book> getStaleBooksByPrice() {
        return getStaleBooks()
                .sorted(Comparator.comparing(Book::getPrice))
                .toList();
    }

    @Override
    public Optional<Order> getOrderDetails(String client, Book book) {
        List<Order> orders = getOrders();
        Order order = new Order(book, client);
        for (Order value : orders) {
            if (value.equals(order)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Book> getBookDetails(Book book) {
        List<Book> books = getBooks();
        for (Book value : books) {
            if (value.equals(book)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
