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

        List<Book> books = getBooks();

        Book book1 = books.getLast();
        Book book2 = books.get(books.size() - 2);
        Book book3 = books.get(books.size() - 3);
        Book book4 = books.get(books.size() - 5);

        createOrder(Map.of(book1.getId(), 2, book2.getId(), 1), "Сергей Юртаев", LocalDate.now());
        createOrder(Map.of(book1.getId(), 1, book4.getId(), 2), "Екатерина Нуякшева", LocalDate.now());
        createOrder(Map.of(book2.getId(), 1, book3.getId(), 2), "Антон Некрасов", LocalDate.now());
    }

    @Override
    public void writeOff(long id, Integer amount, LocalDate writeOffDate) {
        libraryManager.writeOff(id, amount, writeOffDate);
        updateOrders(writeOffDate);
    }

    @Override
    public void addBook(long id, Integer amount, LocalDate deliveredDate) {
        libraryManager.addBook(id, amount, deliveredDate);
        updateOrders(deliveredDate);
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
                if (getBook(entry.getKey()).getAmount() < entry.getValue()) {
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
    public void addRequest(long bookId) {
        ordersManager.addRequest(bookId);
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
        return libraryManager.getBooks();
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

    private List<Order> sortOrders(List<Order> orders, Comparator<Order> comparator) {
        orders.sort(comparator);
        return orders;
    }

    @Override
    public List<Request> getRequests() {
        return ordersManager.getRequests()
                .stream()
                .filter(request -> request.getStatus() == RequestStatus.OPEN)
                .toList();
    }

    private Map<Long, Long> groupRequestsByBook(List<Request> requests) {
        return requests.stream()
                .collect(Collectors.groupingBy(Request::getBook, Collectors.counting()));
    }

    @Override
    public LinkedHashMap<Book, Long> getRequestsByCount() {
        return groupRequestsByBook(getRequests()).entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        entry -> getBook(entry.getKey()),
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    @Override
    public LinkedHashMap<Book, Long> getRequestsByPrice() {
        return groupRequestsByBook(getRequests()).entrySet().stream()
                .sorted(Comparator.comparingDouble(entry -> getBook(entry.getKey()).getPrice()))
                .collect(Collectors.toMap(
                        entry -> getBook(entry.getKey()),
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
    public Optional<Book> getMaybeBook(long id) {
        return libraryManager.getMaybeBook(id);
    }

    @Override
    public Optional<Order> getMaybeOrder(Long orderId) {
        List<Order> orders = getOrders();
        for (Order order : orders) {
            if (order.getId() == orderId) {
                return Optional.of(order);
            }
        }
        return Optional.empty();
    }

    public Order getOrder(Long orderId) {
        return ordersManager.getOrder(orderId);
    }


    @Override
    public Book getBook(long id) {
        return libraryManager.getBook(id);
    }

    @Override
    public boolean containsBook(long bookId) {
        for (Book book : getBooks()) {
            if (bookId == book.getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsOrder(long orderId) {
        for (Order order : getOrders()) {
            if (orderId == order.getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsRequest(long requestId) {
        for (Request request : getRequests()) {
            if (requestId == request.getId()) {
                return true;
            }
        }
        return false;
    }

    public double getPrice(List<Long> booksIds) {
        return getBooks(booksIds).stream().mapToDouble(Book::getPrice).sum();
    }

    public boolean isAvailable(long bookId, int requestAmount) {
        for (Book book : getBooks()) {
            if (bookId == book.getId()) {
                return book.getAmount() >= requestAmount;
            }
        }
        return false;
    }

    @Override
    public void importBook(Book importBook) {
        Optional<Book> findBook = getMaybeBook(importBook.getId());
        if (findBook.isPresent()) {
            findBook.get().copyOf(importBook);
        } else {
            libraryManager.importBook(importBook);
        }
    }

    @Override
    public void importOrder(Order importOrder) {
        Optional<Order> findOrder = getMaybeOrder(importOrder.getId());
        if (findOrder.isPresent()) {
            // При копировании меняется состав заказа, нужно закрыть старые запросы
            ordersManager.closeRequests(findOrder.get().getBooks());
            // Перезаписываем заказ
            findOrder.get().copyOf(importOrder);
        } else {
            ordersManager.addOrder(importOrder);
        }
        // Открываем новые запросы, соответствующие составу импортируемого заказа
        createRequests(importOrder);
    }

    @Override
    public void createRequests(Order order) {
        boolean completed = true;
        for (Map.Entry<Long, Integer> entry : order.getBooks().entrySet()) {
            // Если книги нет, то создаём запрос на неё
            if (!isAvailable(entry.getKey(), entry.getValue())) {
                int missingBooks = entry.getValue() - getBook(entry.getKey()).getAmount();
                // Если не хватает пяти одинаковых книг, то открываем на неё 5 запросов
                for (int i = 0; i < missingBooks; i++) {
                    addRequest(entry.getKey());
                }
                completed = false;
            }
        }
        // Если все книги есть, то мы их списываем и закрываем заказ
        if (completed) {
            completeOrder(order, LocalDate.now());
        }
    }

    @Override
    public Optional<Request> getMaybeRequest(long requestId) {
        List<Request> requests = getRequests();
        for (Request request : requests) {
            if (request.getId() == requestId) {
                return Optional.of(request);
            }
        }
        return Optional.empty();
    }

    @Override
    public Request getRequest(long requestId) {
        return ordersManager.getRequest(requestId);
    }

    @Override
    public void importRequest(Request importRequest) {
        Optional<Request> findRequest = getMaybeRequest(importRequest.getId());
        if (findRequest.isPresent()) {
            findRequest.get().copyOf(importRequest);
        } else {
            ordersManager.importRequest(importRequest);
        }
    }
}
