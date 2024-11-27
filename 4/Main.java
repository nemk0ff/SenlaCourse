import java.util.List;

public class Main {
    public static void main(String[] args) {
        MainManagerImpl myManager = new MainManagerImpl();
        printAbout(myManager);

        // Списать книгу со склада
        System.out.println("Списать книгу со склада: Дубровский");
        myManager.writeOff(new Book("Дубровский", "А.С.Пушкин"), 1);
        printAbout(myManager);

        // Создать заказ
        System.out.println("Создать заказ: Капитанская дочка");
        myManager.createOrder(new Book("Капитанская дочка", "А.С.Пушкин"));
        printAbout(myManager);

        // Оставить запрос на книгу
        System.out.println("Оставить запрос на книгу: Капитанская почка");
        myManager.createOrder(new Book("Капитанская почка", "А.С.Пупкин"));
        printAbout(myManager);

        // Изменить статус заказа
        System.out.println("Изменить статус заказа: Капитанская дочка");
        Order testOrder1 = new Order(new Book("Капитанская дочка", "А.С.Пушкин"), OrderStatus.Completed);
        myManager.setOrderStatus(testOrder1, OrderStatus.NotCompleted);
        printAbout(myManager);

        // Отменить заказ
        System.out.println("Отменить заказ: Капитанская почка");
        Order testOrder2 = new Order(new Book("Капитанская почка", "А.С.Пупкин"), OrderStatus.NotCompleted);
        myManager.cancelOrder(testOrder2);
        printAbout(myManager);

        // Добавить книгу на склад
        System.out.println("Добавить книгу на склад: Капитанская дочка");
        myManager.addBook(new Book("Капитанская дочка", "А.С.Пушкин"), 1);
        printAbout(myManager);
    }

    public static void printAbout(MainManagerImpl manager){
        System.out.println("BOOKS:");
        List<Book> books = manager.getLibraryManager().getBooks();
        for (Book book : books) {
            System.out.println(book.getName() + " - " + book.getAuthor() + " - " + book.getStatus());
        }

        System.out.println("ORDERS:");
        List<Order> orders = manager.getOrdersManager().getOrders();
        for (Order order : orders) {
            System.out.println(order.getBook().getName() + " - " + order.getBook().getAuthor() + " - " + order.getStatus());
        }
        System.out.println("---------------------------------------");
    }
}