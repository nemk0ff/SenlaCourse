package view;

import java.util.List;
import model.Item;
import model.impl.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code Menu} - Интерфейс, определяющий поведение для всех меню в приложении.
 * Предоставляет методы для отображения информации пользователю.
 */
public interface Menu {
  Logger log = LoggerFactory.getLogger(Menu.class);

  default void showMenu() {
  }

  default void showError(String error) {
    log.info("Ошибка: {}", error);
  }

  default void showInputError(String error) {
    System.out.println("Ошибка: " + error);
  }

  default void showSuccess(String success) {
    log.info("Выполнено: {}", success);
  }

  default void showGetId(String message) {
    System.out.println(message);
  }

  default void showGetBookId(int index) {
    System.out.println("Введите id книги №" + (index + 1) + ": ");
  }

  default void showGetAmountBooks(String message) {
    System.out.println(message);
  }

  /**
   * Отображает список книг пользователю.
   *
   * @param books Список книг, которые необходимо отобразить.
   */
  default void showBooks(List<Book> books) {
    System.out.println("========== КНИГИ МАГАЗИНА ==========");
    books.forEach(this::showItem);
    System.out.println("====================================");
  }

  default <T extends Item> void showItem(T item) {
    System.out.println(item.getInfoAbout());
  }

  default void showMessage(String message) {
    System.out.println(message);
  }

  default void showErrorImport() {
    log.info("Не удалось выполнить импорт");
  }
}
