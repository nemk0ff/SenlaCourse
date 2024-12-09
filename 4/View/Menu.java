package View;

public interface Menu {
    void showMenu();

    default void showInputError() {
        System.out.println("Вы ввели неизвестную команду");
    }

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

    default void showGetPrice() {
        System.out.print("Введите цену книги: ");
    }

    default void showGetPublicationDate() {
        System.out.print("Введите дату публикации книги: ");
    }
}
