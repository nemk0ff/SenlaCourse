public interface MainManager{
    void addBook(Book book, Integer amount);
    void writeOff(Book book, Integer amount);
    void cancelOrder(Order order);
    void setOrderStatus(Order order, OrderStatus status);
    void createOrder(Book book);
}
