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

    default void showGetName() {
        System.out.print("Введите название книги: ");
    }

    default void showGetAuthor() {
        System.out.print("Введите автора книги: ");
    }

    default void showGetAmountBooks() {
        System.out.print("Введите количество книг: ");
    }

    default void showBooks(List<Book> books) {
        System.out.println("========== КНИГИ МАГАЗИНА ==========");
        books.forEach(this::showBook);
        System.out.println("====================================");
    }

    default void showBook(Book book) {
        System.out.println(book.getInfoAbout());
    }
}
