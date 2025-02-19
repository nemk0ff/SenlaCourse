package view;

import java.util.List;
import java.util.Map;
import model.impl.Book;
import model.impl.Request;

/**
 * {@code RequestsMenu} - Интерфейс, расширяющий {@link Menu} и определяющий
 * методы для отображения информации о запросах пользователю.
 */
public interface RequestsMenu extends Menu {
  void showRequests(Map<Book, Long> requests);

  void showRequests(List<Request> requests);

  void showSuccessImport(Request request);
}
