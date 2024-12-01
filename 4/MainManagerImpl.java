import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
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
    public void writeOff(Book book, Integer amount){
        libraryManager.writeOff(book, amount);
    }

    // Создать заказ
    @Override
    public void createOrder(Book book){
        Order newOrder;

        if(libraryManager.isAvailable(book)){
            newOrder = new Order(book, OrderStatus.Completed, LocalDate.of(1998, 12, 12), "Ivanov");
            writeOff(book, 1);
        }
        // Оставить запрос на книгу
        else{
            newOrder = new Order(book, OrderStatus.NotCompleted, LocalDate.of(1998, 12, 12), "Petrov");
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
    public List<Order> getCompletedOrdersByDate(LocalDate begin, LocalDate end){
        List<Order> orders = ordersManager.getOrders()
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.Completed)
                .filter(order -> order.getCompleteDate().isAfter(begin) && order.getCompleteDate().isBefore(end))
                .sorted(Comparator.comparing(Order::getCompleteDate))
                .toList();
        return orders;
    }
    @Override
    public List<Order> getCompletedOrdersByPrice(LocalDate begin, LocalDate end){
        List<Order> orders = ordersManager.getOrders()
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.Completed)
                .filter(order -> order.getCompleteDate().isAfter(begin) && order.getCompleteDate().isBefore(end))
                .sorted(Comparator.comparing(Order::getPrice))
                .toList();
        return orders;
    }

    @Override
    public Integer getEarnedSum(LocalDate begin, LocalDate end){
        return ordersManager.getOrders()
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.Completed)
                .filter(order -> order.getCompleteDate().isAfter(begin) && order.getCompleteDate().isBefore(end))
                .mapToInt(Order::getPrice)
                .sum();
    }

    @Override
    public Integer getCountCompletedOrders(LocalDate begin, LocalDate end){
        return ordersManager.getOrders()
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.Completed)
                .filter(order -> order.getCompleteDate().isAfter(begin) && order.getCompleteDate().isBefore(end))
                .toList()
                .size();
    }

    @Override
    public void showOrderDetails(String client, String bookName, String author){
        List<Order> orders = ordersManager.getOrders();
        Order order = new Order(new Book("Мёртвые души", "Н.В.Гоголь"),"Григорий Лепс");
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
    public void showBookDetails(String bookName, String author){
        List<Book> books = libraryManager.getBooks();
        Book book = new Book("Мёртвые души", "Н.В.Гоголь");
        for (Book value : books) {
            if (value.equals(book)) {
                System.out.println(value.getInfoAbout());
                return;
            }
        }
        System.out.println("Этой книги нет в магазине");
    }
}
