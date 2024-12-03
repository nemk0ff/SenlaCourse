import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

public class MainManagerImpl implements MainManager{
    private final LibraryManager libraryManager;
    private final OrdersManager ordersManager;

    MainManagerImpl(){
        libraryManager = new LibraryManagerImpl();
        ordersManager = new OrdersManagerImpl();
    }

    // Списать книгу со склада
    @Override
    public void writeOff(Book book, Integer amount, LocalDate saleDate){
        libraryManager.writeOff(book, amount, saleDate);
    }

    // Создать заказ
    @Override
    public void createOrder(Book book, String clientName, LocalDate saleDate){
        Order newOrder;

        if(libraryManager.isAvailable(book)){
            newOrder = new Order(book, OrderStatus.Completed, LocalDate.of(1998, 12, 12), clientName);
            writeOff(book, 1, saleDate);
        }
        // Оставить запрос на книгу(в addOrder)
        else{
            newOrder = new Order(book, OrderStatus.NotCompleted, LocalDate.of(1998, 12, 12), clientName);
        }
        ordersManager.addOrder(newOrder);
    }

    // Отменить заказ
    @Override
    public void cancelOrder(Order order){
        ordersManager.cancelOrder(order);
    }

    // Изменить статус заказа
    @Override
    public void setOrderStatus(Order order, OrderStatus status){
        ordersManager.setOrderStatus(order, status);
    }

    // Добавить книгу
    @Override
    public void addBook(Book book, Integer amount){
        libraryManager.addBook(book, amount);
        ordersManager.closeRequests(book);
    }

    public LibraryManager getLibraryManager(){
        return libraryManager;
    }

    public OrdersManager getOrdersManager(){
        return ordersManager;
    }

    @Override
    public List<Book> getBooks(){
        return libraryManager.getBooks();
    }

    @Override
    public List<Book> getBooksByAlphabet(){
       List<Book> sortedBooks = libraryManager.getBooks();
       sortedBooks.sort(Comparator.comparing(Book::getName));
       return sortedBooks;
    }
    @Override
    public List<Book> getBooksByDate(){
        List<Book> sortedBooks = libraryManager.getBooks();
        sortedBooks.sort(Comparator.comparing(Book::getPublicationDate));
        return sortedBooks;
    }
    @Override
    public List<Book> getBooksByPrice(){
        List<Book> sortedBooks = libraryManager.getBooks();
        sortedBooks.sort(Comparator.comparing(Book::getPrice));
        return sortedBooks;
    }
    @Override
    public List<Book> getBooksByAvailable(){
        List<Book> sortedBooks = libraryManager.getBooks();
        sortedBooks.sort(Comparator.comparing(Book::getStatus));
        return sortedBooks;
    }


    @Override
    public List<Order> getOrders(){
        return ordersManager.getOrders();
    }
    @Override
    public List<Request> getRequests(){
        return ordersManager.getRequests();
    }

    @Override
    public List<Order> getOrdersByDate(){
        List<Order> sortedOrders = ordersManager.getOrders();
        sortedOrders.sort(Comparator.comparing(Order::getCompleteDate, Comparator.nullsFirst(Comparator.naturalOrder())));
        return sortedOrders;
    }
    @Override
    public List<Order> getOrdersByPrice(){
        List<Order> sortedOrders = ordersManager.getOrders();
        sortedOrders.sort(Comparator.comparing(Order::getPrice));
        return sortedOrders;
    }
    @Override
    public List<Order> getOrdersByStatus(){
        List<Order> sortedOrders = ordersManager.getOrders();
        sortedOrders.sort(Comparator.comparing(Order::getStatus));
        return sortedOrders;
    }

    @Override
    public SortedMap<Book, Long> getRequestsByCount(){
        return ordersManager.getRequests()
                .stream()
                .collect(Collectors.groupingBy(Request::getBook, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, TreeMap::new));
    }
    @Override
    public SortedMap<Book, Long> getRequestsByPrice(){
        return ordersManager.getRequests()
                .stream()
                .collect(Collectors.groupingBy(Request::getBook, Collectors.counting()))
                .entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getPrice()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (first, second) -> first, TreeMap::new));
    }

    @Override
    public List<Order> getCompletedOrdersByDate(LocalDate begin, LocalDate end){
        return ordersManager.getOrders()
                .stream()
                .filter(Order::isCompleted)
                .filter(order -> order.getCompleteDate().isAfter(begin) && order.getCompleteDate().isBefore(end))
                .sorted(Comparator.comparing(Order::getCompleteDate))
                .toList();
    }

    @Override
    public List<Order> getCompletedOrdersByPrice(LocalDate begin, LocalDate end){
        return ordersManager.getOrders()
                .stream()
                .filter(Order::isCompleted)
                .filter(order -> order.getCompleteDate().isAfter(begin) && order.getCompleteDate().isBefore(end))
                .sorted(Comparator.comparing(Order::getPrice))
                .toList();
    }

    @Override
    public Integer getEarnedSum(LocalDate begin, LocalDate end){
        return ordersManager.getOrders()
                .stream()
                .filter(Order::isCompleted)
                .filter(order -> order.getCompleteDate().isAfter(begin) && order.getCompleteDate().isBefore(end))
                .mapToInt(Order::getPrice)
                .sum();
    }

    @Override
    public Long getCountCompletedOrders(LocalDate begin, LocalDate end){
        return ordersManager.getOrders()
                .stream()
                .filter(Order::isCompleted)
                .filter(order -> order.getCompleteDate().isAfter(begin) && order.getCompleteDate().isBefore(end))
                .count();
    }

    @Override
    public void showOrderDetails(String client, Book book){
        List<Order> orders = ordersManager.getOrders();
        Order order = new Order(book, client);
        for (Order value : orders) {
            if (value.equals(order)) {
                System.out.println(value.getInfoAbout());
                System.out.println(value.getBook().getInfoAbout());
                return;
            }
        }
        System.out.println("Этого заказа нет в магазине");
    }

    @Override
    public List<Book> getStaleBooksByDate(){
        return libraryManager.getBooks()
                .stream()
                .filter(book -> book.getLastSaleDate() != null)
                .filter(book -> book.getLastDeliveredDate() != null)
                .filter(book -> Period.between(book.getLastSaleDate(), LocalDate.now()).getMonths() >= 6)
                .sorted(Comparator.comparing(Book::getLastDeliveredDate))
                .toList();
    }
    @Override
    public List<Book> getStaleBooksByPrice(){
        return libraryManager.getBooks()
                .stream()
                .filter(book -> book.getLastSaleDate() != null)
                .filter(book -> Period.between(book.getLastSaleDate(), LocalDate.now()).getMonths() >= 6)
                .sorted(Comparator.comparing(Book::getPrice))
                .toList();
    }


    @Override
    public void showBookDetails(Book book){
        List<Book> books = libraryManager.getBooks();
        for (Book value : books) {
            if (value.equals(book)) {
                System.out.println(value.getInfoAbout());
                return;
            }
        }
        System.out.println("Этой книги нет в магазине");
    }
}
