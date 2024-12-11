import Controllers.Impl.MainController;

public class Main {
    public static void main(String[] args) {
        MainController myController = new MainController();
        myController.run();
    }
    // Оптимизирован код менюшек с точки зрения повтора кода, вынесены общие методы в интерфейс Main.
    // Интерфейс BooksMenu пустой, но пока что оставил, мб пригодится
    // Поправлена грамматика сообщений, формат вывода изменен на более удобный
    // Вынесены Impl в подпакеты
    // Добавлен метод containsBook в mainManager
    // В конструктор mainManager добавлено создание трех заказов для удобства тестирования

    // TODO: Посидеть проанализировать код на повторы и возможные оптимизации логики
    // TODO: Добавить возможность заказать две одинаковые книги
}