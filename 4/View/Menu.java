package View;

import Model.Book;

import java.util.List;

public interface Menu {
    void showMenu();

    default void showError(String error) {
        System.out.println("Ошибка: " + error);
    }

    default void showSuccess(String success) {
        System.out.println("Выполнено: " + success);
    }

    default void showGetId(String message) {
        System.out.print(message);
    }

    default void showGetBookId(int index) {
        System.out.print("Введите id книги №" + (index + 1) + ": ");
    }

    default void showGetAmountBooks(String message) {
        System.out.print(message);
    }

    default void showBooks(List<Book> books) {
        System.out.println("========== КНИГИ МАГАЗИНА ==========");
        books.forEach(this::showBook);
        System.out.println("====================================");
    }

    default void showBook(Book book) {
        System.out.println(book.getInfoAbout());
    }

    default void showMessage(String message) {
        System.out.println(message);
    }
}
