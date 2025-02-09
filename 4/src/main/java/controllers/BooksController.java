package controllers;

/**
 * {@code OrdersController} - Интерфейс, определяющий поведение для контроллера, управляющего
 * операциями, связанными с книгами.
 */
public interface BooksController extends Controller {
  void addBook();

  void writeOff();

  void showBookDetails();

  void getBooksByName();

  void getBooksByDate();

  void getBooksByPrice();

  void getBooksByAvailable();

  void getStaleBooksByDate();

  void getStaleBooksByPrice();

  void importAll();

  void exportAll();

  void importBook();

  void exportBook();
}
