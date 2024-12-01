import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MainManagerImpl myManager = new MainManagerImpl();

        // Получаем списки книг с разной сортировкой
        //testBooksGetters(myManager);

        // Получаем списки заказов с разной сортировкой
        //testOrdersGetters(myManager);

        // Получаем списко запросов на книгу (сортировка по количеству запросов, по цене)
        // ---

        // Получаем список выполненных заказов за период времени (сортировка по дате, цене)
        //testCompletedOrders(myManager);

        // Получаем сумму заработанных средств за период времени
        System.out.println("Сумма заработанных средств за период времени: ");
        System.out.println(myManager.getEarnedSum(LocalDate.of(2024, 10, 1),
                LocalDate.of(2024,12,31)));

        // Количество выполненных заказов за период времени
        System.out.println();
        System.out.println("Количество выполненных заказов за период времени: ");
        System.out.println(myManager.getCountCompletedOrders(LocalDate.of(2024, 10, 1),
                LocalDate.of(2024,12,31)));

        // Список залежавшихся книг, не проданных более чем 6 месяцев (по дате поступления, цене)
        // ---

        // Посмотреть детали заказа
        System.out.println();
        System.out.println("Посмотреть детали заказа: ");
        myManager.showOrderDetails("Степан Краснов", "Дубровский", "А.С.Пушкин");

        // Посмотреть описание книги
        System.out.println();
        System.out.println("Посмотреть описание книги: ");
        myManager.showBookDetails("Ревизор", "Н.В.Гоголь");
    }

    public static void testBooksGetters(MainManagerImpl myManager){
        System.out.println("На складе есть следующие книги:");
        List<Book> books = myManager.getBooks();
        for (Book book : books) {
            System.out.println(book.getInfoAbout());
        }
        System.out.println("---------------------------------------");

        System.out.println("Сортировка книг по алфавиту");
        books = myManager.getBooksByAlphabet();
        for (Book book : books) {
            System.out.println(book.getInfoAbout());
        }
        System.out.println("---------------------------------------");

        System.out.println("Сортировка книг по дате издания");
        books = myManager.getBooksByDate();
        for (Book book : books) {
            System.out.println(book.getInfoAbout());
        }
        System.out.println("---------------------------------------");

        System.out.println("Сортировка книг по цене");
        books = myManager.getBooksByPrice();
        for (Book book : books) {
            System.out.println(book.getInfoAbout());
        }
        System.out.println("---------------------------------------");

        System.out.println("Сортировка книг по наличию");
        books = myManager.getBooksByAvailable();
        for (Book book : books) {
            System.out.println(book.getInfoAbout());
        }
        System.out.println("---------------------------------------");
    }

    public static void testOrdersGetters(MainManagerImpl myManager){
        System.out.println("В магазине есть следующие заказы:");
        List<Order> orders = myManager.getOrders();
        for (Order order : orders) {
            System.out.println(order.getInfoAbout());
            System.out.println(order.getInfoAboutBook());
            System.out.println();
        }
        System.out.println("---------------------------------------");

        System.out.println("Сортировка заказов по дате выполнения:");
        orders = myManager.getOrdersByDate();
        for (Order order : orders) {
            System.out.println(order.getInfoAbout());
            System.out.println(order.getInfoAboutBook());
            System.out.println();
        }
        System.out.println("---------------------------------------");

        System.out.println("Сортировка заказов по цене:");
        orders = myManager.getOrdersByPrice();
        for (Order order : orders) {
            System.out.println(order.getInfoAbout());
            System.out.println(order.getInfoAboutBook());
            System.out.println();
        }
        System.out.println("---------------------------------------");

        System.out.println("Сортировка заказов по статусу:");
        orders = myManager.getOrdersByStatus();
        for (Order order : orders) {
            System.out.println(order.getInfoAbout());
            System.out.println(order.getInfoAboutBook());
            System.out.println();
        }
        System.out.println("---------------------------------------");
    }

    public static void testCompletedOrders(MainManagerImpl myManager){
        List<Order> orders = myManager.getCompletedOrdersByDate(LocalDate.of(2024, 10, 1),
                LocalDate.of(2024,12,31));

        System.out.println("    Заказы по книге, сортировка по дате заказа:");
        for (Order order : orders) {
            System.out.println(order.getInfoAbout());
            System.out.println(order.getInfoAboutBook());
            System.out.println();
        }
        System.out.println("---------------------------------------");

        orders = myManager.getCompletedOrdersByPrice(LocalDate.of(2024, 10, 1),
                LocalDate.of(2024,12,31));

        System.out.println("    Заказы по книге, сортировка по цене заказа:");
        for (Order order : orders) {
            System.out.println(order.getInfoAbout());
            System.out.println(order.getInfoAboutBook());
            System.out.println();
        }
        System.out.println("---------------------------------------");
    }


    public static void printAbout(MainManagerImpl manager){
        System.out.println("    BOOKS: ");

        List<Book> books = manager.getLibraryManager().getBooks();
        for (Book book : books) {
            System.out.println(book.getInfoAbout());
            System.out.println();
        }

        System.out.println("    ORDERS:");

        List<Order> orders = manager.getOrdersManager().getOrders();
        for (Order order : orders) {
            System.out.println(order.getInfoAbout());
            System.out.println(order.getBook().getInfoAbout());
            System.out.println();
        }

        System.out.println("---------------------------------------");
    }
}