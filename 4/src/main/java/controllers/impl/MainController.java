package controllers.impl;

import annotations.ComponentDependency;
import controllers.Action;
import controllers.Controller;
import controllers.impl.importexport.ImportController;
import lombok.NoArgsConstructor;
import manager.MainManagerImpl;
import view.impl.MainMenu;

/**
 * {@code MainController} - Реализует интерфейс {@link Controller} и является главным контроллером
 * приложения, отвечающим за отображение главного меню и перенаправление управления в другие
 * контроллеры (книг, заказов, запросов).
 */
@NoArgsConstructor
public class MainController implements Controller {
  @ComponentDependency
  MainManagerImpl mainManager;
  @ComponentDependency
  MainMenu mainMenu;
  @ComponentDependency
  BooksControllerImpl booksController;
  @ComponentDependency
  OrdersControllerImpl ordersController;
  @ComponentDependency
  RequestsControllerImpl requestsController;

  @Override
  public Action run() {
    ImportController.setMainManager(mainManager);

    mainMenu.showMenu();
    Action action = checkInput();

    while (action == Action.CONTINUE || action == Action.MAIN_MENU) {
      mainMenu.showMenu();
      action = checkInput();
    }
    return Action.EXIT;
  }

  @Override
  public Action checkInput() {
    int answer = (int) getNumberFromConsole();

    return switch (answer) {
      case 1 -> booksController.run();
      case 2 -> ordersController.run();
      case 3 -> requestsController.run();
      case 4 -> Action.EXIT;
      default -> {
        mainMenu.showError("Неизвестная команда");
        yield Action.CONTINUE;
      }
    };
  }

  private long getNumberFromConsole() {
    long answer;
    while (true) {
      try {
        answer = InputUtils.getNumberFromConsole();
        break;
      } catch (NumberFormatException e) {
        mainMenu.showError("Неверный формат, попробуйте еще раз");
      }
    }
    return answer;
  }
}
