package controllers;

/**
 * {@code OrdersController} - Интерфейс, определяющий поведение для контроллера, управляющего
 * операциями, связанными с запросами.
 */
public interface RequestsController extends Controller {
  void createRequest();

  void getRequestsByCount();

  void getRequestsByPrice();

  void getAllRequests();

  void exportRequest();

  void importRequest();

  void importAll();

  void exportAll();
}
