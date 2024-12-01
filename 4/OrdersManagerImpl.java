import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdersManagerImpl implements OrdersManager{
    private final List<Order> orders;

    public OrdersManagerImpl() {
        orders = new ArrayList<>();

        orders.add(new Order(new Book("Война и Мир", "Л.Н.Толстой", 100, 1869, BookStatus.Ordered),
                OrderStatus.Completed, LocalDate.of(2024, 5, 12), "Максим Иванов"));
        orders.add(new Order(new Book("Анна Каренина", "Л.Н.Толстой", 150, 1877, BookStatus.Ordered),
                OrderStatus.Completed, LocalDate.of(2024, 5, 9), "Илья Петров"));
        orders.add(new Order(new Book("Капитанская почка", "А.С.Пупкин", 200, 2024, BookStatus.Ordered),
                OrderStatus.NotCompleted, null, "Игорь Дроздов"));
        orders.add(new Order(new Book("Мёртвые души", "Н.В.Гоголь", 350, 1842, BookStatus.Ordered),
                OrderStatus.NotCompleted, null, "Екатерина Смирнова"));
        orders.add(new Order(new Book("Гарри Поттер и узник Азкабана", "Дж.К.Роулинг", 450, 1999, BookStatus.Ordered),
                OrderStatus.NotCompleted, null, "Дмитрий Розанов"));

        orders.add(new Order(new Book("Введение в алгебру", "А.И.Кострикин", 450, 2001, BookStatus.Ordered),
                OrderStatus.NotCompleted, null, "Алина Петрова"));
        orders.add(new Order(new Book("Преступление и наказание", "Ф.М.Достоевский", 200, 1866, BookStatus.Ordered),
                OrderStatus.Completed, LocalDate.of(2024, 11, 23), "Александр Бессонов"));
        orders.add(new Order(new Book("Дубровский", "А.С.Пушкин", 450, 1833, BookStatus.Ordered),
                OrderStatus.Completed, LocalDate.of(2024, 11, 1), "Степан Краснов"));
        orders.add(new Order(new Book("Мёртвые души", "Н.В.Гоголь", 350, 1842, BookStatus.Ordered),
                OrderStatus.NotCompleted, null, "Григорий Лепс"));
        orders.add(new Order(new Book("Идиот", "Ф.М.Достоевский", 350, 1868, BookStatus.Ordered),
                OrderStatus.Completed, LocalDate.of(2024, 11, 30), "Игорь Некрасов"));

    }

    // Закрыть запросы по книге
    @Override
    public void closeRequests(Book book){
        for (Order order : orders) {
            if (order.getBook().equals(book) && order.getStatus() == OrderStatus.NotCompleted) {
                order.setStatus(OrderStatus.Completed);
            }
        }
    }

    // Добавить заказ
    @Override
    public void addOrder(Order order){
        orders.add(order);
    }

    // Отменить заказ
    @Override
    public void cancelOrder(Order order){
        orders.remove(order);
    }

    // Изменить статус заказа
    @Override
    public void setOrderStatus(Order order, OrderStatus status){
        for (Order value : orders) {
            if (value.equals(order)) {
                value.setStatus(status);
            }
        }
    }

    @Override
    public List<Order> getOrders(){
        return orders;
    }
}
