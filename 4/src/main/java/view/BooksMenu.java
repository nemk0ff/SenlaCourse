package view;

import model.impl.Book;

/**
 * {@code BooksMenu} - Интерфейс, расширяющий {@link Menu} и определяющий
 * методы для отображения информации о книгах пользователю.
 */
public interface BooksMenu extends Menu {
  void showSuccessImport(Book book);
}
