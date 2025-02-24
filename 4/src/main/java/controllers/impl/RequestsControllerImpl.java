package controllers.impl;

import annotations.ComponentDependency;
import constants.FileConstants;
import controllers.Action;
import controllers.RequestsController;
import controllers.impl.importexport.ExportController;
import controllers.impl.importexport.ImportController;
import java.util.List;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manager.MainManagerImpl;
import model.impl.Book;
import model.impl.Request;
import view.impl.RequestsMenuImpl;

/**
 * {@code RequestsControllerImpl} - Реализация интерфейса {@link RequestsController},
 * представляющая собой контроллер для управления запросами.
 */
@Slf4j
@NoArgsConstructor
public class RequestsControllerImpl implements RequestsController {
  @ComponentDependency
  MainManagerImpl mainManager;
  @ComponentDependency
  RequestsMenuImpl requestsMenu;

  @Override
  public Action run() {
    requestsMenu.showMenu();
    Action action = checkInput();

    while (action == Action.CONTINUE) {
      requestsMenu.showMenu();
      action = checkInput();
    }

    return action;
  }

  @Override
  public Action checkInput() {
    int answer = (int) getNumberFromConsole();

    return switch (answer) {
      case 1:
        createRequest();
        yield Action.CONTINUE;
      case 2:
        getRequestsByCount();
        yield Action.CONTINUE;
      case 3:
        getRequestsByPrice();
        yield Action.CONTINUE;
      case 4:
        importRequest();
        yield Action.CONTINUE;
      case 5:
        exportRequest();
        yield Action.CONTINUE;
      case 6:
        importAll();
        yield Action.CONTINUE;
      case 7:
        exportAll();
        yield Action.CONTINUE;
      case 8:
        getAllRequests();
        yield Action.CONTINUE;
      case 9:
        yield Action.MAIN_MENU;
      case 10:
        yield Action.EXIT;
      default:
        requestsMenu.showError("Неизвестная команда");
        yield Action.CONTINUE;
    };
  }

  @Override
  public void createRequest() {
    try {
      requestsMenu.showBooks(mainManager.getAllBooks());
      requestsMenu.showGetId("Введите id книги, на которую хотите создать запрос: ");
      Optional<Book> book = mainManager.getBook(getNumberFromConsole());
      if (book.isEmpty()) {
        throw new IllegalArgumentException("Книга не найдена в магазине");
      }
      requestsMenu.showMessage("На сколько книг создать запрос? ");
      int amount = (int) getNumberFromConsole();
      long id = mainManager.createRequest(book.get(), amount);
      requestsMenu.showSuccess("Запрос [" + id + "] на " + amount + " книг ["
          + book.get().getId() + "] успешно" + " создан");
    } catch (Exception e) {
      log.warn("При создании запроса произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void getRequestsByCount() {
    try {
      requestsMenu.showRequests(mainManager.getRequestsByCount());
    } catch (Exception e) {
      log.warn("При получении запросов произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void getRequestsByPrice() {
    try {
      requestsMenu.showRequests(mainManager.getRequestsByPrice());
    } catch (Exception e) {
      log.warn("При получении запросов произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void getAllRequests() {
    try {
      requestsMenu.showRequests(mainManager.getRequests());
    } catch (Exception e) {
      log.warn("При получении запросов произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void importRequest() {
    try {
      Optional<Request> findRequest = ImportController.importItem(FileConstants.IMPORT_REQUEST_PATH,
          ImportController::requestParser);
      if (findRequest.isPresent()) {
        mainManager.importItem(findRequest.get());
        requestsMenu.showSuccessImport(findRequest.get());
        findRequest.ifPresent(requestsMenu::showItem);
      } else {
        requestsMenu.showErrorImport();
      }
    } catch (Exception e) {
      log.warn("При импорте запроса произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void exportRequest() {
    try {
      requestsMenu.showRequests(mainManager.getRequests());
      requestsMenu.showGetId("Введите id запроса, который хотите экспортировать: ");
      long exportId = getNumberFromConsole();
      Request exportRequest = getExportRequest(exportId);

      ExportController.exportItemToFile(exportRequest, FileConstants.EXPORT_REQUEST_PATH,
          FileConstants.REQUEST_HEADER);
    } catch (Exception e) {
      log.warn("При экспорте запроса произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void importAll() {
    try {
      List<Request> importedRequests = ImportController.importAllItemsFromFile(
          FileConstants.IMPORT_REQUEST_PATH, ImportController::requestParser);

      if (!importedRequests.isEmpty()) {
        requestsMenu.showMessage("Результат импортирования:");
        for (Request importedRequest : importedRequests) {
          mainManager.importItem(importedRequest);
        }
      } else {
        requestsMenu.showError("Импорт не выполнен: запросы для импорта не найдены");
      }
    } catch (Exception e) {
      log.warn("При импорте запросов произошла ошибка: {}", e.getMessage(), e);
    }
  }

  @Override
  public void exportAll() {
    try {
      ExportController.exportAll(mainManager.getRequests(),
          FileConstants.EXPORT_REQUEST_PATH, FileConstants.REQUEST_HEADER);
    } catch (Exception e) {
      log.warn("При экспорте запросов произошла ошибка: {}", e.getMessage(), e);
    }
  }

  private Request getExportRequest(long id) {
    Optional<Request> request = mainManager.getRequest(id);
    if (request.isPresent()) {
      return request.get();
    }
    throw new IllegalArgumentException("Запрос №" + id + " не найден");
  }

  private long getNumberFromConsole() {
    long answer;
    while (true) {
      try {
        answer = InputUtils.getNumberFromConsole();
        break;
      } catch (NumberFormatException e) {
        requestsMenu.showError("Неверный формат, попробуйте еще раз");
      }
    }
    return answer;
  }
}
