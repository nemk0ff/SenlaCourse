import Controllers.Impl.MainController;

public class Main {
    public static void main(String[] args) {
        MainController myController = new MainController();
        myController.run();
    }
    // Удалён дубликат книги "Дубровский"
    // Исправлена логика закрытия запросов
    // Добавлено сообщение о вводе количества книг
    // Добавлен вывод списка всех книг при addBook, writeOff, showBookDetails


    // TODO: Посидеть проанализировать код на повторы и возможные оптимизации логики
    // TODO: Добавить возможность заказать две одинаковые книги
}