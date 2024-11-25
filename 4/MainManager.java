public class MainManager implements Libraryable, Orderable{
    private final LibraryManager libraryManager;
    private final OrdersManager ordersManager;

    MainManager(){
        libraryManager = new LibraryManager();
        ordersManager = new OrdersManager();
    }

    // Списать книгу со склада
    @Override
    public void writeOff(Book book){
        libraryManager.writeOff(book);
    }

    // Создать заказ
    void createOrder(Book book){
        Order newOrder;

        if(libraryManager.isAvailable(book)){
            newOrder = new Order(book, OrderStatus.Completed);
            writeOff(book);
        }
        // Оставить запрос на книгу
        else{
            newOrder = new Order(book, OrderStatus.NotCompleted);
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
    public void addBook(Book book){
        libraryManager.addBook(book);
        ordersManager.closeRequests(book);
    }

    public LibraryManager getLibraryManager(){
        return libraryManager;
    }

    public OrdersManager getOrdersManager(){
        return ordersManager;
    }
}
