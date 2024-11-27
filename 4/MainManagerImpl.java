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
            newOrder = new Order(book, OrderStatus.Completed);
            writeOff(book, 1);
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
}
