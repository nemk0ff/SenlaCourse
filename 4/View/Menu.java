package View;

import Model.Items.Impl.Book;
import Model.Items.Item;

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
        books.forEach(this::showItem);
        System.out.println("====================================");
    }

    default <T extends Item> void showItem(T item){
        System.out.println(item.getInfoAbout());
    }

    default void showMessage(String message) {
        System.out.println(message);
    }

    default void showImportDataMessage(){
        System.out.println("Доступные данные для импорта:");
    }

    default void showGetImportId(){
        System.out.print("Введите id объекта, который хотите импортировать: ");
    }

    default void showSuccessImport(){
        System.out.println("Импорт выполнен успешно");
    }

    default void showErrorImport(){
        System.out.println("Не удалось выполнить импорт");
    }
}
