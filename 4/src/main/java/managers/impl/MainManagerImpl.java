package managers.impl;

import DTO.*;
import annotations.ConfigProperty;
import config.ConfigurationManager;
import lombok.Data;
import managers.MainManager;
import model.impl.*;
import model.Item;
import model.OrderStatus;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class MainManagerImpl implements MainManager {
    @ConfigProperty(propertyName = "book.stale.months", type = int.class)
    private int staleBookMonths;

    @ConfigProperty(propertyName = "mark.orders.completed", type = boolean.class)
    private boolean markOrdersCompleted;

    LibraryManagerImpl libraryManager;
    OrdersManagerImpl ordersManager;

    public MainManagerImpl(LibraryManagerDTO libraryDTO, OrdersManagerDTO ordersDTO) {
        ConfigurationManager.configure(this);

        libraryManager = new LibraryManagerImpl(libraryDTO);
        ordersManager = new OrdersManagerImpl(ordersDTO);
    }

    @Override
    public void writeOff(long id, Integer amount, LocalDate writeOffDate) {
        libraryManager.writeOff(id, amount, writeOffDate);
        updateOrders(writeOffDate);
    }

    @Override
    public void addBook(long id, Integer amount, LocalDate deliveredDate) {
        libraryManager.addBook(id, amount, deliveredDate);
        if (markOrdersCompleted) {
            updateOrders(deliveredDate);
        }
    }

    private void updateOrders(LocalDate updateDate) {
        for (Order order : getOrders()) {
            updateOrder(order, updateDate);
        }
    }

    private void updateOrder(Order order, LocalDate updateDate) {
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

    private void completeOrder(Order order, LocalDate completeDate) {
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompleteDate(completeDate);

        ordersManager.closeRequests(order.getBooks());

        for (Map.Entry<Long, Integer> entry : order.getBooks().entrySet()) {
            libraryManager.writeOff(entry.getKey(), entry.getValue(), completeDate);
        }
    }

    @Override
    public void addRequest(long bookId, int amount) {
        ordersManager.addRequest(bookId, amount);
    }

    @Override
    public void createOrder(Map<Long, Integer> booksIds, String clientName, LocalDate orderDate) {
        Order newOrder = new Order(booksIds, getPrice(booksIds.keySet().stream().toList()),
                OrderStatus.NEW, orderDate, clientName);

        createRequests(newOrder);

        ordersManager.addOrder(newOrder);
    }

    @Override
    public boolean cancelOrder(long orderId) {
        return ordersManager.cancelOrder(orderId);
    }

    @Override
    public boolean setOrderStatus(long orderId, OrderStatus status) {
        return ordersManager.setOrderStatus(orderId, status);
    }

    @Override
    public List<Book> getBooks() {
        return libraryManager.getBooksAsList();
    }

    public List<Book> getBooks(List<Long> booksIds) {
        return libraryManager.getBooks(booksIds);
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

    private List<Book> sortBooks(List<Book> books, Comparator<Book> comparator) {
        List<Book> sortedBooks = new ArrayList<>(books);
        sortedBooks.sort(comparator);
        return sortedBooks;
    }

    @Override
    public List<Order> getOrders() {
        return ordersManager.getOrdersAsList();
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

    private List<Order> sortOrders(List<Order> orders, Comparator<Order> comparator) {
        List<Order> sortedOrders = new ArrayList<>(orders);
        sortedOrders.sort(comparator);
        return sortedOrders;
    }

    @Override
    public List<Request> getRequests() {
        return ordersManager.getRequestsAsList();
    }

    private Map<Book, Long> groupRequestsByBook(List<Request> requests) {
        return requests.stream()
                .map(request -> getBook(request.getBookId()).map(book -> Map.entry(book, request.getBookId())))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.counting()));
    }

    @Override
    public LinkedHashMap<Book, Long> getRequestsByCount() {
        return groupRequestsByBook(getRequests()).entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    @Override
    public LinkedHashMap<Book, Long> getRequestsByPrice() {
        return groupRequestsByBook(getRequests()).entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getPrice()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    @Override
    public List<Order> getCompletedOrdersByDate(LocalDate begin, LocalDate end) {
        return filterCompletedOrdersInRange(begin, end)
                .sorted(Comparator.comparing(Order::getCompleteDate))
                .toList();
    }

    @Override
    public List<Order> getCompletedOrdersByPrice(LocalDate begin, LocalDate end) {
        return filterCompletedOrdersInRange(begin, end)
                .sorted(Comparator.comparing(Order::getPrice))
                .toList();
    }

    @Override
    public Double getEarnedSum(LocalDate begin, LocalDate end) {
        return filterCompletedOrdersInRange(begin, end)
                .mapToDouble(Order::getPrice)
                .sum();
    }

    @Override
    public Long getCountCompletedOrders(LocalDate begin, LocalDate end) {
        return filterCompletedOrdersInRange(begin, end)
                .count();
    }

    private Stream<Order> filterCompletedOrdersInRange(LocalDate begin, LocalDate end) {
        return getOrders().stream()
                .filter(Order::isCompleted)
                .filter(order -> !order.getCompleteDate().isBefore(begin) && !order.getCompleteDate().isAfter(end));
    }

    private Stream<Book> getStaleBooks() {
        return getBooks().stream()
                .filter(book -> book.getAmount() > 0)
                .filter(book -> (book.getLastSaleDate() == null && Period.between(book.getLastDeliveredDate(),
                        LocalDate.now()).getMonths() >= staleBookMonths)
                        || (book.getLastSaleDate() != null && Period.between(book.getLastSaleDate(),
                        LocalDate.now()).getMonths() >= staleBookMonths));
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
    public Optional<Book> getBook(long id) {
        return libraryManager.getMaybeBook(id);
    }

    @Override
    public Optional<Order> getOrder(Long orderId) {
        return ordersManager.getOrder(orderId);
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
        return libraryManager.containsBook(bookId);
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
            // Если книги нет, то создаём запрос на неё
            Optional<Book> book = getBook(entry.getKey());
            if (book.isPresent() && !isAvailable(entry.getKey(), entry.getValue())) {
                addRequest(entry.getKey(), entry.getValue());
                completed = false;
            }
        }
        // Если все книги есть, то мы их списываем и закрываем заказ
        if (completed) {
            completeOrder(order, LocalDate.now());
        }
    }

    @Override
    public Optional<Request> getRequest(long requestId) {
        return ordersManager.getRequest(requestId);
    }

    @Override
    public void importBook(Book book) {
        Optional<Book> findBook = getBook(book.getId());
        if (findBook.isPresent()) {
            findBook.get().copyOf(book);
        } else {
            libraryManager.importBook(book);
        }
    }

    @Override
    public void importOrder(Order order) {
        // Если импортируем заказ на книгу, которой нет в магазине вообще
        if (!containsBooks((order).getBooks().keySet().stream().toList())) {
            throw new IllegalArgumentException("В импортируемом заказе " + order.getId() + " есть несуществующие книги");
        }

        Optional<Order> findOrder = getOrder(order.getId());
        if (findOrder.isPresent()) {
            // При копировании меняется состав заказа, нужно закрыть старые запросы
            ordersManager.closeRequests(findOrder.get().getBooks());
            // Перезаписываем заказ
            findOrder.get().copyOf(order);
        } else {
            ordersManager.addOrder(order);
        }
        // Открываем новые запросы, соответствующие составу импортируемого заказа
        createRequests(order);
    }

    @Override
    public void importRequest(Request request) {
        // Если импортируем запрос на книгу, которой нет в магазине вообще
        if (!containsBook((request).getBookId())) {
            throw new IllegalArgumentException("Запрос " + request.getId() + " - запрос на книгу, которой не существует");
        }
        Optional<Request> findRequest = getRequest(request.getId());
        if (findRequest.isPresent()) {
            throw new IllegalArgumentException("Запрос [" + request.getId() + "] уже есть в магазине");
        } else {
            ordersManager.importRequest(request);
        }
    }

    @Override
    public <T extends Item> void importItem(T item) {
        if (item instanceof Book) {
            importBook((Book) item);
        } else if (item instanceof Order) {
            importOrder((Order) item);
        } else if (item instanceof Request) {
            importRequest((Request) item);
        }
    }
}
